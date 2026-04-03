package io.github.sadellie.sukko.feature.editor.parameters

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ListItemShapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.composables.core.SheetDetent
import com.composables.core.rememberModalBottomSheetState
import io.github.sadellie.sukko.core.model.basic.AlignmentSource
import io.github.sadellie.sukko.core.model.layer.ColdBoxLayer
import io.github.sadellie.sukko.core.ui.ListItem2Compact
import io.github.sadellie.sukko.core.ui.ModalBottomSheetWithItems
import io.github.sadellie.sukko.core.ui.expand
import io.github.sadellie.sukko.core.ui.lastShapes
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.editor_parameters_alignment
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun EditorParametersBoxLayer(
  onUpdateLayer: (ColdBoxLayer) -> Unit,
  layer: ColdBoxLayer,
  compactListMode: Boolean,
) {
  EditorParametersAlignment(
    onUpdateLayer = onUpdateLayer,
    layer = layer,
    compactListMode = compactListMode,
    shapes = ListItemDefaults.lastShapes,
  )
}

@Composable
private fun EditorParametersAlignment(
  onUpdateLayer: (ColdBoxLayer) -> Unit,
  layer: ColdBoxLayer,
  compactListMode: Boolean,
  shapes: ListItemShapes,
) {
  val sheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
  ListItem2Compact(
    content = { Text(stringResource(Res.string.editor_parameters_alignment)) },
    supportingContent = { Text(stringResource(layer.alignmentSource.displayName)) },
    compactListMode = compactListMode,
    onClick = sheetState::expand,
    shapes = shapes,
  )

  ModalBottomSheetWithItems(
    state = sheetState,
    items = AlignmentSource.allBoth,
    headlineText = { stringResource(it.displayName) },
    onClick = { onUpdateLayer(layer.copy(alignmentSource = it)) },
  )
}

@Composable
@Preview
private fun PreviewEditorParametersBoxLayer() {
  var layer by remember { mutableStateOf(ColdBoxLayer(id = 0)) }
  Column {
    EditorParametersBoxLayer(onUpdateLayer = { layer = it }, layer = layer, compactListMode = false)
  }
}
