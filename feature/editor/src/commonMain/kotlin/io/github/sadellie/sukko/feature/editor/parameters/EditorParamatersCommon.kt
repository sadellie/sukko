package io.github.sadellie.sukko.feature.editor.parameters

import androidx.compose.foundation.clickable
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
import io.github.sadellie.sukko.core.designsystem.Preview2
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.basic.LocalScriptableDisplay
import io.github.sadellie.sukko.core.model.layer.ColdTextLayer
import io.github.sadellie.sukko.core.model.layer.Layer
import io.github.sadellie.sukko.core.ui.ListItem2Compact
import io.github.sadellie.sukko.core.ui.expand
import io.github.sadellie.sukko.core.ui.firstShape
import io.github.sadellie.sukko.feature.editor.selector.BooleanSelectorSheet
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.common_enabled
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun EditorParametersCommon(
  onUpdateLayer: (Layer.Cold) -> Unit,
  layer: Layer.Cold,
  compactListMode: Boolean,
  globals: Globals,
) {
  val sheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
  ListItem2Compact(
    headlineContent = { Text(stringResource(Res.string.common_enabled)) },
    supportingContent = { Text(LocalScriptableDisplay.current.displayString(layer.isEnabled)) },
    compactListMode = compactListMode,
    modifier = Modifier.clickable { sheetState.expand() },
    shape = ListItemDefaults.firstShape,
  )
  BooleanSelectorSheet(
    state = sheetState,
    onValueSelected = { onUpdateLayer(layer.updateIsEnabled(it)) },
    value = layer.isEnabled,
    globals = globals.booleans,
  )
}

@Composable
@Preview
private fun PreviewEditorParametersCommon() = Preview2 {
  var layer by remember { mutableStateOf<Layer.Cold>(ColdTextLayer(1)) }
  EditorParametersCommon(
    onUpdateLayer = { layer = it },
    layer = layer,
    compactListMode = false,
    globals = Globals(),
  )
}
