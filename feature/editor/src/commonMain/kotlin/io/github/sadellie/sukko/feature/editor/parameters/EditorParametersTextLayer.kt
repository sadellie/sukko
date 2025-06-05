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
import io.github.sadellie.sukko.core.designsystem.Preview2
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.basic.LocalScriptableDisplay
import io.github.sadellie.sukko.core.model.layer.ColdTextLayer
import io.github.sadellie.sukko.core.ui.ListItem2Compact
import io.github.sadellie.sukko.core.ui.expand
import io.github.sadellie.sukko.core.ui.lastShape
import io.github.sadellie.sukko.core.ui.middleShape
import io.github.sadellie.sukko.feature.editor.selector.BrushSourceSelectorSheet
import io.github.sadellie.sukko.feature.editor.selector.StringSelectorSheet
import io.github.sadellie.sukko.feature.editor.selector.TextStyleSelectorSheet
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.editor_parameters_color
import io.github.sadellie.sukko.resources.editor_parameters_text
import io.github.sadellie.sukko.resources.editor_parameters_text_style
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun EditorParametersTextLayer(
  onUpdateLayer: (ColdTextLayer) -> Unit,
  layer: ColdTextLayer,
  compactListMode: Boolean,
  globals: Globals,
) {
  EditorParametersText(
    onUpdateLayer = onUpdateLayer,
    layer = layer,
    compactListMode = compactListMode,
    globals = globals,
  )
  EditorParametersTextStyle(
    onUpdateLayer = onUpdateLayer,
    layer = layer,
    compactListMode = compactListMode,
    globals = globals,
  )
  EditorParametersTextColor(
    onUpdateLayer = onUpdateLayer,
    layer = layer,
    compactListMode = compactListMode,
    globals = globals,
  )
}

@Composable
private fun EditorParametersText(
  onUpdateLayer: (ColdTextLayer) -> Unit,
  layer: ColdTextLayer,
  compactListMode: Boolean,
  globals: Globals,
) {
  val sheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
  ListItem2Compact(
    headlineContent = { Text(stringResource(Res.string.editor_parameters_text)) },
    supportingContent = { Text(LocalScriptableDisplay.current.displayString(layer.text)) },
    compactListMode = compactListMode,
    modifier = Modifier.clickable { sheetState.expand() },
    shape = ListItemDefaults.middleShape,
  )

  StringSelectorSheet(
    state = sheetState,
    onValueSelected = { onUpdateLayer(layer.copy(text = it)) },
    value = layer.text,
    globals = globals.strings,
  )
}

@Composable
private fun EditorParametersTextStyle(
  onUpdateLayer: (ColdTextLayer) -> Unit,
  layer: ColdTextLayer,
  compactListMode: Boolean,
  globals: Globals,
) {
  val sheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
  ListItem2Compact(
    headlineContent = { Text(stringResource(Res.string.editor_parameters_text_style)) },
    supportingContent = { Text(stringResource(layer.textStyleSource.displayName)) },
    compactListMode = compactListMode,
    modifier = Modifier.clickable { sheetState.expand() },
    shape = ListItemDefaults.middleShape,
  )

  TextStyleSelectorSheet(
    state = sheetState,
    onValueSelected = { onUpdateLayer(layer.copy(textStyleSource = it)) },
    value = layer.textStyleSource,
    globals = globals,
  )
}

@Composable
private fun EditorParametersTextColor(
  onUpdateLayer: (ColdTextLayer) -> Unit,
  layer: ColdTextLayer,
  compactListMode: Boolean,
  globals: Globals,
) {
  val sheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
  ListItem2Compact(
    headlineContent = { Text(stringResource(Res.string.editor_parameters_color)) },
    supportingContent = { Text(layer.textColor.displayValue()) },
    compactListMode = compactListMode,
    modifier = Modifier.clickable { sheetState.expand() },
    shape = ListItemDefaults.lastShape,
  )

  BrushSourceSelectorSheet(
    state = sheetState,
    onValueSelected = { onUpdateLayer(layer.copy(textColor = it)) },
    value = layer.textColor,
    globals = globals,
  )
}

@Composable
@Preview
private fun PreviewEditorParametersTextLayer() = Preview2 {
  var layer by remember { mutableStateOf(ColdTextLayer(1)) }
  Column {
    EditorParametersTextLayer(
      layer = layer,
      onUpdateLayer = { layer = it },
      compactListMode = false,
      globals = Globals(),
    )
  }
}
