package io.github.sadellie.sukko.feature.editor

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
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.touchlab.kermit.Logger
import google.material.design.symbols.Save
import google.material.design.symbols.Symbols
import io.github.pingpongboss.explodedlayers.ExperimentalExplodedLayersApi
import io.github.pingpongboss.explodedlayers.ExplodedLayersState
import io.github.pingpongboss.explodedlayers.rememberExplodedLayersState
import io.github.sadellie.sukko.core.common.collectAsStateWithLifecycleKMP
import io.github.sadellie.sukko.core.designsystem.LocalWindowSize
import io.github.sadellie.sukko.core.designsystem.PreviewScreenSizesContainer
import io.github.sadellie.sukko.core.designsystem.theme.Sizes
import io.github.sadellie.sukko.core.model.WidgetData
import io.github.sadellie.sukko.core.model.basic.BrushSource
import io.github.sadellie.sukko.core.model.basic.M3Color
import io.github.sadellie.sukko.core.model.basic.ScriptableColor
import io.github.sadellie.sukko.core.model.basic.ScriptableString
import io.github.sadellie.sukko.core.model.layer.ColdColumnLayer
import io.github.sadellie.sukko.core.model.layer.ColdTextLayer
import io.github.sadellie.sukko.core.model.layer.EvaluatedTextLayer
import io.github.sadellie.sukko.core.model.modifier.ColdBackgroundColorModifier
import io.github.sadellie.sukko.core.model.modifier.ColdFillMaxSizeModifier
import io.github.sadellie.sukko.core.model.modifier.EvaluatedBackgroundColorModifier
import io.github.sadellie.sukko.core.model.modifier.EvaluatedFillMaxHeightModifier
import io.github.sadellie.sukko.core.model.modifier.EvaluatedFillMaxWidthModifier
import io.github.sadellie.sukko.core.ui.AlertDialogWithText
import io.github.sadellie.sukko.core.ui.BackHandler
import io.github.sadellie.sukko.core.ui.LoadingScaffoldWithTopAppBar
import io.github.sadellie.sukko.core.ui.NavigateUpButton
import io.github.sadellie.sukko.core.ui.ScaffoldWithTopAppBar
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.common_widget_name_placeholder
import io.github.sadellie.sukko.resources.editor_exit_without_saving_exit_button_label
import io.github.sadellie.sukko.resources.editor_exit_without_saving_stay_button_label
import io.github.sadellie.sukko.resources.editor_exit_without_saving_text
import io.github.sadellie.sukko.resources.editor_exit_without_saving_title
import io.github.sadellie.sukko.resources.editor_grant_permission
import io.github.sadellie.sukko.resources.editor_ignore_and_save
import io.github.sadellie.sukko.resources.editor_missing_notification_listener_permission
import io.github.sadellie.sukko.resources.editor_missing_notification_listener_permission_text
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun EditorScene(
  onNavigateUp: () -> Unit,
  onNavigateToSaveAsPreset: () -> Unit,
  onNavigateToPresetSelector: () -> Unit,
  onNavigateNotificationListener: () -> Unit,
  onNavigateToWidgetInfo: () -> Unit,
  viewModel: EditorViewModel,
) {
  LaunchedEffect(Unit) { viewModel.onUpdateWidgetSize() }
  LaunchedEffect(Unit) { viewModel.periodicLayerRefresh() }

  when (val uiState = viewModel.uiState.collectAsStateWithLifecycleKMP().value) {
    null -> LoadingScaffoldWithTopAppBar(onNavigateUp = onNavigateUp, disableBack = false)
    else ->
      EditorScreen(
        uiState = uiState,
        onNavigateUp = onNavigateUp,
        onSave = viewModel::saveWidgetData,
        onRename = viewModel::renameWidget,
        onNavigateToSaveAsPreset = onNavigateToSaveAsPreset,
        onNavigateToLayer = viewModel::onNavigateToLayer,
        onEvent = viewModel::onEvent,
        onUpdateWidgetDataSaverState = viewModel::updateWidgetSaverState,
        onNavigateNotificationListener = onNavigateNotificationListener,
        onNavigateToPresetSelector = onNavigateToPresetSelector,
        onHighlightSelectedLayerClick = viewModel::updateHighlightSelectedLayer,
        onNavigateToWidgetInfo = onNavigateToWidgetInfo,
      )
  }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalExplodedLayersApi::class)
@Composable
internal fun EditorScreen(
  uiState: EditorUIState,
  onNavigateUp: () -> Unit,
  onSave: (isForced: Boolean, preview: ImageBitmap?) -> Unit,
  onRename: (String) -> Unit,
  onNavigateToSaveAsPreset: () -> Unit,
  onNavigateToLayer: (Int?) -> Unit,
  onEvent: (EditorEvent) -> Unit,
  onUpdateWidgetDataSaverState: (WidgetDataSaverState) -> Unit,
  onNavigateNotificationListener: () -> Unit,
  onNavigateToPresetSelector: () -> Unit,
  onNavigateToWidgetInfo: () -> Unit,
  onHighlightSelectedLayerClick: (Boolean) -> Unit,
) {
  var isInFullscreen by rememberSaveable { mutableStateOf(false) }
  var compactListMode by rememberSaveable { mutableStateOf(false) }
  var showOnLeaveUnsaved by rememberSaveable { mutableStateOf(false) }
  val graphicsLayer = rememberGraphicsLayer()
  val coroutineScope = rememberCoroutineScope()
  var explode by rememberSaveable { mutableStateOf(false) }
  val explodedLayersState = rememberExplodedLayersState(interactive = true, initialSpread = 0f)
  LaunchedEffect(explode) { explodedLayersState.explode(explode) }

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
        onSave = { isForced ->
          coroutineScope.launch {
            val preview =
              try {
                graphicsLayer.toImageBitmap()
              } catch (e: Exception) {
                Logger.e(throwable = e, tag = TAG) { "Failed to get preview bitmap" }
                null
              }
            onSave(isForced, preview)
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
        onFullScreenClick = { isInFullscreen = it },
        compactListMode = compactListMode,
        onCompactListModeUpdate = { compactListMode = it },
        navigateNotificationListener = onNavigateNotificationListener,
        onHighlightSelectedLayerClick = onHighlightSelectedLayerClick,
        highlightSelectedLayer = uiState.viewerState.highlightSelectedLayer,
        onNavigateToWidgetInfo = onNavigateToWidgetInfo,
        explodeLayers = explode,
        onExplodeLayerClick = { explode = it },
      )
    },
  ) { padding ->
    EditorScreenContent(
      modifier = Modifier.fillMaxSize().padding(padding).consumeWindowInsets(padding),
      isInFullscreen = isInFullscreen,
      uiState = uiState,
      onNavigateToLayer = onNavigateToLayer,
      onEvent = onEvent,
      compactListMode = compactListMode,
      graphicsLayer = graphicsLayer,
      explodedLayersState = explodedLayersState,
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
  explodedLayersState: ExplodedLayersState,
) {
  EditorScreenContentResponsive(
    modifier = modifier,
    firstContent = {
      EditorCanvas(
        modifier = Modifier.fillMaxSize(),
        layers = uiState.evaluatedLayers,
        canvasSize = uiState.canvasSize,
        graphicsLayer = graphicsLayer,
        renderOption = uiState.viewerState.asRenderOptions(),
        explodedLayersState = explodedLayersState,
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
  onSave: (isForced: Boolean) -> Unit,
  onRename: (String) -> Unit,
  onNavigateToSaveAsPreset: () -> Unit,
  onNavigateToPresetSelector: () -> Unit,
  onNavigateToWidgetInfo: () -> Unit,
  widgetName: String?,
  isWidgetDataSaved: Boolean,
  onUpdateWidgetDataSaverState: (WidgetDataSaverState) -> Unit,
  navigateNotificationListener: () -> Unit,
  isInFullscreen: Boolean,
  onFullScreenClick: (Boolean) -> Unit,
  compactListMode: Boolean,
  onCompactListModeUpdate: (Boolean) -> Unit,
  onHighlightSelectedLayerClick: (Boolean) -> Unit,
  highlightSelectedLayer: Boolean,
  widgetDataSaverState: WidgetDataSaverState,
  explodeLayers: Boolean,
  onExplodeLayerClick: (Boolean) -> Unit,
) {
  val isSaveEnabled =
    remember(isWidgetDataSaved, widgetDataSaverState) {
      !isWidgetDataSaved && widgetDataSaverState !is WidgetDataSaverState.Running
    }
  FilledIconButton(
    onClick = { onSave(false) },
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
    onForceUpdate = { onSave(true) },
    onNavigateToSaveAsPreset = onNavigateToSaveAsPreset,
    onNavigateToPresetSelector = onNavigateToPresetSelector,
    isWidgetDataSaved = isWidgetDataSaved,
    widgetDataSaverState = widgetDataSaverState,
    isInFullscreen = isInFullscreen,
    onFullScreenClick = onFullScreenClick,
    compactListMode = compactListMode,
    onCompactListModeClick = onCompactListModeUpdate,
    widgetName = widgetName,
    onHighlightSelectedLayerClick = onHighlightSelectedLayerClick,
    highlightSelectedLayer = highlightSelectedLayer,
    onNavigateToWidgetInfo = onNavigateToWidgetInfo,
    explodeLayers = explodeLayers,
    onExplodeLayerClick = onExplodeLayerClick,
  )

  if (widgetDataSaverState is WidgetDataSaverState.MissingNotificationListener) {
    AlertDialogWithText(
      onDismiss = { onSave(true) },
      onDismissDialog = { onUpdateWidgetDataSaverState(WidgetDataSaverState.NotRunning) },
      dismissButtonLabel = stringResource(Res.string.editor_ignore_and_save),
      onConfirm = navigateNotificationListener,
      confirmButtonLabel = stringResource(Res.string.editor_grant_permission),
      title = stringResource(Res.string.editor_missing_notification_listener_permission),
      text = stringResource(Res.string.editor_missing_notification_listener_permission_text),
    )
  }
}

private fun ExplodedLayersState.explode(value: Boolean) {
  this.spread = if (value) EXPLODED_LAYER_SPREAD_ON else EXPLODED_LAYER_SPREAD_OFF
}

private const val EXPLODED_LAYER_SPREAD_ON = 0.9f
private const val EXPLODED_LAYER_SPREAD_OFF = 0f
private const val TAG = "EditorScene"

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
              textStyle = TextStyle(fontSize = 16.sp, textAlign = TextAlign.Start),
              text = "Basic text layer 22",
              textColor = SolidColor(MaterialTheme.colorScheme.onBackground),
            )
          ),
        canvasSize = DpSize(200.dp, 400.dp),
        viewerState =
          ViewerState(
            currentLayer = ColdTextLayer(0, null),
            highlightSelectedLayer = true,
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
    onHighlightSelectedLayerClick = {},
    onNavigateToWidgetInfo = {},
  )
}

@PreviewScreenSizes
@Preview
@Composable
private fun PreviewEditorScreenSizes(@PreviewParameter(PreviewCollection::class) dpSize: DpSize) {
  PreviewScreenSizesContainer {
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
                text = "Basic text layer 22",
                textColor = SolidColor(MaterialTheme.colorScheme.onBackground),
              )
            ),
          canvasSize = dpSize,
          viewerState =
            ViewerState(
              currentLayer = ColdTextLayer(2, 1),
              highlightSelectedLayer = true,
              parentLayer = null,
              breadcrumbs =
                listOf(ColdColumnLayer(0, null), ColdColumnLayer(1, 0), ColdTextLayer(2, 1)),
              loadedLayers =
                listOf(
                  ColdColumnLayer(
                    id = 0,
                    parentId = null,
                    widgetModifiers =
                      listOf(
                        ColdBackgroundColorModifier(
                          id = 0,
                          color =
                            BrushSource.SolidColor(ScriptableColor.FixedM3(M3Color.BACKGROUND)),
                        ),
                        ColdFillMaxSizeModifier(id = 1),
                      ),
                  ),
                  ColdTextLayer(id = 1, parentId = 0, text = ScriptableString.Fixed("text")),
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
      onHighlightSelectedLayerClick = {},
      onNavigateToWidgetInfo = {},
    )
  }
}

private class PreviewCollection(
  override val values: Sequence<DpSize> =
    sequenceOf(
      DpSize.Zero,
      DpSize(200.dp, 200.dp),
      DpSize(200.dp, 400.dp),
      DpSize(200.dp, 600.dp),
      DpSize(600.dp, 200.dp),
      DpSize(400.dp, 200.dp),
    )
) : PreviewParameterProvider<DpSize>
