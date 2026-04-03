package io.github.sadellie.sukko.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarDefaults.ScreenOffset
import androidx.compose.material3.FloatingToolbarExitDirection
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import google.material.design.symbols.Home
import google.material.design.symbols.LibraryAdd
import google.material.design.symbols.Symbols
import io.github.sadellie.sukko.core.ui.Tab
import io.github.sadellie.sukko.core.ui.TabsSwitcher
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.home_presets_title
import io.github.sadellie.sukko.resources.home_widgets_title
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.launch

@Composable
internal fun HomeScene(
  navigateToEditor: (widgetId: Int) -> Unit,
  navigateToSettings: () -> Unit,
  navigateToImportPreset: (platformFile: PlatformFile) -> Unit,
  onAddWidget: () -> Unit,
) {
  Scaffold(containerColor = MaterialTheme.colorScheme.surfaceContainer) { paddingValues ->
    Box {
      val tabs = remember { tabs() }
      val pagerState = rememberPagerState { tabs.size }
      val scrollBehavior =
        FloatingToolbarDefaults.exitAlwaysScrollBehavior(
          exitDirection = FloatingToolbarExitDirection.Bottom
        )
      val coroutineScope = rememberCoroutineScope()

      fun switchToTab(tab: Tab) =
        coroutineScope.launch { pagerState.animateScrollToPage(tabs.indexOf(tab)) }

      HorizontalPager(state = pagerState) { currentPage ->
        when (currentPage) {
          0 ->
            WidgetsScene(
              onWidgetClick = navigateToEditor,
              onNavigateToSettings = navigateToSettings,
              toolBarNestedScrollConnection = scrollBehavior,
              onAddWidget = onAddWidget,
            )
          else ->
            PresetsScene(
              onNavigateToWidgets = { switchToTab(WidgetsTab) },
              onNavigateToImportPreset = navigateToImportPreset,
              onNavigateToSettings = navigateToSettings,
              toolBarNestedScrollConnection = scrollBehavior,
              onAddWidget = onAddWidget,
            )
        }
      }
      TabsSwitcher(
        modifier =
          Modifier.padding(paddingValues).align(Alignment.BottomCenter).offset(y = -ScreenOffset),
        tabs = tabs,
        onClick = ::switchToTab,
        selectedTabIndex = pagerState.currentPage,
        scrollBehavior = scrollBehavior,
      )
    }
  }
}

private data object WidgetsTab : Tab {
  override val icon = Symbols.Home
  override val label = Res.string.home_widgets_title
}

private data object Presets : Tab {
  override val icon = Symbols.LibraryAdd
  override val label = Res.string.home_presets_title
}

private fun tabs(): List<Tab> = listOf(WidgetsTab, Presets)
