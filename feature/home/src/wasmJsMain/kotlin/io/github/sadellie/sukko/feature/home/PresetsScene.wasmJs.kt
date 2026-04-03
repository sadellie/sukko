package io.github.sadellie.sukko.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@androidx.compose.runtime.Composable
internal actual fun PresetsScene(
  onNavigateToWidgets: () -> Unit,
  onNavigateToImportPreset: (selectedFile: io.github.vinceglb.filekit.PlatformFile) -> Unit,
  onNavigateToSettings: () -> Unit,
  onAddWidget: () -> Unit,
  toolBarNestedScrollConnection: androidx.compose.ui.input.nestedscroll.NestedScrollConnection,
) {
  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    Text("Presets are not available in web")
  }
}
