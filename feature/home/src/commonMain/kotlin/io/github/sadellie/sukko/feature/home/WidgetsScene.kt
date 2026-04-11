package io.github.sadellie.sukko.feature.home

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarExitDirection
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.annotation.ExperimentalCoilApi
import google.material.design.symbols.Add
import google.material.design.symbols.EmojiPeople
import google.material.design.symbols.Settings
import google.material.design.symbols.Symbols
import io.github.sadellie.sukko.core.common.collectAsStateWithLifecycleKMP
import io.github.sadellie.sukko.core.common.stateIn
import io.github.sadellie.sukko.core.data.WidgetDataRepository
import io.github.sadellie.sukko.core.data.WidgetInfoRepository
import io.github.sadellie.sukko.core.designsystem.LocalFilesDirPath
import io.github.sadellie.sukko.core.designsystem.PreviewScreenSizesContainer
import io.github.sadellie.sukko.core.designsystem.theme.Sizes
import io.github.sadellie.sukko.core.model.WidgetData
import io.github.sadellie.sukko.core.ui.EmptyScreen
import io.github.sadellie.sukko.core.ui.ScaffoldWithLargeTopAppBar
import io.github.sadellie.sukko.core.ui.ScenePlaceholder
import io.github.sadellie.sukko.core.ui.WidgetDataList
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.common_widget_name_placeholder
import io.github.sadellie.sukko.resources.home_add_widget
import io.github.sadellie.sukko.resources.home_widgets_placeholder_text
import io.github.sadellie.sukko.resources.home_widgets_placeholder_title
import io.github.sadellie.sukko.resources.home_widgets_title
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun WidgetsScene(
  onWidgetClick: (Int) -> Unit,
  onNavigateToSettings: () -> Unit,
  toolBarNestedScrollConnection: NestedScrollConnection,
  onAddWidget: () -> Unit,
) {
  val viewModel = koinViewModel<WidgetsViewModel>()
  when (val uiState = viewModel.uiState.collectAsStateWithLifecycleKMP().value) {
    null -> EmptyScreen()
    else ->
      WidgetsScreen(
        uiState = uiState,
        onWidgetClick = onWidgetClick,
        onAddWidget = onAddWidget,
        onNavigateToSettings = onNavigateToSettings,
        toolBarNestedScrollConnection = toolBarNestedScrollConnection,
      )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WidgetsScreen(
  uiState: WidgetsUIState,
  onWidgetClick: (widgetId: Int) -> Unit,
  onAddWidget: () -> Unit,
  onNavigateToSettings: () -> Unit,
  toolBarNestedScrollConnection: NestedScrollConnection,
) {
  val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
  ScaffoldWithLargeTopAppBar(
    title = { Text(stringResource(Res.string.home_widgets_title)) },
    scrollBehavior = scrollBehavior,
    actions = {
      IconButton(
        modifier = Modifier.size(IconButtonDefaults.smallContainerSize()),
        shapes = IconButtonDefaults.shapes(),
        onClick = onAddWidget,
      ) {
        Icon(
          imageVector = Symbols.Add,
          contentDescription = null,
          modifier = Modifier.size(IconButtonDefaults.mediumIconSize),
        )
      }

      Spacer(Modifier.width(2.dp))

      FilledTonalIconButton(
        modifier = Modifier.size(IconButtonDefaults.smallContainerSize()),
        shapes = IconButtonDefaults.shapes(),
        onClick = onNavigateToSettings,
      ) {
        Icon(
          imageVector = Symbols.Settings,
          contentDescription = null,
          modifier = Modifier.size(IconButtonDefaults.mediumIconSize),
        )
      }
    },
  ) { padding ->
    if (uiState.widgetDataList.isEmpty()) {
      ScenePlaceholder(
        modifier = Modifier.padding(padding).padding(Sizes.large).fillMaxSize(),
        icon = Symbols.EmojiPeople,
        title = stringResource(Res.string.home_widgets_placeholder_title),
        text = stringResource(Res.string.home_widgets_placeholder_text),
        onClick = onAddWidget,
        actionLabel = stringResource(Res.string.home_add_widget),
      )
      return@ScaffoldWithLargeTopAppBar
    }
    val filesDirPath = LocalFilesDirPath.current

    WidgetDataList(
      modifier =
        Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
          .nestedScroll(toolBarNestedScrollConnection)
          .consumeWindowInsets(padding)
          .padding(horizontal = Sizes.large)
          .fillMaxSize(),
      widgets = uiState.widgetDataList,
      key = { it.appWidgetId },
      previewSrc = { it.getPreviewPath(filesDirPath).toString() },
      name = {
        it.name ?: stringResource(Res.string.common_widget_name_placeholder, it.appWidgetId)
      },
      contentPadding = padding,
      onClick = { onWidgetClick(it.appWidgetId) },
    )
  }
}

internal class WidgetsViewModel(
  widgetInfoRepository: WidgetInfoRepository,
  widgetDataRepository: WidgetDataRepository,
) : ViewModel() {
  private val _allWidgetData = widgetDataRepository.allWidgetData(decodeExtra = false)
  private val _allWidgetInfo = widgetInfoRepository.allWidgetIds()

  private val _currentWidgetsOnScreen =
    combine(_allWidgetData, _allWidgetInfo) { allWidgetData, allWidgetInfo ->
        allWidgetInfo.map { id ->
          val widgetData = allWidgetData.firstOrNull { widgetData -> widgetData.appWidgetId == id }
          widgetData ?: WidgetData(appWidgetId = id)
        }
      }
      .distinctUntilChanged()
      .flowOn(Dispatchers.Default)

  @OptIn(ExperimentalCoroutinesApi::class)
  internal val uiState =
    _currentWidgetsOnScreen
      .mapLatest { widgetDataList -> WidgetsUIState(widgetDataList = widgetDataList) }
      .stateIn(viewModelScope, null)
}

internal data class WidgetsUIState(val widgetDataList: List<WidgetData>)

@OptIn(ExperimentalCoilApi::class)
@Preview
@PreviewScreenSizes
@Composable
private fun PreviewWidgetsScreen(
  @PreviewParameter(WidgetsUIStateCollection::class) uiState: WidgetsUIState
) = PreviewScreenSizesContainer {
  WidgetsScreen(
    uiState = uiState,
    onWidgetClick = {},
    onAddWidget = {},
    onNavigateToSettings = {},
    toolBarNestedScrollConnection =
      FloatingToolbarDefaults.exitAlwaysScrollBehavior(FloatingToolbarExitDirection.Bottom),
  )
}

@Suppress("MagicNumber")
private class WidgetsUIStateCollection(
  override val values: Sequence<WidgetsUIState> =
    sequenceOf(
      WidgetsUIState(
        widgetDataList = List(10) { WidgetData(appWidgetId = it, name = "Widget $it") }
      ),
      WidgetsUIState(widgetDataList = emptyList()),
    )
) : PreviewParameterProvider<WidgetsUIState>
