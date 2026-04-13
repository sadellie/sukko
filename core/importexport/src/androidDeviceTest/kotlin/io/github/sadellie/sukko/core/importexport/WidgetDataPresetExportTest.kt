package io.github.sadellie.sukko.core.importexport

import android.net.Uri
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import io.github.sadellie.sukko.core.common.EXPORT_EXTENSION
import io.github.sadellie.sukko.core.common.filesPath
import io.github.sadellie.sukko.core.data.IconPackCustomRepositoryImpl
import io.github.sadellie.sukko.core.data.WidgetDataPresetCustomRepositoryImpl
import io.github.sadellie.sukko.core.database.SukkoDatabase
import io.github.sadellie.sukko.core.fontfiles.FontFile
import io.github.sadellie.sukko.core.fontfiles.FontFileCustomRepositoryImpl
import io.github.sadellie.sukko.core.iconfiles.IconFile
import io.github.sadellie.sukko.core.iconfiles.IconPack
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.WidgetDataPreset
import io.github.sadellie.sukko.core.model.basic.ImageUriSource
import io.github.sadellie.sukko.core.model.basic.TextStyleSource
import io.github.sadellie.sukko.core.model.layer.ColdColumnLayer
import io.github.sadellie.sukko.core.model.layer.ColdImageLayer
import io.github.sadellie.sukko.core.model.layer.ColdTextLayer
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.zip.ZipInputStream

class WidgetDataPresetExportTest {
  private val context =
    InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
  private val db = Room.inMemoryDatabaseBuilder<SukkoDatabase>(context).build()
  private val widgetDataPresetDao = db.widgetDataPresetDao()
  private val widgetDataPresetRepository =
    WidgetDataPresetCustomRepositoryImpl(
      dao = widgetDataPresetDao,
      context = context,
      removeImageFromCache = {},
    )
  private val iconPackDao = db.iconPackDao()
  private val iconPackRepository =
    IconPackCustomRepositoryImpl(dao = iconPackDao, context = context, removeImageFromCache = {})
  private val fontFileCustomRepository = FontFileCustomRepositoryImpl(context = context)
  private val widgetDataPresetExportImport =
    WidgetDataPresetExportImport(
      widgetDataPresetCustomRepository = widgetDataPresetRepository,
      iconPackCustomRepository = iconPackRepository,
      fontFileCustomRepository = fontFileCustomRepository,
      context = context,
    )

  @Test
  fun export_testWithNullIconFile() {
    runBlocking(Dispatchers.IO) {
      val widgetDataPreset =
        WidgetDataPreset.Custom(
          presetId = 18,
          name = "Widget preset 18",
          layers =
            listOf(
              ColdColumnLayer(id = 0),
              ColdTextLayer(id = 1, parentId = 0),
              ColdTextLayer(id = 2, parentId = 0),
              ColdImageLayer(id = 3, parentId = 0, imageUriSource = ImageUriSource.IconPack(null)),
            ),
          globals = Globals(),
        )
      val expectedExportContent =
        listOf("widget_data_preset.json", "schema_version.txt", "preview.png")
      assertExport(widgetDataPreset, expectedExportContent)
    }
  }

  @Test
  fun export_testWithIconFileAndFontFile() {
    runBlocking(Dispatchers.IO) {
      // manually put icon files
      val iconPack =
        iconPackRepository.addNewIconPack(IconPack.Custom(iconPackId = 0, name = "Icon pack 1"))
      val iconFile = IconFile(fileName = "icon_file.svg", iconPack = iconPack)
      val iconFileAndroid = iconFile.getFullPath(context.filesPath).toFile()
      iconFileAndroid.parentFile?.mkdirs()
      iconFileAndroid.createNewFile()

      // manually put font file
      val fontFile = FontFile.Custom("font_1.otf")
      val fontFileAndroid = fontFile.getFullPath(context.filesPath).toFile()
      fontFileAndroid.parentFile?.mkdirs()
      fontFileAndroid.createNewFile()

      val widgetDataPreset =
        WidgetDataPreset.Custom(
          presetId = 18,
          name = "Widget preset 18",
          layers =
            listOf(
              ColdColumnLayer(id = 0),
              ColdTextLayer(id = 1, parentId = 0),
              ColdTextLayer(
                id = 2,
                parentId = 0,
                textStyleSource = TextStyleSource.Local(fontFile = fontFile),
              ),
              ColdImageLayer(
                id = 3,
                parentId = 0,
                imageUriSource = ImageUriSource.IconPack(iconFile = iconFile),
              ),
            ),
          globals = Globals(),
        )
      val expectedExportContent =
        listOf(
          "widget_data_preset.json",
          "schema_version.txt",
          "preview.png",
          "iconPacks/1/icon_file.svg",
          "fonts/font_1.otf",
        )
      assertExport(widgetDataPreset, expectedExportContent)
    }
  }

  /** Checks if exporting [widgetDataPreset] produces a zip archive with [expectedExportContent] */
  private suspend fun assertExport(
    widgetDataPreset: WidgetDataPreset.Custom,
    expectedExportContent: List<String>,
  ) {
    val previewPath = widgetDataPreset.getPreviewPath(context.filesPath)
    previewPath.parent?.toFile()?.mkdirs()
    withContext(Dispatchers.IO) { previewPath.toFile().createNewFile() }
    val insertedId =
      widgetDataPresetRepository.insertNewWithPreview(
        widgetDataPreset = widgetDataPreset,
        previewPath = previewPath,
      )
    val destinationFilePath = context.filesPath / "exported.$EXPORT_EXTENSION"
    val destinationFile = destinationFilePath.toFile()

    widgetDataPresetExportImport.export(
      presetId = insertedId,
      destination = PlatformFile(uri = Uri.fromFile(destinationFile)),
    )

    val actualExportContent = mutableListOf<String>()
    destinationFile.inputStream().use { fileInputStream ->
      ZipInputStream(fileInputStream).use { zipInputStream ->
        var entry = zipInputStream.nextEntry
        while (entry != null) {
          actualExportContent.add(entry.name)
          entry = zipInputStream.nextEntry
        }
      }
    }

    assertEquals(expectedExportContent, actualExportContent)
  }
}
