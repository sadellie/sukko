package io.github.sadellie.sukko.core.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.util.SizeF
import android.widget.RemoteViews
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.util.fastForEach
import androidx.glance.session.Session
import co.touchlab.kermit.Logger
import io.github.sadellie.sukko.core.common.appWidgetSizes
import io.github.sadellie.sukko.core.data.LayerContextProvider
import io.github.sadellie.sukko.core.data.WidgetDataRepository
import io.github.sadellie.sukko.core.model.LayerContext
import io.github.sadellie.sukko.core.model.WidgetData
import io.github.sadellie.sukko.core.model.layer.evaluateEnabled
import io.github.sadellie.sukko.core.unglance.RenderResult
import io.github.sadellie.sukko.core.unglance.renderAllWidgetConfigurations
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class AppWidgetSession(private val appWidgetId: Int) :
  Session(appWidgetId.toString()), KoinComponent {
  companion object {
    private const val TAG = "AppWidgetSession"
    // avoid processing all events and short initial renders (throttle all render steps)
    private const val EVALUATE_LAYER_DEBOUNCE_MS = 500L
    private const val PROVIDE_UNGLANCE_DEBOUNCE_MS = 500L
    private const val RENDER_DEBOUNCE_MS = 500L
  }

  private sealed interface UnglanceEvent {
    /** Update entire widget invalidating layer context and widget data. */
    data object UpdateWidget : UnglanceEvent

    /**
     * Update parts of layer context and invalidate layer properties that become outdated on alarm:
     * time, battery level
     */
    data object UpdateFromAlarm : UnglanceEvent

    /** Update only media info but keep remaining data. */
    data object UpdateMediaInfo : UnglanceEvent

    /** Keep layer context and widget data, but update widget size. */
    data object UpdateWidgetOptions : UnglanceEvent
  }

  private val _widgetDataRepository: WidgetDataRepository by inject()
  // render inputs
  private val _layerContext = MutableStateFlow<LayerContext?>(null)
  private val _widgetData = MutableStateFlow<WidgetData?>(null)
  private val _widgetSizes = MutableStateFlow<List<DpSize>?>(null)

  @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
  override suspend fun provideUnglance(context: Context): Flow<RenderResult> =
    // evaluate layers
    combine(_layerContext, _widgetData) { layerContext, widgetData ->
        // null if loading
        if (layerContext == null || widgetData == null) flowOf(null)
        else widgetData.layers.evaluateEnabled(layerContext, widgetData.globals)
      }
      .debounce(EVALUATE_LAYER_DEBOUNCE_MS)
      // emit stable evaluated result
      .flatMapLatest { it }
      // render for current targets (widget sizes)
      .combine(_widgetSizes) { evaluatedLayers, widgetSizes ->
        // loading, do not render yet
        if (widgetSizes == null || evaluatedLayers == null) flowOf(RenderResult.Ignore)
        else renderAllWidgetConfigurations(context, widgetSizes, evaluatedLayers)
      }
      .debounce(RENDER_DEBOUNCE_MS)
      // emit stable renders
      .flatMapLatest { it }
      .debounce(PROVIDE_UNGLANCE_DEBOUNCE_MS)

  override suspend fun processRenderResult(context: Context, renderResult: RenderResult) {
    val remoteViews =
      when (renderResult) {
        RenderResult.Ignore -> return
        RenderResult.Error -> RemoteViews(context.packageName, R.layout.error_layout)
        is RenderResult.Ready ->
          RemoteViews(
            renderResult.subResults.associate { subResult ->
              subResult.widgetSize.toSizeF() to
                processRenderSubResult(context, subResult, renderResult.layers)
            }
          )
      }
    val appWidgetManager = AppWidgetManager.getInstance(context)
    appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
  }

  override suspend fun processEvent(context: Context, event: Any) {
    if (event !is UnglanceEvent) error("Unknown event($appWidgetId): $event")
    Logger.d(TAG) { "processEvent($appWidgetId): $event" }
    // update property only when it's null or specifically requested
    // duplicated code, but readable and flexible
    val layerContextProvider = LayerContextProvider()

    when (event) {
      is UnglanceEvent.UpdateWidget -> {
        // reload everything
        _widgetSizes.update { AppWidgetManager.getInstance(context).appWidgetSizes(appWidgetId) }
        _layerContext.update { layerContextProvider.provide(currentCoroutineContext()) }
        _widgetData.update { _widgetDataRepository.loadByAppWidgetId(appWidgetId) }
      }
      is UnglanceEvent.UpdateWidgetOptions -> {
        _widgetSizes.update {
          it ?: AppWidgetManager.getInstance(context).appWidgetSizes(appWidgetId)
        }
        _layerContext.update { layerContextProvider.provide(currentCoroutineContext()) }
        _widgetData.update { it ?: _widgetDataRepository.loadByAppWidgetId(appWidgetId) }
      }
      is UnglanceEvent.UpdateFromAlarm -> {
        _widgetSizes.update {
          it ?: AppWidgetManager.getInstance(context).appWidgetSizes(appWidgetId)
        }
        _layerContext.update {
          it?.invalidateOnAlarmProviders()
            ?: layerContextProvider.provide(currentCoroutineContext())
        }
        _widgetData.update { it ?: _widgetDataRepository.loadByAppWidgetId(appWidgetId) }
      }
      is UnglanceEvent.UpdateMediaInfo -> {
        _widgetSizes.update {
          it ?: AppWidgetManager.getInstance(context).appWidgetSizes(appWidgetId)
        }
        _layerContext.update {
          it?.invalidateMediaInfoProvider()
            ?: layerContextProvider.provide(currentCoroutineContext())
        }
        _widgetData.update { it ?: _widgetDataRepository.loadByAppWidgetId(appWidgetId) }
      }
    }
  }

  override suspend fun recreateWithEvents(events: List<Any>): Session {
    Logger.d(TAG) { "recreateWithEvents($appWidgetId): $events" }
    return AppWidgetSession(appWidgetId).also { newSession ->
      events.fastForEach { event -> newSession.sendEvent(event) }
    }
  }

  override suspend fun onCompositionError(context: Context, throwable: Throwable) =
    processRenderResult(context, RenderResult.Error)

  suspend fun updateWidget() = sendEvent(UnglanceEvent.UpdateWidget)

  suspend fun updateFromAlarm() = sendEvent(UnglanceEvent.UpdateFromAlarm)

  suspend fun updateMediaInfo() = sendEvent(UnglanceEvent.UpdateMediaInfo)

  suspend fun updateWidgetOptions() = sendEvent(UnglanceEvent.UpdateWidgetOptions)
}

private fun DpSize.toSizeF(): SizeF = SizeF(width.value, height.value)
