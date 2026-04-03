package io.github.sadellie.sukko.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import io.github.vinceglb.filekit.PlatformFile

@Composable
internal expect fun PresetsScene(
  onNavigateToWidgets: () -> Unit,
  onNavigateToImportPreset: (selectedFile: PlatformFile) -> Unit,
  onNavigateToSettings: () -> Unit,
  onAddWidget: () -> Unit,
  toolBarNestedScrollConnection: NestedScrollConnection,
)
