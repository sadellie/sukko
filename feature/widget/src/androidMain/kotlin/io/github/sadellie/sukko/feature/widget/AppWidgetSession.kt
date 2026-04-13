package io.github.sadellie.sukko.feature.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.util.fastForEach
import androidx.glance.session.Session
import co.touchlab.kermit.Logger
import coil3.ImageLoader
import io.github.sadellie.sukko.core.common.appWidgetSizes
import io.github.sadellie.sukko.core.common.getWidgetSizes
import io.github.sadellie.sukko.core.data.LayerEvaluator
import io.github.sadellie.sukko.core.data.ScriptableEvaluator
import io.github.sadellie.sukko.core.data.WidgetDataRepository
import io.github.sadellie.sukko.core.data.invalidateMediaProvider
import io.github.sadellie.sukko.core.data.invalidateOnAlarmProviders
import io.github.sadellie.sukko.core.medialistener.MediaListener
import io.github.sadellie.sukko.core.unglance.RenderResult
import io.github.sadellie.sukko.core.unglance.renderAllWidgetConfigurations
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update

internal class AppWidgetSession(
  private val appWidgetId: Int,
  private val widgetDataRepository: WidgetDataRepository,
  private val mediaListener: MediaListener,
  private val imageLoader: ImageLoader,
  private val widgetProviderIntent: Intent,
  private val layerEvaluatorFactory: LayerEvaluator.LayerEvaluatorFactory,
  private val scriptableEvaluatorFactory: ScriptableEvaluator.ScriptableEvaluatorFactory,
) : Session(appWidgetId.toString()) {
  companion object {
    private const val TAG = "AppWidgetSession"
    // avoid processing all events and short initial renders (throttle all render steps)
    /**
     * The duration for which the evaluation result must remain alive to be considered stable for
     * further processing. If no new evaluations appears after the specified time (in ms), they will
     * be passed to renderer.
     */
    private const val EVALUATE_LAYER_DEBOUNCE_MS = 500L
    /**
     * The duration for which the last render must remain alive to be considered stable for further
     * processing. If no new render result appears after the specified time (in ms), the widget will
     * be updated.
     */
    private const val RENDER_DEBOUNCE_MS = 500L
    /** Debounce for processing in session worker. */
    private const val PROVIDE_UNGLANCE_DEBOUNCE_MS = 500L
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
    data class UpdateWidgetOptions(val newOptions: Bundle) : UnglanceEvent

    data class ProcessClick(val clickActions: Array<String>) : UnglanceEvent {
      override fun equals(other: Any?) =
        when {
          this === other -> true
          javaClass != other?.javaClass -> false
          other is ProcessClick -> clickActions.contentEquals(other.clickActions)
          else -> false
        }

      override fun hashCode() = clickActions.contentHashCode()
    }
  }

  // render inputs
  private val _layerEvaluator = MutableStateFlow<LayerEvaluator?>(null)
  private val _widgetSizes = MutableStateFlow<List<DpSize>?>(null)
  private val _clickActionProcessor =
    ClickActionProcessor(
      mediaListener = mediaListener,
      widgetDataRepository = widgetDataRepository,
      appWidgetId = appWidgetId,
      scriptableEvaluatorFactory = scriptableEvaluatorFactory,
    )

  @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
  override suspend fun provideUnglance(context: Context): Flow<RenderResult> =
    // evaluate layers
    _layerEvaluator
      .mapLatest { layerEvaluator ->
        // null if loading
        layerEvaluator?.evaluateEnabled() ?: flowOf(null)
      }
      .distinctUntilChanged()
      .debounce(EVALUATE_LAYER_DEBOUNCE_MS)
      // emit stable evaluated result
      .flatMapLatest { it }
      // render for current targets (widget sizes)
      .combine(_widgetSizes) { evaluatedLayers, widgetSizes ->
        // loading, do not render yet
        if (widgetSizes == null || evaluatedLayers == null) flowOf(RenderResult.Ignore)
        else renderAllWidgetConfigurations(context, widgetSizes, evaluatedLayers, imageLoader)
      }
      .distinctUntilChanged()
      .debounce(RENDER_DEBOUNCE_MS)
      // emit stable renders
      .flatMapLatest { it }
      .distinctUntilChanged()
      .debounce(PROVIDE_UNGLANCE_DEBOUNCE_MS)

  override suspend fun processRenderResult(context: Context, renderResult: RenderResult) {
    val remoteViews =
      when (renderResult) {
        RenderResult.Ignore -> return
        RenderResult.Error -> RemoteViews(context.packageName, R.layout.error_layout)
        is RenderResult.Ready ->
          processAllRenderSubResults(
            context = context,
            appWidgetId = appWidgetId,
            renderResult = renderResult,
            widgetProviderIntent = widgetProviderIntent,
          )
      }
    val appWidgetManager = AppWidgetManager.getInstance(context)
    appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
  }

  override suspend fun processEvent(context: Context, event: Any) {
    if (event !is UnglanceEvent) error("Unknown event($appWidgetId): $event")
    Logger.d(tag = TAG) { "processEvent($appWidgetId): $event" }
    // update property only when it's null or specifically requested
    // duplicated code, but readable and flexible
    when (event) {
      UnglanceEvent.UpdateWidget -> processUpdateWidgetEvent(context)
      is UnglanceEvent.UpdateWidgetOptions -> processUpdateWidgetOptionsEvent(event)
      UnglanceEvent.UpdateFromAlarm -> processUpdateFromAlarmEvent(context)
      UnglanceEvent.UpdateMediaInfo -> processUpdateMediaInfo(context)
      is UnglanceEvent.ProcessClick -> processClickEvent(event, context)
    }
  }

  private suspend fun processUpdateWidgetEvent(context: Context) {
    // reload everything
    _widgetSizes.update { AppWidgetManager.getInstance(context).appWidgetSizes(appWidgetId) }
    _layerEvaluator.update { reloadWidgetDataAndClearCache() }
  }

  private suspend fun processUpdateWidgetOptionsEvent(event: UnglanceEvent.UpdateWidgetOptions) {
    _widgetSizes.update { event.newOptions.getWidgetSizes() }
    _layerEvaluator.update { it ?: reloadWidgetDataAndClearCache() }
  }

  private suspend fun processUpdateFromAlarmEvent(context: Context) {
    _widgetSizes.update { AppWidgetManager.getInstance(context).appWidgetSizes(appWidgetId) }
    _layerEvaluator.update {
      it?.invalidateOnAlarmProviders(context) ?: reloadWidgetDataAndClearCache()
    }
  }

  private suspend fun processUpdateMediaInfo(context: Context) {
    _widgetSizes.update { AppWidgetManager.getInstance(context).appWidgetSizes(appWidgetId) }
    _layerEvaluator.update {
      it?.invalidateMediaProvider(context, mediaListener) ?: reloadWidgetDataAndClearCache()
    }
  }

  private suspend fun processClickEvent(event: UnglanceEvent.ProcessClick, context: Context) {
    _clickActionProcessor.process(
      clickActionsJsons = event.clickActions,
      context = context,
      afterScripCallback = {
        _widgetSizes.update { AppWidgetManager.getInstance(context).appWidgetSizes(appWidgetId) }
        _layerEvaluator.update { reloadWidgetDataAndClearCache() }
      },
    )
  }

  override suspend fun recreateWithEvents(events: List<Any>): Session {
    Logger.d(tag = TAG) { "recreateWithEvents($appWidgetId): $events" }
    val newSession =
      AppWidgetSession(
        appWidgetId = appWidgetId,
        widgetDataRepository = widgetDataRepository,
        mediaListener = mediaListener,
        imageLoader = imageLoader,
        widgetProviderIntent = widgetProviderIntent,
        layerEvaluatorFactory = layerEvaluatorFactory,
        scriptableEvaluatorFactory = scriptableEvaluatorFactory,
      )
    events.fastForEach { event -> newSession.sendEvent(event) }
    return newSession
  }

  override suspend fun onCompositionError(context: Context, throwable: Throwable) =
    processRenderResult(context, RenderResult.Error)

  suspend fun updateWidget() = sendEvent(UnglanceEvent.UpdateWidget)

  suspend fun updateFromAlarm() = sendEvent(UnglanceEvent.UpdateFromAlarm)

  suspend fun updateMediaInfo() = sendEvent(UnglanceEvent.UpdateMediaInfo)

  suspend fun updateClickActions(actions: Array<String>) =
    sendEvent(UnglanceEvent.ProcessClick(actions))

  suspend fun updateWidgetOptions(newOption: Bundle) =
    sendEvent(UnglanceEvent.UpdateWidgetOptions(newOption))

  private suspend fun reloadWidgetDataAndClearCache(): LayerEvaluator {
    val widgetData =
      widgetDataRepository.loadByAppWidgetId(appWidgetId) ?: error("Widget not found $appWidgetId")
    return layerEvaluatorFactory.create(layers = widgetData.layers, globals = widgetData.globals)
  }
}
