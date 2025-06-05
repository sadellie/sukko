package io.github.sadellie.sukko.feature.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import io.github.sadellie.sukko.core.common.collectAsStateWithLifecycleKMP
import io.github.sadellie.sukko.core.ui.LoadingScaffoldWithTopAppBar

@Composable
actual fun EditorScene(
  onNavigateUp: () -> Unit,
  onNavigateToSaveAsPreset: () -> Unit,
  onNavigateToPresetSelector: () -> Unit,
  viewModel: EditorViewModel,
) {
  LaunchedEffect(Unit) { viewModel.onUpdateWidgetSize() }

  when (val uiState = viewModel.uiState.collectAsStateWithLifecycleKMP().value) {
    null -> LoadingScaffoldWithTopAppBar(onNavigateUp = onNavigateUp, disableBack = false)
    else ->
      EditorScreen(
        uiState = uiState,
        onNavigateUp = onNavigateUp,
        onSave = { _, _ -> },
        onRename = viewModel::renameWidget,
        onNavigateToSaveAsPreset = onNavigateToSaveAsPreset,
        onNavigateToLayer = viewModel::onNavigateToLayer,
        onEvent = viewModel::onEvent,
        onUpdateWidgetDataSaverState = viewModel::updateWidgetSaverState,
        onNavigateNotificationListener = {},
        onNavigateToPresetSelector = onNavigateToPresetSelector,
      )
  }
}
