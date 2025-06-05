package io.github.sadellie.sukko.core.data

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import io.github.sadellie.sukko.core.common.filesPath
import io.github.sadellie.sukko.core.database.SukkoDatabase
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.WidgetDataPreset
import kotlinx.coroutines.runBlocking
import okio.Path.Companion.toOkioPath
import org.junit.Test

class WidgetDataPresetRepositoryDeleteTest {
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

  @Test
  fun delete_nonExisting() = runBlocking {
    // should not throw anything
    widgetDataPresetRepository.delete(18)
    assert(true)
  }

  @Test
  fun delete_withAllFiles() = runBlocking {
    // first inserted is always with id 1
    val widgetDataPreset =
      WidgetDataPreset.Custom(
        presetId = 1,
        name = "Widget 1",
        layers = emptyList(),
        globals = Globals(),
      )
    val previewPath = context.cacheDir.toOkioPath() / "preview.png"
    val previewFile = previewPath.toFile()
    previewFile.parentFile?.mkdirs()
    previewFile.createNewFile()
    widgetDataPresetRepository.insertNewWithPreview(
      widgetDataPreset = widgetDataPreset,
      previewPath = previewPath,
    )
    widgetDataPresetRepository.delete(widgetDataPreset.presetId)

    val widgetDataPresetDir = widgetDataPreset.getPreviewPath(context.filesPath).parent!!.toFile()
    val isExists = widgetDataPresetDir.exists()

    assert(!isExists)
  }
}
