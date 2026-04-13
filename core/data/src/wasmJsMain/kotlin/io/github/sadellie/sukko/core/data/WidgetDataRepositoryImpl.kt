package io.github.sadellie.sukko.core.data

import androidx.compose.ui.graphics.ImageBitmap
import io.github.sadellie.sukko.core.model.WidgetData
import io.github.sadellie.sukko.core.model.layer.Layer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import okio.Path

internal class WidgetDataRepositoryImpl : WidgetDataRepository {
  override fun allWidgetData(decodeExtra: Boolean): Flow<List<WidgetData>> = flowOf(emptyList())

  override suspend fun loadByAppWidgetId(appWidgetId: Int): WidgetData? = null

  override suspend fun save(
    widgetData: WidgetData,
    evaluatedLayers: List<Layer.Evaluated>,
    previewImageBitmap: ImageBitmap?,
    isForced: Boolean,
  ) {}

  override suspend fun rename(appWidgetId: Int, newName: String) {}

  override suspend fun delete(appWidgetId: Int) {}

  override suspend fun getPreview(widgetData: WidgetData): Path? = null
}
