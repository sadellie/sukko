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
import io.github.sadellie.sukko.core.designsystem.Preview2
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.basic.LocalScriptableDisplay
import io.github.sadellie.sukko.core.model.basic.TextOverflowSource
import io.github.sadellie.sukko.core.model.layer.ColdTextLayer
import io.github.sadellie.sukko.core.ui.ListItem2Compact
import io.github.sadellie.sukko.core.ui.ModalBottomSheetWithItems
import io.github.sadellie.sukko.core.ui.expand
import io.github.sadellie.sukko.core.ui.lastShapes
import io.github.sadellie.sukko.core.ui.middleShapes
import io.github.sadellie.sukko.feature.editor.selector.brushsource.BrushSourceSelectorSheet
import io.github.sadellie.sukko.feature.editor.selector.DoubleSelectorSheet
import io.github.sadellie.sukko.feature.editor.selector.DoubleSelectorSheetNullable
import io.github.sadellie.sukko.feature.editor.selector.StringSelectorSheet
import io.github.sadellie.sukko.feature.editor.selector.TextStyleSelectorSheet
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.editor_parameters_color
import io.github.sadellie.sukko.resources.editor_parameters_text
import io.github.sadellie.sukko.resources.editor_parameters_text_max_lines
import io.github.sadellie.sukko.resources.editor_parameters_text_min_lines
import io.github.sadellie.sukko.resources.editor_parameters_text_overflow
import io.github.sadellie.sukko.resources.editor_parameters_text_style
import org.jetbrains.compose.resources.stringResource

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
    shapes = ListItemDefaults.middleShapes,
  )
  EditorParametersTextStyle(
    onUpdateLayer = onUpdateLayer,
    layer = layer,
    compactListMode = compactListMode,
    globals = globals,
    shapes = ListItemDefaults.middleShapes,
  )
  EditorParametersTextColor(
    onUpdateLayer = onUpdateLayer,
    layer = layer,
    compactListMode = compactListMode,
    globals = globals,
    shapes = ListItemDefaults.middleShapes,
  )
  EditorParametersMinLines(
    onUpdateLayer = onUpdateLayer,
    layer = layer,
    compactListMode = compactListMode,
    globals = globals,
    shapes = ListItemDefaults.middleShapes,
  )
  EditorParametersMaxLines(
    onUpdateLayer = onUpdateLayer,
    layer = layer,
    compactListMode = compactListMode,
    globals = globals,
    shapes = ListItemDefaults.middleShapes,
  )
  EditorParametersOverflow(
    onUpdateLayer = onUpdateLayer,
    layer = layer,
    compactListMode = compactListMode,
    shapes = ListItemDefaults.lastShapes,
  )
}

@Composable
private fun EditorParametersText(
  onUpdateLayer: (ColdTextLayer) -> Unit,
  layer: ColdTextLayer,
  compactListMode: Boolean,
  globals: Globals,
  shapes: ListItemShapes,
) {
  val sheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
  ListItem2Compact(
    content = { Text(stringResource(Res.string.editor_parameters_text)) },
    supportingContent = { Text(LocalScriptableDisplay.current.displayString(layer.text)) },
    compactListMode = compactListMode,
    shapes = shapes,
    onClick = sheetState::expand,
  )

  StringSelectorSheet(
    state = sheetState,
    onValueSelected = { onUpdateLayer(layer.copy(text = it)) },
    value = layer.text,
    globals = globals,
  )
}

@Composable
private fun EditorParametersTextStyle(
  onUpdateLayer: (ColdTextLayer) -> Unit,
  layer: ColdTextLayer,
  compactListMode: Boolean,
  globals: Globals,
  shapes: ListItemShapes,
) {
  val sheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
  ListItem2Compact(
    content = { Text(stringResource(Res.string.editor_parameters_text_style)) },
    supportingContent = { Text(stringResource(layer.textStyleSource.displayName)) },
    compactListMode = compactListMode,
    onClick = sheetState::expand,
    shapes = shapes,
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
  shapes: ListItemShapes,
) {
  val sheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
  ListItem2Compact(
    content = { Text(stringResource(Res.string.editor_parameters_color)) },
    supportingContent = { Text(layer.textColor.displayValue()) },
    compactListMode = compactListMode,
    shapes = shapes,
    onClick = sheetState::expand,
  )

  BrushSourceSelectorSheet(
    state = sheetState,
    onValueSelected = { onUpdateLayer(layer.copy(textColor = it)) },
    value = layer.textColor,
    globals = globals,
  )
}

@Composable
private fun EditorParametersOverflow(
  onUpdateLayer: (ColdTextLayer) -> Unit,
  layer: ColdTextLayer,
  compactListMode: Boolean,
  shapes: ListItemShapes,
) {
  val sheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
  ListItem2Compact(
    content = { Text(stringResource(Res.string.editor_parameters_text_overflow)) },
    supportingContent = { Text(stringResource(layer.textOverflowSource.displayName)) },
    compactListMode = compactListMode,
    onClick = sheetState::expand,
    shapes = shapes,
  )

  ModalBottomSheetWithItems(
    state = sheetState,
    items = remember { TextOverflowSource.values() },
    headlineText = { stringResource(it.displayName) },
    onClick = { onUpdateLayer(layer.copy(textOverflowSource = it)) },
  )
}

@Composable
private fun EditorParametersMinLines(
  onUpdateLayer: (ColdTextLayer) -> Unit,
  layer: ColdTextLayer,
  compactListMode: Boolean,
  globals: Globals,
  shapes: ListItemShapes,
) {
  val weightSheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
  ListItem2Compact(
    content = { Text(stringResource(Res.string.editor_parameters_text_min_lines)) },
    supportingContent = { Text(LocalScriptableDisplay.current.displayString(layer.minLines)) },
    compactListMode = compactListMode,
    onClick = weightSheetState::expand,
    shapes = shapes,
  )
  DoubleSelectorSheet(
    state = weightSheetState,
    onValueSelected = { onUpdateLayer(layer.copy(minLines = it)) },
    value = layer.minLines,
    range = ColdTextLayer.minLinesRange,
    allowFraction = false,
    globals = globals,
  )
}

@Composable
private fun EditorParametersMaxLines(
  onUpdateLayer: (ColdTextLayer) -> Unit,
  layer: ColdTextLayer,
  compactListMode: Boolean,
  globals: Globals,
  shapes: ListItemShapes,
) {
  val weightSheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
  ListItem2Compact(
    content = { Text(stringResource(Res.string.editor_parameters_text_max_lines)) },
    supportingContent = { Text(LocalScriptableDisplay.current.displayString(layer.maxLines)) },
    compactListMode = compactListMode,
    onClick = weightSheetState::expand,
    shapes = shapes,
  )
  DoubleSelectorSheetNullable(
    state = weightSheetState,
    onValueSelected = { onUpdateLayer(layer.copy(maxLines = it)) },
    value = layer.maxLines,
    range = ColdTextLayer.maxLinesRange,
    allowFraction = false,
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
