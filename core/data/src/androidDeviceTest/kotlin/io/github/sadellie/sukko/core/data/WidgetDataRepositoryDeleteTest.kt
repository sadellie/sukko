package io.github.sadellie.sukko.core.data

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import io.github.sadellie.sukko.core.common.filesPath
import io.github.sadellie.sukko.core.database.SukkoDatabase
import io.github.sadellie.sukko.core.model.WidgetData
import kotlinx.coroutines.runBlocking
import org.junit.Test

class WidgetDataRepositoryDeleteTest {
  private val context =
    InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
  private val db = Room.inMemoryDatabaseBuilder<SukkoDatabase>(context).build()
  private val widgetDataDao = db.widgetDataDao()
  private val widgetDataRepository =
    WidgetDataRepositoryImpl(dao = widgetDataDao, context = context, removeImageFromCache = {})

  @Test
  fun delete_nonExisting() = runBlocking {
    // should not throw anything
    widgetDataRepository.delete(18)
    assert(true)
  }

  @Test
  fun delete_withAllFiles() = runBlocking {
    val widgetData = WidgetData(appWidgetId = 20, name = "Widget 1", layers = emptyList())
    widgetDataRepository.save(
      widgetData = widgetData,
      evaluatedLayers = emptyList(),
      previewImageBitmap = null,
      isForced = true,
    )
    widgetDataRepository.delete(20)

    val widgetDataDir = widgetData.getPreviewPath(context.filesPath).parent!!.toFile()
    val isExists = widgetDataDir.exists()

    assert(!isExists)
  }
}
