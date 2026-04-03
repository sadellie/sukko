package io.github.sadellie.sukko.feature.widget

import androidx.compose.ui.unit.DpSize
import kotlinx.coroutines.flow.Flow

interface WidgetInfoRepository {
  fun allWidgetIds(): Flow<IntArray>

  suspend fun getWidgetSize(appWidgetId: Int): DpSize
}
