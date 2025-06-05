package io.github.sadellie.sukko.core.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import androidx.compose.ui.unit.DpSize
import io.github.sadellie.sukko.core.common.appWidgetSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class WidgetInfoRepositoryImpl(private val context: Context) : WidgetInfoRepository {
  private val appWidgetManager = AppWidgetManager.getInstance(context)
  private val componentName = ComponentName(context, MainWidgetProvider::class.java)

  override fun allWidgetIds(): Flow<IntArray> =
    flow {
        while (true) {
          val widgetIds = appWidgetManager.getAppWidgetIds(componentName)
          emit(widgetIds)
          delay(WIDGET_INFO_UPDATE_RATE_MS)
        }
      }
      .distinctUntilChanged()
      .flowOn(Dispatchers.Default)

  override suspend fun getWidgetSize(appWidgetId: Int): DpSize =
    withContext(Dispatchers.Default) {
      appWidgetManager.appWidgetSize(appWidgetId, context.resources.configuration.orientation)
    }
}

private const val WIDGET_INFO_UPDATE_RATE_MS = 5_000L
