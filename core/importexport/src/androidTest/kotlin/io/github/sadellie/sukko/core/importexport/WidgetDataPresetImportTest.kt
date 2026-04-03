package io.github.sadellie.sukko.core.importexport

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import io.github.sadellie.sukko.core.common.filesPath
import io.github.sadellie.sukko.core.data.IconPackCustomRepositoryImpl
import io.github.sadellie.sukko.core.data.WidgetDataPresetCustomRepositoryImpl
import io.github.sadellie.sukko.core.database.SukkoDatabase
import io.github.sadellie.sukko.core.fontfiles.FontFile
import io.github.sadellie.sukko.core.fontfiles.FontFileCustomRepositoryImpl
import io.github.sadellie.sukko.core.iconfiles.IconFile
import io.github.sadellie.sukko.core.iconfiles.IconPack
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.ImportingFontFile
import io.github.sadellie.sukko.core.model.ImportingIconPack
import io.github.sadellie.sukko.core.model.ImportingIconPackAction
import io.github.sadellie.sukko.core.model.ImportingWidgetDataPreset
import io.github.sadellie.sukko.core.model.WidgetDataPreset
import io.github.sadellie.sukko.core.model.basic.ImageUriSource
import io.github.sadellie.sukko.core.model.basic.TextStyleSource
import io.github.sadellie.sukko.core.model.layer.ColdImageLayer
import io.github.sadellie.sukko.core.model.layer.ColdTextLayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class WidgetDataPresetImportTest {
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
  fun import_testEmpty() =
    runBlocking(Dispatchers.IO) {
      val importingWidgetDataPreset =
        ImportingWidgetDataPreset(
          widgetDataPreset =
            WidgetDataPreset.Custom(
              presetId = 7,
              name = "Widget preset 7",
              layers = emptyList(),
              globals = Globals(),
            ),
          fullPreviewPath = null,
          importingIconPacks = emptyList(),
          importingFontFiles = emptyList(),
        )
      widgetDataPresetExportImport.import(importingWidgetDataPreset)

      val expectedWidgetDataPreset =
        WidgetDataPreset.Custom(
          presetId = 1,
          name = "Widget preset 7",
          layers = emptyList(),
          globals = Globals(),
        )
      val actualWidgetDataPreset = widgetDataPresetRepository.allWidgetDataPresets().first().last()
      assertEquals(expectedWidgetDataPreset, actualWidgetDataPreset)
    }

  @Test
  fun import_testWithPreview() =
    runBlocking(Dispatchers.IO) {
      val previewFilePath = context.filesPath / "preview.png"
      val previewFile = previewFilePath.toFile()
      previewFile.createNewFile()
      val importingWidgetDataPreset =
        ImportingWidgetDataPreset(
          widgetDataPreset =
            WidgetDataPreset.Custom(
              presetId = 7,
              name = "Widget preset 7",
              layers = emptyList(),
              globals = Globals(),
            ),
          fullPreviewPath = previewFilePath,
          importingIconPacks = emptyList(),
          importingFontFiles = emptyList(),
        )
      widgetDataPresetExportImport.import(importingWidgetDataPreset)

      val expectedWidgetDataPreset =
        WidgetDataPreset.Custom(
          presetId = 1,
          name = "Widget preset 7",
          layers = emptyList(),
          globals = Globals(),
        )
      val actualWidgetDataPreset = widgetDataPresetRepository.allWidgetDataPresets().first().last()
      assertEquals(actualWidgetDataPreset, expectedWidgetDataPreset)

      assert(actualWidgetDataPreset.getPreviewPath(context.filesPath).toFile().exists())
    }

  @Test
  fun import_testWithIconFiles() =
    runBlocking(Dispatchers.IO) {
      val temporaryFolderPath = widgetDataPresetExportImport.getTemporaryFolderPath()
      val iconPack1FromImport = IconPack.Custom(iconPackId = 1, name = "Icon pack 1")
      val iconFileFromImport1 = IconFile(fileName = "file1.svg", iconPack = iconPack1FromImport)
      val iconFileFromImport2 = IconFile(fileName = "file2.svg", iconPack = iconPack1FromImport)
      val fontFileFromImport = FontFile.Custom(fileName = "font1.otf")
      iconPack1FromImport.getFullPath(temporaryFolderPath).toFile().mkdirs()
      iconFileFromImport1.getFullPath(temporaryFolderPath).toFile().createNewFile()
      iconFileFromImport2.getFullPath(temporaryFolderPath).toFile().createNewFile()
      fontFileFromImport.getFullPath(temporaryFolderPath).toFile().parentFile?.mkdirs()
      fontFileFromImport.getFullPath(temporaryFolderPath).toFile().createNewFile()
      val importingWidgetDataPreset =
        ImportingWidgetDataPreset(
          widgetDataPreset =
            WidgetDataPreset.Custom(
              presetId = 7,
              name = "Widget preset 7",
              layers =
                listOf(
                  ColdTextLayer(id = 0, parentId = null),
                  ColdImageLayer(
                    id = 1,
                    parentId = null,
                    imageUriSource = ImageUriSource.IconPack(iconFile = iconFileFromImport1),
                  ),
                  ColdImageLayer(
                    id = 2,
                    parentId = null,
                    imageUriSource = ImageUriSource.IconPack(iconFile = iconFileFromImport2),
                  ),
                  ColdImageLayer(
                    id = 3,
                    parentId = null,
                    imageUriSource = ImageUriSource.IconPack(iconFile = null),
                  ),
                  ColdTextLayer(
                    id = 4,
                    parentId = null,
                    textStyleSource = TextStyleSource.Local(fontFile = fontFileFromImport),
                  ),
                ),
              globals = Globals(),
            ),
          fullPreviewPath = null,
          importingIconPacks =
            listOf(
              ImportingIconPack(
                importingId = iconPack1FromImport.iconPackId,
                importingName = iconPack1FromImport.name,
                action = ImportingIconPackAction.CreateNew,
              )
            ),
          importingFontFiles = listOf(ImportingFontFile(importingName = "font1.otf", import = true)),
        )
      widgetDataPresetExportImport.import(importingWidgetDataPreset)

      val expectedIconPack = IconPack.Custom(iconPackId = 1, name = "Icon pack 1")
      val actualIconPack = iconPackRepository.getAll().first().last()
      assertEquals(expectedIconPack, actualIconPack)

      val expectedIconFile1 = IconFile("file1.svg", expectedIconPack)
      assert(expectedIconFile1.getFullPath(context.filesPath).toFile().exists())
      val expectedIconFile2 = IconFile("file2.svg", expectedIconPack)
      assert(expectedIconFile2.getFullPath(context.filesPath).toFile().exists())

      val expectedFontFile = FontFile.Custom(fileName = "font1.otf")
      assert(expectedFontFile.getFullPath(context.filesPath).toFile().exists())

      val expectedWidgetDataPreset =
        WidgetDataPreset.Custom(
          presetId = 1,
          name = "Widget preset 7",
          layers =
            listOf(
              ColdTextLayer(id = 0, parentId = null),
              ColdImageLayer(
                id = 1,
                parentId = null,
                imageUriSource = ImageUriSource.IconPack(iconFile = expectedIconFile1),
              ),
              ColdImageLayer(
                id = 2,
                parentId = null,
                imageUriSource = ImageUriSource.IconPack(iconFile = expectedIconFile2),
              ),
              ColdImageLayer(
                id = 3,
                parentId = null,
                imageUriSource = ImageUriSource.IconPack(iconFile = null),
              ),
              ColdTextLayer(
                id = 4,
                parentId = null,
                textStyleSource = TextStyleSource.Local(fontFile = expectedFontFile),
              ),
            ),
          globals = Globals(),
        )
      val actualWidgetDataPreset = widgetDataPresetRepository.allWidgetDataPresets().first().last()
      assertEquals(actualWidgetDataPreset, expectedWidgetDataPreset)
    }
}
