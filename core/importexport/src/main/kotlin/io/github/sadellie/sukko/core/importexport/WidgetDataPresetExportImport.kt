package io.github.sadellie.sukko.core.importexport

import android.content.Context
import androidx.core.net.toUri
import io.github.sadellie.sukko.core.common.SCHEMA_VERSION
import io.github.sadellie.sukko.core.common.filesPath
import io.github.sadellie.sukko.core.common.uri
import io.github.sadellie.sukko.core.data.IconPackCustomRepository
import io.github.sadellie.sukko.core.data.WidgetDataPresetCustomRepository
import io.github.sadellie.sukko.core.fontfiles.FontFile
import io.github.sadellie.sukko.core.fontfiles.FontFileCustomRepository
import io.github.sadellie.sukko.core.iconfiles.IconPack
import io.github.sadellie.sukko.core.model.ImportingFontFile
import io.github.sadellie.sukko.core.model.ImportingIconPack
import io.github.sadellie.sukko.core.model.ImportingIconPackAction
import io.github.sadellie.sukko.core.model.ImportingWidgetDataPreset
import io.github.sadellie.sukko.core.model.WidgetDataPreset
import io.github.sadellie.sukko.core.model.basic.ImageUriSource
import io.github.sadellie.sukko.core.model.basic.TextStyleSource
import io.github.sadellie.sukko.core.model.layer.ColdImageLayer
import io.github.sadellie.sukko.core.model.layer.ColdTextLayer
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okio.Path
import okio.Path.Companion.toOkioPath
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class WidgetDataPresetExportImport(
  private val widgetDataPresetCustomRepository: WidgetDataPresetCustomRepository,
  private val iconPackCustomRepository: IconPackCustomRepository,
  private val fontFileCustomRepository: FontFileCustomRepository,
  private val context: Context,
) {
  /**
   * Creates an archive
   *
   * ```txt
   * widget_data_preset.json - serialized WidgetDataPreset
   * preview.png - preview image
   * schema_version.txt - schema version of exported data
   * /iconPacks/0/icon_file_1.svg - icon file from icon pack with id 0
   * /iconPacks/0/icon_file_2.svg
   * /iconPacks/0/icon_file_3.svg
   * ```
   */
  suspend fun export(presetId: Long, destination: PlatformFile): Unit =
    withContext(Dispatchers.IO) {
      context.contentResolver.openOutputStream(destination.uri())?.use { outputStream ->
        val widgetDataPreset =
          widgetDataPresetCustomRepository.loadByPresetId(presetId)?.nullifyGalleryImageUri()
            ?: error("No preset with id: $presetId")
        ZipOutputStream(outputStream.buffered()).use { zipOutputStream ->
          // widget data preset as json
          val widgetDataPresetJson = Json.encodeToString<WidgetDataPreset>(widgetDataPreset)
          val widgetDataPresetZipEntry = ZipEntry(WIDGET_DATA_PRESET_FILE_NAME)
          zipOutputStream.putNextEntry(widgetDataPresetZipEntry)
          zipOutputStream.write(widgetDataPresetJson.toByteArray())

          // schema version file with schema version
          val schemaVersion = SCHEMA_VERSION.toString().toByteArray()
          val schemaVersionEntry = ZipEntry(SCHEMA_VERSION_FILE_NAME)
          zipOutputStream.putNextEntry(schemaVersionEntry)
          zipOutputStream.write(schemaVersion)

          // preview image
          val previewFile = widgetDataPreset.getPreviewPath(context.filesPath).toFile()
          if (previewFile.exists()) {
            val entry = ZipEntry(WidgetDataPreset.PREVIEW_IMAGE_NAME)
            zipOutputStream.putNextEntry(entry)
            previewFile.writeToZip(zipOutputStream)
          }

          // icon pack
          widgetDataPreset.layers
            .filterIsInstance<ColdImageLayer>()
            .mapNotNull { layer ->
              val iconPack = layer.imageUriSource as? ImageUriSource.IconPack
              iconPack?.iconFile
            }
            .filter { iconFile -> iconFile.iconPack is IconPack.Custom }
            .distinct()
            .forEach { iconFile ->
              val iconFileFullPath = iconFile.getFullPath(context.filesPath)
              val file = iconFileFullPath.toFile()
              // /iconPacks/0/file.svg
              val relativePath = iconFileFullPath.relativeTo(context.filesPath)
              val entry = ZipEntry(relativePath.toString())
              zipOutputStream.putNextEntry(entry)
              file.writeToZip(zipOutputStream)
            }

          // font files
          val fontsFromLayers =
            widgetDataPreset.layers
              .filterIsInstance<ColdTextLayer>()
              .map { textLayer -> textLayer.textStyleSource }
              .filterIsInstance<TextStyleSource.Local>()
              .map { it.fontFile }
              .filterIsInstance<FontFile.Custom>()
          val fontsFromGlobals =
            widgetDataPreset.globals.textStyles
              .map { it.value }
              .filterIsInstance<TextStyleSource.Local>()
              .map { it.fontFile }
              .filterIsInstance<FontFile.Custom>()

          val allFonts = fontsFromLayers + fontsFromGlobals
          allFonts.forEach { fontFile ->
            val fontFileFullPath = fontFile.getFullPath(context.filesPath)
            val file = fontFileFullPath.toFile()
            // /fonts/0/font.otf
            val relativePath = fontFileFullPath.relativeTo(context.filesPath)
            val entry = ZipEntry(relativePath.toString())
            zipOutputStream.putNextEntry(entry)
            file.writeToZip(zipOutputStream)
          }
        }
      }
    }

  /** Unzips archive and return basic info about data that user wants to import. */
  suspend fun preloadData(importingPresetUri: String): ImportingWidgetDataPreset =
    withContext(Dispatchers.IO) {
      val temporaryFolderPath = getTemporaryFolderPath()
      temporaryFolderPath.toFile().deleteRecursively()
      temporaryFolderPath.toFile().mkdirs()

      // unzip into temporary folder
      context.applicationContext.contentResolver.openInputStream(importingPresetUri.toUri())?.use {
        backupFileInputStream ->
        ZipInputStream(backupFileInputStream).use { zipInputStream ->
          var entry = zipInputStream.nextEntry
          while (entry != null) {
            val unzippedPath = temporaryFolderPath / entry.name
            val unzippedFile = unzippedPath.toFile()
            unzippedFile.parentFile?.mkdirs()
            unzippedFile.outputStream().buffered().use { unzippedFileStream ->
              zipInputStream.copyTo(unzippedFileStream)
            }
            entry = zipInputStream.nextEntry
          }
        }
      }

      // data was read, convert
      val schemaVersion =
        (temporaryFolderPath / SCHEMA_VERSION_FILE_NAME).toFile().readText().toInt()
      val widgetDataPresetAsString =
        (temporaryFolderPath / WIDGET_DATA_PRESET_FILE_NAME).toFile().readText()
      val widgetDataPreset =
        consumeAndMigrateWidgetDataPreset(widgetDataPresetAsString, schemaVersion)

      val fullPreviewPath = temporaryFolderPath / WidgetDataPreset.PREVIEW_IMAGE_NAME
      val importingIconPacks =
        widgetDataPreset.layers
          .filterIsInstance<ColdImageLayer>()
          .mapNotNull { layer ->
            val iconImageSource = layer.imageUriSource as? ImageUriSource.IconPack
            iconImageSource?.iconFile?.iconPack
          }
          .filterIsInstance<IconPack.Custom>()
          .distinct()
          .map { ImportingIconPack(it.iconPackId, it.name, ImportingIconPackAction.CreateNew) }

      val importingFontFilesFromLayers =
        widgetDataPreset.layers
          .filterIsInstance<ColdTextLayer>()
          .map { it.textStyleSource }
          .filterIsInstance<TextStyleSource.Local>()
          .map { it.fontFile }
          .filterIsInstance<FontFile.Custom>()
          .map { ImportingFontFile(it.fileName, true) }
      val importingFontFilesFromGlobals =
        widgetDataPreset.globals.textStyles
          .map { it.value }
          .filterIsInstance<TextStyleSource.Local>()
          .map { it.fontFile }
          .filterIsInstance<FontFile.Custom>()
          .map { ImportingFontFile(it.fileName, true) }
      val allImportingFontFiles = importingFontFilesFromLayers + importingFontFilesFromGlobals

      return@withContext ImportingWidgetDataPreset(
        widgetDataPreset = widgetDataPreset,
        fullPreviewPath = fullPreviewPath,
        importingIconPacks = importingIconPacks,
        importingFontFiles = allImportingFontFiles,
      )
    }

  suspend fun import(importingWidgetDataPreset: ImportingWidgetDataPreset) {
    withContext(Dispatchers.IO) {
      val temporaryFolderPath = getTemporaryFolderPath()
      val widgetDataPreset =
        importIconPacksFromPreset(
          widgetDataPreset = importingWidgetDataPreset.widgetDataPreset,
          importingIconPacks = importingWidgetDataPreset.importingIconPacks,
          temporaryFolderPath = temporaryFolderPath,
        )
      importFontFileFromPreset(
        importingFontFiles = importingWidgetDataPreset.importingFontFiles,
        temporaryFolderPath = temporaryFolderPath,
      )
      widgetDataPresetCustomRepository.insertNewWithPreview(
        widgetDataPreset = widgetDataPreset,
        previewPath = importingWidgetDataPreset.fullPreviewPath,
      )
    }
  }

  /** Import icon packs and update icon files so they point to correct paths. */
  private suspend fun importIconPacksFromPreset(
    widgetDataPreset: WidgetDataPreset.Custom,
    importingIconPacks: List<ImportingIconPack>,
    temporaryFolderPath: Path,
  ): WidgetDataPreset.Custom {
    var result = widgetDataPreset
    for (importingIconPack in importingIconPacks) {
      val oldIconPack =
        IconPack.Custom(importingIconPack.importingId, importingIconPack.importingName)
      val importingIconPackPath = oldIconPack.getFullPath(temporaryFolderPath)
      val iconFilesInImporting =
        importingIconPackPath.toFile().listFiles()?.map { PlatformFile(it) } ?: emptyList()
      when (val action = importingIconPack.action) {
        ImportingIconPackAction.CreateNew -> {
          // create new icon pack (id will be updated from db)
          val newIconPack = iconPackCustomRepository.addNewIconPack(oldIconPack)
          // import icons into this icon pack
          iconPackCustomRepository.importIconFiles(newIconPack, iconFilesInImporting)
          // modify all icon layer so they point to this icon pack
          result = replaceIconPack(oldIconPack, newIconPack, result)
        }

        is ImportingIconPackAction.Merge -> {
          val destinationIconPack = action.destinationIconPack
          // import into selected icon pack
          iconPackCustomRepository.importIconFiles(destinationIconPack, iconFilesInImporting)
          // modify icon layers
          result = replaceIconPack(oldIconPack, destinationIconPack, result)
        }
      }
    }

    return result
  }

  /** Import font files, doesn't change anything in preset. */
  private suspend fun importFontFileFromPreset(
    importingFontFiles: List<ImportingFontFile>,
    temporaryFolderPath: Path,
  ) {
    val fontFilesToImport =
      importingFontFiles
        .filter { it.import }
        .map { importingFontFile ->
          val importingFontFileAndroid =
            FontFile.Custom(importingFontFile.importingName)
              .getFullPath(temporaryFolderPath)
              .toFile()
          PlatformFile(importingFontFileAndroid)
        }
    fontFileCustomRepository.importFontFiles(fontFilesToImport)
  }

  internal fun getTemporaryFolderPath(): Path =
    context.cacheDir.toOkioPath() / TEMPORARY_IMPORT_FOLDER_NAME
}

private fun replaceIconPack(
  oldIconPack: IconPack,
  newIconPack: IconPack,
  widgetDataPreset: WidgetDataPreset.Custom,
): WidgetDataPreset.Custom {
  val updatedLayers =
    widgetDataPreset.layers.map { layer ->
      if (layer !is ColdImageLayer) return@map layer
      val imageSource = layer.imageUriSource
      if (imageSource !is ImageUriSource.IconPack) return@map layer
      val iconFile = imageSource.iconFile ?: return@map layer
      if (iconFile.iconPack != oldIconPack) return@map layer

      val updatedIconFile = iconFile.copy(iconPack = newIconPack)
      val updatedLayer = layer.copy(imageUriSource = ImageUriSource.IconPack(updatedIconFile))
      updatedLayer
    }

  return widgetDataPreset.copy(layers = updatedLayers)
}

internal fun WidgetDataPreset.Custom.nullifyGalleryImageUri(): WidgetDataPreset.Custom {
  val updatedLayers =
    this.layers.map { layer ->
      if (layer !is ColdImageLayer) return@map layer
      if (layer.imageUriSource !is ImageUriSource.Gallery) return@map layer

      val updatedLayer = layer.copy(imageUriSource = ImageUriSource.Gallery(null))
      updatedLayer
    }

  return this.copy(layers = updatedLayers)
}

/**
 * Will write content of this [File] into currently active entry.
 *
 * @param zipOutputStream Target [ZipOutputStream]
 * @receiver Source [File].
 */
private fun File.writeToZip(zipOutputStream: ZipOutputStream): Unit =
  this.inputStream().buffered().use { inputStream -> inputStream.copyTo(zipOutputStream) }

private const val SCHEMA_VERSION_FILE_NAME = "schema_version.txt"
private const val TEMPORARY_IMPORT_FOLDER_NAME = "temp_importing_widget_data_preset"
private const val WIDGET_DATA_PRESET_FILE_NAME = "widget_data_preset.json"
