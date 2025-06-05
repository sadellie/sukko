package io.github.sadellie.sukko.feature.editor.parameters

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.composables.core.SheetDetent
import com.composables.core.rememberModalBottomSheetState
import io.github.sadellie.sukko.core.model.basic.AlignmentSource
import io.github.sadellie.sukko.core.model.layer.ColdBoxLayer
import io.github.sadellie.sukko.core.ui.ListItem2Compact
import io.github.sadellie.sukko.core.ui.ModalBottomSheetWithItems
import io.github.sadellie.sukko.core.ui.expand
import io.github.sadellie.sukko.core.ui.lastShape
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.editor_parameters_alignment
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

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
  )
}

@Composable
private fun EditorParametersAlignment(
  onUpdateLayer: (ColdBoxLayer) -> Unit,
  layer: ColdBoxLayer,
  compactListMode: Boolean,
) {
  val sheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
  ListItem2Compact(
    headlineContent = { Text(stringResource(Res.string.editor_parameters_alignment)) },
    supportingContent = { Text(stringResource(layer.alignmentSource.displayName)) },
    compactListMode = compactListMode,
    modifier = Modifier.clickable { sheetState.expand() },
    shape = ListItemDefaults.lastShape,
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
