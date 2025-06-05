package io.github.sadellie.sukko.feature.editor

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.EaseInOutExpo
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavKey
import co.touchlab.kermit.Logger
import google.material.design.symbols.CollapseAll
import google.material.design.symbols.DensityMedium
import google.material.design.symbols.DensitySmall
import google.material.design.symbols.Edit
import google.material.design.symbols.ExpandAll
import google.material.design.symbols.LibraryAdd
import google.material.design.symbols.Refresh
import google.material.design.symbols.Save
import google.material.design.symbols.SaveAs
import google.material.design.symbols.Symbols
import io.github.sadellie.sukko.core.common.combineBig
import io.github.sadellie.sukko.core.common.defaultIODispatcher
import io.github.sadellie.sukko.core.common.stateIn
import io.github.sadellie.sukko.core.data.LayerContextProvider
import io.github.sadellie.sukko.core.data.WidgetDataPresetCustomRepository
import io.github.sadellie.sukko.core.data.WidgetDataRepository
import io.github.sadellie.sukko.core.designsystem.LocalWindowSize
import io.github.sadellie.sukko.core.designsystem.theme.Sizes
import io.github.sadellie.sukko.core.model.WidgetData
import io.github.sadellie.sukko.core.model.WidgetDataPreset
import io.github.sadellie.sukko.core.model.WidgetSubscriptionInfo
import io.github.sadellie.sukko.core.model.basic.BrushSource
import io.github.sadellie.sukko.core.model.basic.M3Color
import io.github.sadellie.sukko.core.model.basic.ScriptableColor
import io.github.sadellie.sukko.core.model.basic.ScriptableString
import io.github.sadellie.sukko.core.model.layer.ColdColumnLayer
import io.github.sadellie.sukko.core.model.layer.ColdTextLayer
import io.github.sadellie.sukko.core.model.layer.EvaluatedTextLayer
import io.github.sadellie.sukko.core.model.layer.Layer
import io.github.sadellie.sukko.core.model.layer.evaluateEnabled
import io.github.sadellie.sukko.core.model.modifier.ColdBackgroundColorModifier
import io.github.sadellie.sukko.core.model.modifier.ColdFillMaxSizeModifier
import io.github.sadellie.sukko.core.model.modifier.EvaluatedBackgroundColorModifier
import io.github.sadellie.sukko.core.model.modifier.EvaluatedFillMaxHeightModifier
import io.github.sadellie.sukko.core.model.modifier.EvaluatedFillMaxWidthModifier
import io.github.sadellie.sukko.core.ui.AlertDialogWithText
import io.github.sadellie.sukko.core.ui.AlertDialogWithTextField
import io.github.sadellie.sukko.core.ui.BackHandler
import io.github.sadellie.sukko.core.ui.DropDownMenuWithFilledTonalButton
import io.github.sadellie.sukko.core.ui.NavigateUpButton
import io.github.sadellie.sukko.core.ui.ScaffoldWithTopAppBar
import io.github.sadellie.sukko.core.widget.WidgetInfoRepository
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.common_rename
import io.github.sadellie.sukko.resources.common_save
import io.github.sadellie.sukko.resources.common_widget_name_placeholder
import io.github.sadellie.sukko.resources.editor_collapse_list
import io.github.sadellie.sukko.resources.editor_compact_list_disable
import io.github.sadellie.sukko.resources.editor_compact_list_enable
import io.github.sadellie.sukko.resources.editor_exit_without_saving_exit_button_label
import io.github.sadellie.sukko.resources.editor_exit_without_saving_stay_button_label
import io.github.sadellie.sukko.resources.editor_exit_without_saving_text
import io.github.sadellie.sukko.resources.editor_exit_without_saving_title
import io.github.sadellie.sukko.resources.editor_expand_list
import io.github.sadellie.sukko.resources.editor_force_update
import io.github.sadellie.sukko.resources.editor_grant_permission
import io.github.sadellie.sukko.resources.editor_ignore_and_save
import io.github.sadellie.sukko.resources.editor_load_from_preset
import io.github.sadellie.sukko.resources.editor_missing_notification_listener_permission
import io.github.sadellie.sukko.resources.editor_missing_notification_listener_permission_text
import io.github.sadellie.sukko.resources.editor_rename_widget
import io.github.sadellie.sukko.resources.editor_save_as_preset
import io.github.sadellie.sukko.resources.editor_save_as_preset_require_save
import io.github.sadellie.sukko.resources.editor_widget_name
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
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.core.component.KoinComponent

@Serializable data class EditorRoute(val appWidgetId: Int) : NavKey

@Composable
expect fun EditorScene(
  onNavigateUp: () -> Unit,
  onNavigateToSaveAsPreset: () -> Unit,
  onNavigateToPresetSelector: () -> Unit,
  viewModel: EditorViewModel,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EditorScreen(
  uiState: EditorUIState,
  onNavigateUp: () -> Unit,
  onSave: (isCheckEnabled: Boolean, preview: ImageBitmap?) -> Unit,
  onRename: (String) -> Unit,
  onNavigateToSaveAsPreset: () -> Unit,
  onNavigateToLayer: (Int?) -> Unit,
  onEvent: (EditorEvent) -> Unit,
  onUpdateWidgetDataSaverState: (WidgetDataSaverState) -> Unit,
  onNavigateNotificationListener: () -> Unit,
  onNavigateToPresetSelector: () -> Unit,
) {
  var isInFullscreen by rememberSaveable { mutableStateOf(false) }
  var compactListMode by rememberSaveable { mutableStateOf(false) }
  var showOnLeaveUnsaved by rememberSaveable { mutableStateOf(false) }
  val graphicsLayer = rememberGraphicsLayer()
  val coroutineScope = rememberCoroutineScope()

  fun navBack() = if (!uiState.isWidgetDataSaved) showOnLeaveUnsaved = true else onNavigateUp()

  BackHandler(!uiState.isWidgetDataSaved) { navBack() }

  ScaffoldWithTopAppBar(
    title = {
      Text(
        uiState.widgetData.name
          ?: stringResource(
            Res.string.common_widget_name_placeholder,
            uiState.widgetData.appWidgetId,
          )
      )
    },
    navigationIcon = {
      NavigateUpButton(
        onClick = ::navBack,
        enabled = uiState.widgetDataSaverState !is WidgetDataSaverState.Running,
      )
    },
    actions = {
      EditorScreenTopBarActions(
        onSave = { isCheckEnabled ->
          coroutineScope.launch {
            val preview =
              try {
                graphicsLayer.toImageBitmap()
              } catch (e: Exception) {
                Logger.e(TAG, e) { "Failed to get preview bitmap" }
                null
              }
            onSave(isCheckEnabled, preview)
          }
        },
        onRename = onRename,
        onNavigateToSaveAsPreset = onNavigateToSaveAsPreset,
        onNavigateToPresetSelector = onNavigateToPresetSelector,
        widgetName = uiState.widgetData.name,
        isWidgetDataSaved = uiState.isWidgetDataSaved,
        widgetDataSaverState = uiState.widgetDataSaverState,
        onUpdateWidgetDataSaverState = onUpdateWidgetDataSaverState,
        isInFullscreen = isInFullscreen,
        onFullScreenClick = { isInFullscreen = !isInFullscreen },
        compactListMode = compactListMode,
        onCompactListModeUpdate = { compactListMode = !compactListMode },
        navigateNotificationListener = onNavigateNotificationListener,
      )
    },
  ) { padding ->
    EditorScreenContent(
      modifier = Modifier.fillMaxSize().padding(padding),
      isInFullscreen = isInFullscreen,
      uiState = uiState,
      onNavigateToLayer = onNavigateToLayer,
      onEvent = onEvent,
      compactListMode = compactListMode,
      graphicsLayer = graphicsLayer,
    )
  }

  if (showOnLeaveUnsaved) {
    AlertDialogWithText(
      onDismiss = { showOnLeaveUnsaved = false },
      dismissButtonLabel = stringResource(Res.string.editor_exit_without_saving_stay_button_label),
      onConfirm = onNavigateUp,
      confirmButtonLabel = stringResource(Res.string.editor_exit_without_saving_exit_button_label),
      title = stringResource(Res.string.editor_exit_without_saving_title),
      text = stringResource(Res.string.editor_exit_without_saving_text),
    )
  }
}

@Composable
private fun EditorScreenContent(
  modifier: Modifier,
  isInFullscreen: Boolean,
  uiState: EditorUIState,
  onNavigateToLayer: (Int?) -> Unit,
  onEvent: (EditorEvent) -> Unit,
  compactListMode: Boolean,
  graphicsLayer: GraphicsLayer,
) {
  EditorScreenContentResponsive(
    modifier = modifier,
    firstContent = {
      EditorCanvas(
        modifier = Modifier.fillMaxSize(),
        layers = uiState.evaluatedLayers,
        canvasSize = uiState.canvasSize,
        graphicsLayer = graphicsLayer,
      )
    },
    secondContent = {
      EditorControls(
        modifier =
          Modifier.background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(top = Sizes.large),
        onNavigateToLayer = onNavigateToLayer,
        onEvent = onEvent,
        compactListMode = compactListMode,
        viewerState = uiState.viewerState,
        globals = uiState.widgetData.globals,
      )
    },
    isInFullscreen = isInFullscreen,
  )
}

@Composable
private fun EditorScreenContentResponsive(
  modifier: Modifier,
  isInFullscreen: Boolean,
  firstContent: @Composable () -> Unit,
  secondContent: @Composable () -> Unit,
) {
  val heightOfControls =
    animateFloatAsState(
      targetValue = if (isInFullscreen) 1f else 0.65f,
      animationSpec = tween(easing = EaseInOutExpo),
    )
  if (LocalWindowSize.current.widthSizeClass > WindowWidthSizeClass.Compact) {
    Row(modifier = modifier) {
      Box(Modifier.weight(1f)) { firstContent() }
      Box(Modifier.weight(1f)) { secondContent() }
    }
  } else {
    Column(modifier = modifier) {
      Box(Modifier.weight(1f)) { firstContent() }
      Box(Modifier.fillMaxHeight(heightOfControls.value)) { secondContent() }
    }
  }
}

@Composable
private fun EditorScreenTopBarActions(
  onSave: (isCheckEnabled: Boolean) -> Unit,
  onRename: (String) -> Unit,
  onNavigateToSaveAsPreset: () -> Unit,
  onNavigateToPresetSelector: () -> Unit,
  widgetName: String?,
  isWidgetDataSaved: Boolean,
  onUpdateWidgetDataSaverState: (WidgetDataSaverState) -> Unit,
  navigateNotificationListener: () -> Unit,
  isInFullscreen: Boolean,
  onFullScreenClick: () -> Unit,
  compactListMode: Boolean,
  onCompactListModeUpdate: () -> Unit,
  widgetDataSaverState: WidgetDataSaverState,
) {
  val isSaveEnabled =
    remember(isWidgetDataSaved, widgetDataSaverState) {
      !isWidgetDataSaved && widgetDataSaverState !is WidgetDataSaverState.Running
    }
  FilledIconButton(
    onClick = { onSave(true) },
    enabled = isSaveEnabled,
    shapes = IconButtonDefaults.shapes(),
    modifier =
      Modifier.size(
        IconButtonDefaults.smallContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide)
      ),
  ) {
    Icon(
      imageVector = Symbols.Save,
      contentDescription = null,
      modifier = Modifier.size(IconButtonDefaults.smallIconSize),
    )
  }
  Spacer(Modifier.width(2.dp))
  EditorScreenDropDownMenu(
    onRename = onRename,
    onSave = { onSave(true) },
    onNavigateToSaveAsPreset = onNavigateToSaveAsPreset,
    onNavigateToPresetSelector = onNavigateToPresetSelector,
    isWidgetDataSaved = isWidgetDataSaved,
    widgetDataSaverState = widgetDataSaverState,
    isInFullscreen = isInFullscreen,
    onFullScreenClick = onFullScreenClick,
    compactListMode = compactListMode,
    onCompactListModeClick = onCompactListModeUpdate,
    widgetName = widgetName,
  )

  if (widgetDataSaverState is WidgetDataSaverState.MissingNotificationListener) {
    AlertDialogWithText(
      onDismiss = { onSave(false) },
      onDismissDialog = { onUpdateWidgetDataSaverState(WidgetDataSaverState.NotRunning) },
      dismissButtonLabel = stringResource(Res.string.editor_ignore_and_save),
      onConfirm = navigateNotificationListener,
      confirmButtonLabel = stringResource(Res.string.editor_grant_permission),
      title = stringResource(Res.string.editor_missing_notification_listener_permission),
      text = stringResource(Res.string.editor_missing_notification_listener_permission_text),
    )
  }
}

@Composable
private fun EditorScreenDropDownMenu(
  onRename: (newWidgetName: String) -> Unit,
  onSave: () -> Unit,
  onNavigateToSaveAsPreset: () -> Unit,
  onNavigateToPresetSelector: () -> Unit,
  isWidgetDataSaved: Boolean,
  widgetDataSaverState: WidgetDataSaverState,
  isInFullscreen: Boolean,
  onFullScreenClick: () -> Unit,
  compactListMode: Boolean,
  onCompactListModeClick: () -> Unit,
  widgetName: String?,
) {
  var dialogState by rememberSaveable { mutableStateOf<EditorScreenAlertDialogState?>(null) }

  DropDownMenuWithFilledTonalButton {
    DropdownMenuItem(
      text = { Text(stringResource(Res.string.common_rename)) },
      onClick = {
        dialogState = EditorScreenAlertDialogState.RENAME
        closeMenu()
      },
      leadingIcon = { Icon(Symbols.Edit, contentDescription = null) },
    )
    DropdownMenuItem(
      text = { Text(stringResource(Res.string.editor_save_as_preset)) },
      onClick = {
        if (isWidgetDataSaved) {
          onNavigateToSaveAsPreset()
        } else {
          dialogState = EditorScreenAlertDialogState.SAVE_AS_PRESET_REQUIRE_SAVE
        }
        closeMenu()
      },
      leadingIcon = { Icon(Symbols.SaveAs, contentDescription = null) },
      enabled = widgetDataSaverState !is WidgetDataSaverState.Running,
    )
    DropdownMenuItem(
      text = { Text(stringResource(Res.string.editor_load_from_preset)) },
      onClick = {
        onNavigateToPresetSelector()
        closeMenu()
      },
      leadingIcon = { Icon(Symbols.LibraryAdd, contentDescription = null) },
    )
    if (LocalWindowSize.current.widthSizeClass == WindowWidthSizeClass.Compact) {
      AnimatedContent(isInFullscreen) {
        val label = if (it) Res.string.editor_collapse_list else Res.string.editor_expand_list
        val icon = if (it) Symbols.CollapseAll else Symbols.ExpandAll
        DropdownMenuItem(
          text = { Text(stringResource(label)) },
          leadingIcon = { Icon(icon, contentDescription = null) },
          onClick = onFullScreenClick,
        )
      }
    }
    AnimatedContent(compactListMode) {
      val label =
        if (it) Res.string.editor_compact_list_disable else Res.string.editor_compact_list_enable
      val icon = if (it) Symbols.DensityMedium else Symbols.DensitySmall
      DropdownMenuItem(
        text = { Text(stringResource(label)) },
        leadingIcon = { Icon(icon, contentDescription = null) },
        onClick = onCompactListModeClick,
      )
    }
    DropdownMenuItem(
      text = { Text(stringResource(Res.string.editor_force_update)) },
      leadingIcon = { Icon(Symbols.Refresh, contentDescription = null) },
      onClick = onSave,
    )
  }

  when (dialogState) {
    EditorScreenAlertDialogState.RENAME ->
      AlertDialogWithTextField(
        title = stringResource(Res.string.editor_rename_widget),
        onDismiss = { dialogState = null },
        onConfirm = { onRename(it) },
        icon = Symbols.Edit,
        confirmButtonLabel = stringResource(Res.string.common_rename),
        textFieldState = rememberTextFieldState(widgetName ?: ""),
        textFieldLabel = stringResource(Res.string.editor_widget_name),
      )
    EditorScreenAlertDialogState.SAVE_AS_PRESET_REQUIRE_SAVE ->
      AlertDialogWithText(
        title = stringResource(Res.string.editor_save_as_preset),
        onDismiss = { dialogState = null },
        onConfirm = { onSave() },
        icon = Symbols.Save,
        confirmButtonLabel = stringResource(Res.string.common_save),
        text = stringResource(Res.string.editor_save_as_preset_require_save),
      )
    null -> Unit
  }
}

private enum class EditorScreenAlertDialogState {
  RENAME,
  SAVE_AS_PRESET_REQUIRE_SAVE,
}

class EditorViewModel(
  private val appWidgetId: Int,
  private val widgetDataRepository: WidgetDataRepository,
  private val widgetDataPresetCustomRepository: WidgetDataPresetCustomRepository,
  private val widgetInfoRepository: WidgetInfoRepository,
) : ViewModel(), KoinComponent {
  private val layerContextProvider = LayerContextProvider()
  private val _canvasSize = MutableStateFlow<DpSize?>(null)
  private val _currentWidgetData = MutableStateFlow<WidgetData?>(null)
  private val _currentlySelectedLayerId = MutableStateFlow<Int?>(null)
  private val _viewerState =
    combine(_currentWidgetData, _currentlySelectedLayerId) { widgetData, selectedId ->
        val currentLayers =
          widgetData?.layers
            ?: return@combine ViewerState(
              currentLayer = null,
              parentLayer = null,
              breadcrumbs = emptyList(),
              loadedLayers = emptyList(),
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
        return@combine ViewerState(currentLayer, parentLayer, newBreadcrumbs, newLoadedLayers)
      }
      .flowOn(Dispatchers.Default)

  private val _layerContext =
    flow {
        while (true) {
          val layerContext = layerContextProvider.provide(viewModelScope.coroutineContext)
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
        currentWidgetData?.layers?.evaluateEnabled(layerContext, currentWidgetData.globals)
          ?: flowOf(emptyList())
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
        Logger.e(TAG) { "loadFromPreset: $presetId not found" }
        return@launch
      }
      _currentWidgetData.update { it?.copy(layers = preset.layers, globals = preset.globals) }
    }

  internal fun onUpdateWidgetSize() =
    viewModelScope.launch {
      val newWidgetSize = widgetInfoRepository.getWidgetSize(appWidgetId)
      _canvasSize.update { newWidgetSize }
    }

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

  internal fun saveWidgetData(
    isChecksEnabled: Boolean,
    preview: ImageBitmap?,
    isNotificationListenerEnabled: Boolean,
    onSaveCallback: (WidgetSubscriptionInfo) -> Unit,
  ) =
    viewModelScope.launch(Dispatchers.Default) {
      val widgetData = _currentWidgetData.value ?: return@launch
      val evaluatedLayers = _evaluatedLayers.value
      _widgetDataSaverState.update { WidgetDataSaverState.Running }
      try {
        val widgetSubscriptionInfo = generateWidgetSubscriptionInfo(widgetData)
        if (widgetSubscriptionInfo.isMedia && !isNotificationListenerEnabled && isChecksEnabled) {
          _widgetDataSaverState.update { WidgetDataSaverState.MissingNotificationListener }
          return@launch
        }

        widgetDataRepository.save(widgetData, evaluatedLayers, preview)
        onSaveCallback(widgetSubscriptionInfo)
        _widgetDataSaverState.update { WidgetDataSaverState.NotRunning }
        _currentWidgetData.update { widgetData }
        _cachedDataFromDatabase.update { widgetData }
      } catch (e: Exception) {
        Logger.e(TAG, e) { "Failed to save widget data" }
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

private const val TAG = "EditorScene"
private const val LAYER_EVALUATION_DEBOUNCE = 300L
private const val EVALUATED_LAYERS_UPDATE_RATE_MS = 10_000L

@Composable
@Preview
private fun PreviewEditorScreenContentResponsive() {
  var isInFullScreen by remember { mutableStateOf(false) }
  EditorScreenContentResponsive(
    modifier = Modifier.fillMaxSize(),
    isInFullscreen = isInFullScreen,
    firstContent = {
      BoxWithConstraints(modifier = Modifier.background(Color.White).fillMaxSize()) {
        Text(maxHeight.toString())
      }
    },
    secondContent = {
      BoxWithConstraints(
        modifier =
          Modifier.clickable { isInFullScreen = !isInFullScreen }
            .background(Color.LightGray)
            .fillMaxSize()
      ) {
        Text(maxHeight.toString())
      }
    },
  )
}

@Composable
@Preview
private fun PreviewEditorScreen() {
  EditorScreen(
    uiState =
      EditorUIState(
        widgetData = WidgetData(appWidgetId = 1, name = "Widget 1"),
        widgetDataSaverState = WidgetDataSaverState.NotRunning,
        isWidgetDataSaved = false,
        evaluatedLayers =
          listOf(
            EvaluatedTextLayer(
              id = 0,
              parentId = null,
              name = "Text 2",
              widgetModifiers =
                listOf(
                  EvaluatedBackgroundColorModifier(
                    id = 0,
                    color = SolidColor(MaterialTheme.colorScheme.background),
                    shape = RectangleShape,
                  ),
                  EvaluatedFillMaxWidthModifier(id = 1, fraction = 1f),
                  EvaluatedFillMaxHeightModifier(id = 2, fraction = 1f),
                ),
              clickActions = emptyList(),
              textStyle = TextStyle(fontSize = 16.sp, textAlign = TextAlign.Start),
              text = "Basic text layer 22",
              textColor = SolidColor(MaterialTheme.colorScheme.onBackground),
            )
          ),
        canvasSize = DpSize(200.dp, 400.dp),
        viewerState =
          ViewerState(
            currentLayer = ColdTextLayer(0, null),
            parentLayer = null,
            breadcrumbs = List(7) { ColdColumnLayer(it, null) },
            loadedLayers =
              listOf(
                ColdColumnLayer(
                  id = 0,
                  parentId = null,
                  widgetModifiers =
                    listOf(
                      ColdBackgroundColorModifier(
                        id = 0,
                        color = BrushSource.SolidColor(ScriptableColor.FixedM3(M3Color.BACKGROUND)),
                      ),
                      ColdFillMaxSizeModifier(id = 1),
                    ),
                ),
                ColdTextLayer(id = 1, parentId = 0, text = ScriptableString.Fixed("text")),
                ColdTextLayer(id = 2, parentId = 0, text = ScriptableString.Fixed("text")),
                ColdTextLayer(id = 3, parentId = 0, text = ScriptableString.Fixed("text")),
                ColdTextLayer(id = 4, parentId = 0, text = ScriptableString.Fixed("text")),
                ColdTextLayer(id = 5, parentId = 0, text = ScriptableString.Fixed("text")),
                ColdTextLayer(id = 6, parentId = 0, text = ScriptableString.Fixed("text")),
              ),
          ),
      ),
    onNavigateUp = {},
    onSave = { _, _ -> },
    onNavigateToLayer = {},
    onEvent = {},
    onRename = {},
    onNavigateToSaveAsPreset = {},
    onUpdateWidgetDataSaverState = {},
    onNavigateNotificationListener = {},
    onNavigateToPresetSelector = {},
  )
}
