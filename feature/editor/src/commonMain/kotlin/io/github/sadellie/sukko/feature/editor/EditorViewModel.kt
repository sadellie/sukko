package io.github.sadellie.sukko.feature.editor

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.DpSize
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import io.github.sadellie.sukko.core.common.combineBig
import io.github.sadellie.sukko.core.common.defaultIODispatcher
import io.github.sadellie.sukko.core.common.stateIn
import io.github.sadellie.sukko.core.data.ImageProvider
import io.github.sadellie.sukko.core.data.LayerContextProvider
import io.github.sadellie.sukko.core.data.LayerEvaluator
import io.github.sadellie.sukko.core.data.WidgetDataPresetCustomRepository
import io.github.sadellie.sukko.core.data.WidgetDataRepository
import io.github.sadellie.sukko.core.model.WidgetData
import io.github.sadellie.sukko.core.model.WidgetDataPreset
import io.github.sadellie.sukko.core.model.WidgetUpdateException
import io.github.sadellie.sukko.core.model.layer.Layer
import io.github.sadellie.sukko.feature.widget.WidgetInfoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class EditorViewModel(
  private val appWidgetId: Int,
  private val widgetDataRepository: WidgetDataRepository,
  private val widgetDataPresetCustomRepository: WidgetDataPresetCustomRepository,
  private val widgetInfoRepository: WidgetInfoRepository,
  private val imageProvider: ImageProvider,
) : ViewModel(), KoinComponent {
  private val layerContextProvider = LayerContextProvider()
  private val _canvasSize = MutableStateFlow<DpSize?>(null)
  private val _currentWidgetData = MutableStateFlow<WidgetData?>(null)
  private val _currentlySelectedLayerId = MutableStateFlow<Int?>(null)
  private val _highlightSelectedLayer = MutableStateFlow(true)
  private val _viewerState =
    combine(_currentWidgetData, _currentlySelectedLayerId, _highlightSelectedLayer) {
        widgetData,
        selectedId,
        highlightSelectedLayer ->
        val currentLayers =
          widgetData?.layers
            ?: return@combine ViewerState(
              currentLayer = null,
              parentLayer = null,
              breadcrumbs = emptyList(),
              loadedLayers = emptyList(),
              highlightSelectedLayer = false,
            )
        val newBreadcrumbs = generateBreadcrumbs(selectedId, currentLayers)
        val newLoadedLayers = currentLayers.filter { layer -> layer.parentId == selectedId }
        val currentLayer = widgetData.layers.firstOrNull { it.id == selectedId }
        val parentLayer =
          if (currentLayer != null) {
            widgetData.layers.firstOrNull { it.id == currentLayer.parentId }
          } else {
            null
          }
        return@combine ViewerState(
          currentLayer = currentLayer,
          highlightSelectedLayer = highlightSelectedLayer,
          parentLayer = parentLayer,
          breadcrumbs = newBreadcrumbs,
          loadedLayers = newLoadedLayers,
        )
      }
      .flowOn(Dispatchers.Default)

  private val _layerContext =
    flow {
        while (true) {
          val layerContext = layerContextProvider.provide()
          emit(layerContext)
          delay(EVALUATED_LAYERS_UPDATE_RATE_MS)
        }
      }
      .flowOn(defaultIODispatcher)
      .distinctUntilChanged()

  @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
  private val _evaluatedLayers =
    combine(_currentWidgetData, _layerContext) { currentWidgetData, layerContext ->
        layerContext.globalValueCache.clear()
        val layers = currentWidgetData?.layers ?: return@combine flowOf(emptyList())
        LayerEvaluator(layers, imageProvider, layerContext, currentWidgetData.globals)
          .evaluateEnabled()
      }
      .debounce(LAYER_EVALUATION_DEBOUNCE)
      .flatMapLatest { it }
      .flowOn(defaultIODispatcher)
      .distinctUntilChanged()
      .stateIn(viewModelScope, emptyList())

  private val _cachedDataFromDatabase = MutableStateFlow<WidgetData?>(null)
  private val _widgetDataSaverState =
    MutableStateFlow<WidgetDataSaverState>(WidgetDataSaverState.NotRunning)
  private val _isWidgetDataSaved =
    combine(_cachedDataFromDatabase, _currentWidgetData) { cachedDataFromDatabase, currentWidgetData
        ->
        if (currentWidgetData == null || cachedDataFromDatabase == null) return@combine false
        cachedDataFromDatabase == currentWidgetData
      }
      .flowOn(Dispatchers.Default)

  init {
    viewModelScope.launch {
      val widgetDataFromDatabase =
        widgetDataRepository.loadByAppWidgetId(appWidgetId) ?: WidgetData(appWidgetId = appWidgetId)
      _currentWidgetData.update { widgetDataFromDatabase }
      _cachedDataFromDatabase.update { widgetDataFromDatabase }
    }
  }

  internal val uiState =
    combineBig(
        _currentWidgetData,
        _evaluatedLayers,
        _canvasSize,
        _viewerState,
        _isWidgetDataSaved,
        _widgetDataSaverState,
      ) {
        currentWidgetData,
        evaluatedLayers,
        canvasSize,
        viewerState,
        isWidgetDataSaved,
        widgetDataSaverState ->
        if (canvasSize == null) return@combineBig null
        if (currentWidgetData == null) return@combineBig null

        EditorUIState(
          widgetData = currentWidgetData,
          widgetDataSaverState = widgetDataSaverState,
          isWidgetDataSaved = isWidgetDataSaved,
          evaluatedLayers = evaluatedLayers,
          canvasSize = canvasSize,
          viewerState = viewerState,
        )
      }
      .stateIn(viewModelScope, null)

  fun loadFromPreset(presetId: Long, isBuiltIn: Boolean) =
    viewModelScope.launch(Dispatchers.Default) {
      val preset =
        if (isBuiltIn) {
          WidgetDataPreset.builtIns().firstOrNull { it.presetId == presetId }
        } else {
          widgetDataPresetCustomRepository.loadByPresetId(presetId)
        }
      if (preset == null) {
        Logger.e(tag = TAG) { "loadFromPreset: $presetId not found" }
        return@launch
      }
      _currentWidgetData.update { it?.copy(layers = preset.layers, globals = preset.globals) }
    }

  internal fun onUpdateWidgetSize() =
    viewModelScope.launch {
      val newWidgetSize = widgetInfoRepository.getWidgetSize(appWidgetId)
      _canvasSize.update { newWidgetSize }
    }

  internal fun updateHighlightSelectedLayer(newValue: Boolean) =
    _highlightSelectedLayer.update { newValue }

  @Suppress("CyclomaticComplexMethod")
  internal fun onEvent(event: EditorEvent) =
    _currentWidgetData.update { widgetData ->
      if (widgetData == null) return@update widgetData
      with(widgetData) {
        when (event) {
          is EditorEvent.LayerAction.Add -> addLayer(event.layerToAdd)
          is EditorEvent.LayerAction.Delete -> deleteLayer(event.layerId)
          is EditorEvent.LayerAction.Reorder -> updateLayerOrder(event.updatedLayers)
          is EditorEvent.LayerAction.Update -> updateLayer(event.updatedLayer)
          is EditorEvent.WidgetModifierAction.Add ->
            addWidgetModifier(event.layerId, event.newModifier)
          is EditorEvent.WidgetModifierAction.Delete ->
            deleteWidgetModifier(event.layerId, event.modifierToDelete)
          is EditorEvent.WidgetModifierAction.Reorder ->
            reorderWidgetModifier(event.layerId, event.updatedModifiers)
          is EditorEvent.WidgetModifierAction.Update ->
            updateWidgetModifier(event.layerId, event.updatedModifier)
          is EditorEvent.GlobalAction.Add -> copy(globals = globals.addGlobal(event.globalToAdd))
          is EditorEvent.GlobalAction.Delete ->
            copy(globals = globals.deleteGlobal(event.globalToDelete))
          is EditorEvent.GlobalAction.Update ->
            copy(globals = globals.updateGlobal(event.globalToUpdate))
          is EditorEvent.ClickActionAction.Add ->
            addClickAction(event.layerId, event.clickActionToAdd)
          is EditorEvent.ClickActionAction.Delete ->
            deleteClickAction(event.layerId, event.clickActionToDelete)
          is EditorEvent.ClickActionAction.Reorder ->
            reorderClickActions(event.layerId, event.updatedClickActions)
          is EditorEvent.ClickActionAction.Update ->
            updateClickAction(event.layerId, event.updatedClickAction)
        }
      }
    }

  internal fun onNavigateToLayer(layerId: Int?) = _currentlySelectedLayerId.update { layerId }

  internal fun renameWidget(widgetName: String) =
    _currentWidgetData.update { widgetData ->
      if (widgetData == null) return@update widgetData
      widgetData.copy(name = widgetName)
    }

  internal fun updateWidgetSaverState(widgetDataSaverState: WidgetDataSaverState) =
    _widgetDataSaverState.update { widgetDataSaverState }

  internal fun saveWidgetData(isForced: Boolean, preview: ImageBitmap?) =
    viewModelScope.launch(Dispatchers.Default) {
      val widgetData = _currentWidgetData.value ?: return@launch
      val evaluatedLayers = _evaluatedLayers.value
      _widgetDataSaverState.update { WidgetDataSaverState.Running }
      try {
        widgetDataRepository.save(widgetData, evaluatedLayers, preview, isForced)
        _widgetDataSaverState.update { WidgetDataSaverState.NotRunning }
        _currentWidgetData.update { widgetData }
        _cachedDataFromDatabase.update { widgetData }
      } catch (_: WidgetUpdateException.MissingNotificationListener) {
        _widgetDataSaverState.update { WidgetDataSaverState.MissingNotificationListener }
      } catch (e: Exception) {
        Logger.e(throwable = e, tag = TAG) { "Failed to save widget data" }
        _widgetDataSaverState.update { WidgetDataSaverState.Error }
      }
    }

  private fun generateBreadcrumbs(layerId: Int?, allLayers: List<Layer.Cold>): List<Layer.Cold> {
    if (layerId == null) return emptyList()
    if (allLayers.isEmpty()) return emptyList()

    val result = mutableListOf<Layer.Cold>()
    var layerToLoad = layerId

    while (layerToLoad != null) {
      val layerToAdd = allLayers.first { it.id == layerToLoad }
      result.add(layerToAdd)
      layerToLoad = layerToAdd.parentId
    }

    return result.asReversed()
  }
}

private const val LAYER_EVALUATION_DEBOUNCE = 300L
private const val TAG = "EditorViewModel"
private const val EVALUATED_LAYERS_UPDATE_RATE_MS = 10_000L
