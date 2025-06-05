package io.github.sadellie.sukko.feature.editor

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults.ScreenOffset
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.github.sadellie.sukko.core.designsystem.Preview2
import io.github.sadellie.sukko.core.designsystem.theme.Sizes
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.basic.GlobalValue
import io.github.sadellie.sukko.core.model.basic.LocalScriptableDisplay
import io.github.sadellie.sukko.core.model.basic.ScriptableDisplay
import io.github.sadellie.sukko.core.model.basic.ScriptableString
import io.github.sadellie.sukko.core.model.layer.ColdBoxLayer
import io.github.sadellie.sukko.core.model.layer.ColdColumnLayer
import io.github.sadellie.sukko.core.model.layer.ColdImageLayer
import io.github.sadellie.sukko.core.model.layer.ColdProgressBarLayer
import io.github.sadellie.sukko.core.model.layer.ColdRowLayer
import io.github.sadellie.sukko.core.model.layer.ColdTextLayer
import io.github.sadellie.sukko.core.ui.Tab
import io.github.sadellie.sukko.core.ui.TabsSwitcher
import io.github.sadellie.sukko.feature.editor.clicks.EditorClickActionsList
import io.github.sadellie.sukko.feature.editor.modifiers.EditorModifiersList
import io.github.sadellie.sukko.feature.editor.parameters.EditorParametersList
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.editor_tab_click_actions
import io.github.sadellie.sukko.resources.editor_tab_globals
import io.github.sadellie.sukko.resources.editor_tab_layers
import io.github.sadellie.sukko.resources.editor_tab_modifiers
import io.github.sadellie.sukko.resources.editor_tab_parameters
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun EditorControls(
  modifier: Modifier,
  onNavigateToLayer: (layerId: Int?) -> Unit,
  onEvent: (EditorEvent) -> Unit,
  compactListMode: Boolean,
  viewerState: ViewerState,
  globals: Globals,
) {
  CompositionLocalProvider(
    LocalScriptableDisplay provides remember(globals) { ScriptableDisplay(globals) }
  ) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(Sizes.small)) {
      EditorBreadcrumbs(
        modifier =
          Modifier.padding(horizontal = Sizes.large)
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceBright)
            .fillMaxWidth(),
        onClick = onNavigateToLayer,
        breadcrumbs = viewerState.breadcrumbs,
      )
      EditorPages(
        modifier = Modifier.fillMaxWidth().weight(1f),
        viewerState = viewerState,
        onEvent = onEvent,
        onNavigateToLayer = onNavigateToLayer,
        compactListMode = compactListMode,
        globals = globals,
      )
    }
  }
}

@Composable
private fun EditorPages(
  modifier: Modifier,
  viewerState: ViewerState,
  onEvent: (EditorEvent) -> Unit,
  onNavigateToLayer: (layerId: Int?) -> Unit,
  compactListMode: Boolean,
  globals: Globals,
) {
  val spatialSpec = MaterialTheme.motionScheme.defaultSpatialSpec<Float>()
  AnimatedContent(
    modifier = modifier,
    targetState = viewerState.currentLayer,
    contentKey = { it?.id },
    transitionSpec = { fadeIn(spatialSpec) togetherWith fadeOut(spatialSpec) },
  ) { currentLayer ->
    Box(modifier = Modifier.fillMaxSize()) {
      val tabs =
        remember(currentLayer) {
          when (currentLayer) {
            is ColdRowLayer,
            is ColdBoxLayer,
            is ColdColumnLayer ->
              listOf(LayersTab, ParametersTab, ModifiersTab, ClicksTab, GlobalsTab)
            is ColdTextLayer,
            is ColdImageLayer,
            is ColdProgressBarLayer -> listOf(ParametersTab, ModifiersTab, ClicksTab, GlobalsTab)
            null -> listOf(LayersTab, GlobalsTab)
          }
        }
      val pagerState = rememberPagerState { tabs.size }
      val pagerScope = rememberCoroutineScope()
      HorizontalPager(state = pagerState, verticalAlignment = Alignment.Top) { page ->
        val tabModifier = Modifier.padding(horizontal = Sizes.large).fillMaxSize()
        val currentTab = tabs[page]
        val contentPadding = PaddingValues(bottom = 96.dp) // ScreenOffset * 2 + Toolbar height
        when (currentTab) {
          LayersTab ->
            EditorLayersList(
              modifier = tabModifier,
              onNavigateToLayer = onNavigateToLayer,
              onEvent = onEvent,
              layers = viewerState.loadedLayers,
              parentLayerId = currentLayer?.id,
              compactListMode = compactListMode,
              contentPadding = contentPadding,
            )

          ParametersTab if currentLayer != null ->
            EditorParametersList(
              modifier = tabModifier,
              onUpdateLayer = { onEvent(EditorEvent.LayerAction.Update(it)) },
              layer = currentLayer,
              compactListMode = compactListMode,
              bottomContentPadding = contentPadding.calculateBottomPadding(),
              globals = globals,
            )

          ModifiersTab if currentLayer != null ->
            EditorModifiersList(
              modifier = tabModifier,
              layer = currentLayer,
              parentLayer = viewerState.parentLayer,
              onEvent = onEvent,
              compactListMode = compactListMode,
              contentPadding = contentPadding,
              globals = globals,
            )

          ClicksTab if currentLayer != null ->
            EditorClickActionsList(
              modifier = tabModifier,
              layer = currentLayer,
              onEvent = onEvent,
              compactListMode = compactListMode,
              contentPadding = contentPadding,
              globals = globals,
            )

          GlobalsTab ->
            EditorGlobalsList(
              modifier = tabModifier,
              contentPadding = contentPadding,
              onEvent = onEvent,
              globals = globals,
            )
        }
      }

      if (tabs.size > 1) {
        TabsSwitcher(
          modifier =
            Modifier.horizontalScroll(rememberScrollState())
              .align(Alignment.BottomCenter)
              .offset(y = -ScreenOffset),
          tabs = tabs,
          onClick = {
            pagerScope.launch {
              val target = tabs.indexOf(it).coerceAtLeast(0)
              pagerState.animateScrollToPage(target)
            }
          },
          selectedTabIndex = pagerState.currentPage,
        )
      }
    }
  }
}

private object LayersTab : Tab {
  override val icon = null
  override val label = Res.string.editor_tab_layers
}

private object ParametersTab : Tab {
  override val icon = null
  override val label = Res.string.editor_tab_parameters
}

private object ModifiersTab : Tab {
  override val icon = null
  override val label = Res.string.editor_tab_modifiers
}

private object ClicksTab : Tab {
  override val icon = null
  override val label = Res.string.editor_tab_click_actions
}

private object GlobalsTab : Tab {
  override val icon = null
  override val label = Res.string.editor_tab_globals
}

@Preview
@Composable
private fun PreviewEditorControlsAtRoot() = Preview2 {
  EditorControls(
    modifier = Modifier.fillMaxSize(),
    onNavigateToLayer = {},
    onEvent = {},
    compactListMode = false,
    viewerState =
      ViewerState(
        currentLayer = null,
        parentLayer = null,
        breadcrumbs = emptyList(),
        loadedLayers = List(3) { ColdTextLayer(it) },
      ),
    globals = Globals(),
  )
}

@Preview
@Composable
private fun PreviewEditorControlsInLayer() = Preview2 {
  EditorControls(
    modifier = Modifier.fillMaxSize(),
    onNavigateToLayer = {},
    onEvent = {},
    compactListMode = false,
    viewerState =
      ViewerState(
        currentLayer = ColdRowLayer(2, 1),
        parentLayer = null,
        breadcrumbs = listOf(ColdColumnLayer(0, null), ColdColumnLayer(1, 0), ColdTextLayer(2, 1)),
        loadedLayers =
          listOf(
            ColdColumnLayer(0, null),
            ColdColumnLayer(1, 0),
            ColdTextLayer(2, 1),
            ColdTextLayer(3, 1),
            ColdTextLayer(4, 1),
            ColdTextLayer(5, 1),
            ColdTextLayer(6, 1),
            ColdTextLayer(7, 1),
            ColdTextLayer(8, 1),
            ColdTextLayer(9, 1),
            ColdTextLayer(10, 1),
            ColdTextLayer(11, 1),
            ColdTextLayer(12, 1),
            ColdTextLayer(13, 1),
          ),
      ),
    globals =
      Globals(
        strings =
          listOf(
            GlobalValue.GlobalString(
              id = 0,
              label = "Item 1",
              value = ScriptableString.Fixed("fixed text"),
            ),
            GlobalValue.GlobalString(
              id = 1,
              label = "Item 2",
              value = ScriptableString.Script("some script"),
            ),
          )
      ),
  )
}
