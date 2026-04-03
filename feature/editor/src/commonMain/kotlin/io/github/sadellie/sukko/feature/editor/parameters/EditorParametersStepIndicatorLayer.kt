package io.github.sadellie.sukko.feature.editor.parameters

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ListItemShapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.composables.core.SheetDetent
import com.composables.core.rememberModalBottomSheetState
import io.github.sadellie.sukko.core.designsystem.Preview2
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.basic.LocalScriptableDisplay
import io.github.sadellie.sukko.core.model.layer.ColdStepIndicatorLayer
import io.github.sadellie.sukko.core.ui.ListItem2Compact
import io.github.sadellie.sukko.core.ui.expand
import io.github.sadellie.sukko.core.ui.lastShapes
import io.github.sadellie.sukko.core.ui.middleShapes
import io.github.sadellie.sukko.feature.editor.selector.BooleanSelectorSheet
import io.github.sadellie.sukko.feature.editor.selector.ColorSelectorSheet
import io.github.sadellie.sukko.feature.editor.selector.DoubleSelectorSheet
import io.github.sadellie.sukko.feature.editor.selector.DpSelectorSheet
import io.github.sadellie.sukko.feature.editor.selector.ShapeSelectorSheet
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.common_fill
import io.github.sadellie.sukko.resources.common_shape
import io.github.sadellie.sukko.resources.editor_parameters_active_color
import io.github.sadellie.sukko.resources.editor_parameters_current_step
import io.github.sadellie.sukko.resources.editor_parameters_inactive_color
import io.github.sadellie.sukko.resources.editor_parameters_indicator_size
import io.github.sadellie.sukko.resources.editor_parameters_total_steps
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun EditorParametersStepIndicatorLayer(
  onUpdateLayer: (ColdStepIndicatorLayer) -> Unit,
  layer: ColdStepIndicatorLayer,
  compactListMode: Boolean,
  globals: Globals,
) {
  EditorParametersFill(
    onUpdateLayer = onUpdateLayer,
    layer = layer,
    compactListMode = compactListMode,
    globals = globals,
    shapes = ListItemDefaults.middleShapes,
  )
  EditorParametersTotalSteps(
    onUpdateLayer = onUpdateLayer,
    layer = layer,
    compactListMode = compactListMode,
    globals = globals,
    shapes = ListItemDefaults.middleShapes,
  )
  EditorParametersCurrentStep(
    onUpdateLayer = onUpdateLayer,
    layer = layer,
    compactListMode = compactListMode,
    globals = globals,
    shapes = ListItemDefaults.middleShapes,
  )
  EditorParametersIndicatorSize(
    onUpdateLayer = onUpdateLayer,
    layer = layer,
    compactListMode = compactListMode,
    globals = globals,
    shapes = ListItemDefaults.middleShapes,
  )
  EditorParametersActiveColor(
    onUpdateLayer = onUpdateLayer,
    layer = layer,
    compactListMode = compactListMode,
    globals = globals,
    shapes = ListItemDefaults.middleShapes,
  )
  EditorParametersInactiveColor(
    onUpdateLayer = onUpdateLayer,
    layer = layer,
    compactListMode = compactListMode,
    globals = globals,
    shapes = ListItemDefaults.middleShapes,
  )
  EditorParametersShape(
    onUpdateLayer = onUpdateLayer,
    layer = layer,
    compactListMode = compactListMode,
    shapes = ListItemDefaults.lastShapes,
  )
}

@Composable
private fun EditorParametersFill(
  onUpdateLayer: (ColdStepIndicatorLayer) -> Unit,
  layer: ColdStepIndicatorLayer,
  compactListMode: Boolean,
  globals: Globals,
  shapes: ListItemShapes,
) {
  val sheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
  ListItem2Compact(
    content = { Text(stringResource(Res.string.common_fill)) },
    supportingContent = { Text(LocalScriptableDisplay.current.displayString(layer.fill)) },
    compactListMode = compactListMode,
    onClick = sheetState::expand,
    shapes = shapes,
  )

  BooleanSelectorSheet(
    state = sheetState,
    onValueSelected = { onUpdateLayer(layer.copy(fill = it)) },
    value = layer.fill,
    globals = globals.booleans,
  )
}

@Composable
private fun EditorParametersTotalSteps(
  onUpdateLayer: (ColdStepIndicatorLayer) -> Unit,
  layer: ColdStepIndicatorLayer,
  compactListMode: Boolean,
  globals: Globals,
  shapes: ListItemShapes,
) {
  val sheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
  ListItem2Compact(
    content = { Text(stringResource(Res.string.editor_parameters_total_steps)) },
    supportingContent = { Text(LocalScriptableDisplay.current.displayString(layer.totalSteps)) },
    compactListMode = compactListMode,
    onClick = sheetState::expand,
    shapes = shapes,
  )

  DoubleSelectorSheet(
    state = sheetState,
    onValueSelected = { onUpdateLayer(layer.copy(totalSteps = it)) },
    value = layer.totalSteps,
    globals = globals.doubles,
    allowFraction = false,
  )
}

@Composable
private fun EditorParametersCurrentStep(
  onUpdateLayer: (ColdStepIndicatorLayer) -> Unit,
  layer: ColdStepIndicatorLayer,
  compactListMode: Boolean,
  globals: Globals,
  shapes: ListItemShapes,
) {
  val sheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
  ListItem2Compact(
    content = { Text(stringResource(Res.string.editor_parameters_current_step)) },
    supportingContent = { Text(LocalScriptableDisplay.current.displayString(layer.currentStep)) },
    compactListMode = compactListMode,
    onClick = sheetState::expand,
    shapes = shapes,
  )

  DoubleSelectorSheet(
    state = sheetState,
    onValueSelected = { onUpdateLayer(layer.copy(currentStep = it)) },
    value = layer.currentStep,
    globals = globals.doubles,
    allowFraction = false,
  )
}

@Composable
private fun EditorParametersIndicatorSize(
  onUpdateLayer: (ColdStepIndicatorLayer) -> Unit,
  layer: ColdStepIndicatorLayer,
  compactListMode: Boolean,
  globals: Globals,
  shapes: ListItemShapes,
) {
  val sheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
  ListItem2Compact(
    content = { Text(stringResource(Res.string.editor_parameters_indicator_size)) },
    supportingContent = { Text(LocalScriptableDisplay.current.displayString(layer.indicatorSize)) },
    compactListMode = compactListMode,
    onClick = sheetState::expand,
    shapes = shapes,
  )

  DpSelectorSheet(
    state = sheetState,
    onValueSelected = { onUpdateLayer(layer.copy(indicatorSize = it)) },
    value = layer.indicatorSize,
    globals = globals.dps,
  )
}

@Composable
private fun EditorParametersActiveColor(
  onUpdateLayer: (ColdStepIndicatorLayer) -> Unit,
  layer: ColdStepIndicatorLayer,
  compactListMode: Boolean,
  globals: Globals,
  shapes: ListItemShapes,
) {
  val sheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
  ListItem2Compact(
    content = { Text(stringResource(Res.string.editor_parameters_active_color)) },
    supportingContent = { Text(LocalScriptableDisplay.current.displayString(layer.activeColor)) },
    compactListMode = compactListMode,
    onClick = sheetState::expand,
    shapes = shapes,
  )

  ColorSelectorSheet(
    state = sheetState,
    onValueSelected = { onUpdateLayer(layer.copy(activeColor = it)) },
    value = layer.activeColor,
    globals = globals.colors,
  )
}

@Composable
private fun EditorParametersInactiveColor(
  onUpdateLayer: (ColdStepIndicatorLayer) -> Unit,
  layer: ColdStepIndicatorLayer,
  compactListMode: Boolean,
  globals: Globals,
  shapes: ListItemShapes,
) {
  val sheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
  ListItem2Compact(
    content = { Text(stringResource(Res.string.editor_parameters_inactive_color)) },
    supportingContent = { Text(LocalScriptableDisplay.current.displayString(layer.inactiveColor)) },
    compactListMode = compactListMode,
    onClick = sheetState::expand,
    shapes = shapes,
  )

  ColorSelectorSheet(
    state = sheetState,
    onValueSelected = { onUpdateLayer(layer.copy(inactiveColor = it)) },
    value = layer.inactiveColor,
    globals = globals.colors,
  )
}

@Composable
private fun EditorParametersShape(
  onUpdateLayer: (ColdStepIndicatorLayer) -> Unit,
  layer: ColdStepIndicatorLayer,
  compactListMode: Boolean,
  shapes: ListItemShapes,
) {
  val sheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
  ListItem2Compact(
    content = { Text(stringResource(Res.string.common_shape)) },
    supportingContent = { Text(stringResource(layer.shape.displayName)) },
    compactListMode = compactListMode,
    onClick = sheetState::expand,
    shapes = shapes,
  )

  ShapeSelectorSheet(
    state = sheetState,
    onValueSelected = { onUpdateLayer(layer.copy(shape = it)) },
    value = layer.shape,
  )
}

@Preview
@Composable
private fun PreviewEditorParametersStepIndicatorLayer() = Preview2 {
  Column {
    EditorParametersStepIndicatorLayer(
      onUpdateLayer = {},
      layer = ColdStepIndicatorLayer(id = 0),
      compactListMode = false,
      globals = Globals(),
    )
  }
}
