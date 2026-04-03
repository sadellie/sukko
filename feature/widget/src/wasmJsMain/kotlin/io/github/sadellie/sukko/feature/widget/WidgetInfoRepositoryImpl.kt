package io.github.sadellie.sukko.feature.widget

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class WidgetInfoRepositoryImpl : WidgetInfoRepository {
  override fun allWidgetIds(): Flow<IntArray> = flowOf(intArrayOf())

  override suspend fun getWidgetSize(appWidgetId: Int): DpSize = DpSize(100.dp, 100.dp)
}
