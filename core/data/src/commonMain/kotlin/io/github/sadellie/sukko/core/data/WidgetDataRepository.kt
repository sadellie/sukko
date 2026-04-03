package io.github.sadellie.sukko.core.data

import androidx.compose.ui.graphics.ImageBitmap
import io.github.sadellie.sukko.core.model.WidgetData
import io.github.sadellie.sukko.core.model.layer.Layer
import kotlinx.coroutines.flow.Flow
import okio.Path

interface WidgetDataRepository {
  fun allWidgetData(decodeExtra: Boolean = true): Flow<List<WidgetData>>

  suspend fun loadByAppWidgetId(appWidgetId: Int): WidgetData?

  suspend fun save(
    widgetData: WidgetData,
    evaluatedLayers: List<Layer.Evaluated>,
    previewImageBitmap: ImageBitmap?,
    isForced: Boolean,
  )

  suspend fun rename(appWidgetId: Int, newName: String)

  suspend fun delete(appWidgetId: Int)

  /** Return full path to preview from [WidgetData.getPreviewPath] using actual device folder. */
  suspend fun getPreview(widgetData: WidgetData): Path?
}
