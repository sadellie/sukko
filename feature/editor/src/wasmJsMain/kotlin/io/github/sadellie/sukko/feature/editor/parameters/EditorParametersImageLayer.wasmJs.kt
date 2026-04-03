package io.github.sadellie.sukko.feature.editor.parameters

import androidx.compose.material3.ListItemShapes
import androidx.compose.runtime.Composable
import io.github.sadellie.sukko.core.common.notReady
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.layer.ColdImageLayer

@Composable
internal actual fun EditorParametersImageUri(
  onUpdateLayer: (ColdImageLayer) -> Unit,
  layer: ColdImageLayer,
  compactListMode: Boolean,
  globals: Globals,
  shapes: ListItemShapes,
) {
  notReady
}
