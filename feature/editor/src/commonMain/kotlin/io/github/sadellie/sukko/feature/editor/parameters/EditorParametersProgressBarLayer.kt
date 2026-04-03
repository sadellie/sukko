package io.github.sadellie.sukko.feature.editor.parameters

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ListItemShapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.composables.core.SheetDetent
import com.composables.core.rememberModalBottomSheetState
import io.github.sadellie.sukko.core.designsystem.Preview2
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.basic.LocalScriptableDisplay
import io.github.sadellie.sukko.core.model.layer.ColdProgressBarLayer
import io.github.sadellie.sukko.core.model.layer.ProgressBarType
import io.github.sadellie.sukko.core.ui.AlertDialogWithRadioItems
import io.github.sadellie.sukko.core.ui.ListItem2Compact
import io.github.sadellie.sukko.core.ui.expand
import io.github.sadellie.sukko.core.ui.lastShapes
import io.github.sadellie.sukko.core.ui.middleShapes
import io.github.sadellie.sukko.feature.editor.selector.ColorSelectorSheet
import io.github.sadellie.sukko.feature.editor.selector.DoubleSelectorSheet
import io.github.sadellie.sukko.feature.editor.selector.DpSelectorSheet
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.editor_parameters_amplitude
import io.github.sadellie.sukko.resources.editor_parameters_color
import io.github.sadellie.sukko.resources.editor_parameters_gap_size
import io.github.sadellie.sukko.resources.editor_parameters_progress
import io.github.sadellie.sukko.resources.editor_parameters_track_color
import io.github.sadellie.sukko.resources.editor_parameters_type
import io.github.sadellie.sukko.resources.editor_parameters_wave_length
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun EditorParametersProgressBarLayer(
  onUpdateLayer: (ColdProgressBarLayer) -> Unit,
  layer: ColdProgressBarLayer,
  compactListMode: Boolean,
  globals: Globals,
) {
  EditorParametersProgress(
    onUpdateLayer = onUpdateLayer,
    layer = layer,
    compactListMode = compactListMode,
    globals = globals,
    shapes = ListItemDefaults.middleShapes,
  )
  EditorParametersProgressBarType(
    onUpdateLayer = onUpdateLayer,
    layer = layer,
    compactListMode = compactListMode,
    shapes = ListItemDefaults.middleShapes,
  )
  EditorParametersColor(
    onUpdateLayer = onUpdateLayer,
    layer = layer,
    compactListMode = compactListMode,
    globals = globals,
    shapes = ListItemDefaults.middleShapes,
  )
  EditorParametersTrackColor(
    onUpdateLayer = onUpdateLayer,
    layer = layer,
    compactListMode = compactListMode,
    globals = globals,
    shapes = ListItemDefaults.middleShapes,
  )
  EditorParametersGapSize(
    onUpdateLayer = onUpdateLayer,
    layer = layer,
    compactListMode = compactListMode,
    globals = globals,
    shapes = ListItemDefaults.middleShapes,
  )
  EditorParametersAmplitude(
    onUpdateLayer = onUpdateLayer,
    layer = layer,
    compactListMode = compactListMode,
    globals = globals,
    shapes = ListItemDefaults.middleShapes,
  )
  EditorParametersWaveLength(
    onUpdateLayer = onUpdateLayer,
    layer = layer,
    compactListMode = compactListMode,
    globals = globals,
    shapes = ListItemDefaults.lastShapes,
  )
}

@Composable
internal fun EditorParametersProgress(
  onUpdateLayer: (ColdProgressBarLayer) -> Unit,
  layer: ColdProgressBarLayer,
  compactListMode: Boolean,
  globals: Globals,
  shapes: ListItemShapes,
) {
  val sheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
  ListItem2Compact(
    content = { Text(stringResource(Res.string.editor_parameters_progress)) },
    supportingContent = { Text(LocalScriptableDisplay.current.displayString(layer.progress)) },
    compactListMode = compactListMode,
    onClick = sheetState::expand,
    shapes = shapes,
  )

  DoubleSelectorSheet(
    state = sheetState,
    onValueSelected = { onUpdateLayer(layer.copy(progress = it)) },
    value = layer.progress,
    globals = globals.doubles,
    allowFraction = true,
    range = ColdProgressBarLayer.progressRange,
  )
}

@Composable
private fun EditorParametersProgressBarType(
  onUpdateLayer: (ColdProgressBarLayer) -> Unit,
  layer: ColdProgressBarLayer,
  compactListMode: Boolean,
  shapes: ListItemShapes,
) {
  var showDialog by rememberSaveable { mutableStateOf(false) }
  ListItem2Compact(
    onClick = { showDialog = true },
    content = { Text(stringResource(Res.string.editor_parameters_type)) },
    supportingContent = { Text(stringResource(layer.progressBarType.res)) },
    compactListMode = compactListMode,
    shapes = shapes,
  )

  if (showDialog) {
    AlertDialogWithRadioItems(
      title = stringResource(Res.string.editor_parameters_type),
      onDismiss = { showDialog = false },
      items = remember { ProgressBarType.entries },
      key = { _, item -> item },
      headlineText = { stringResource(it.res) },
      isSelected = { it == layer.progressBarType },
      onClick = { onUpdateLayer(layer.copy(progressBarType = it)) },
    )
  }
}

@Composable
internal fun EditorParametersColor(
  onUpdateLayer: (ColdProgressBarLayer) -> Unit,
  layer: ColdProgressBarLayer,
  compactListMode: Boolean,
  globals: Globals,
  shapes: ListItemShapes,
) {
  val sheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
  ListItem2Compact(
    content = { Text(stringResource(Res.string.editor_parameters_color)) },
    supportingContent = { Text(LocalScriptableDisplay.current.displayString(layer.color)) },
    compactListMode = compactListMode,
    onClick = sheetState::expand,
    shapes = shapes,
  )

  ColorSelectorSheet(
    state = sheetState,
    onValueSelected = { onUpdateLayer(layer.copy(color = it)) },
    value = layer.color,
    globals = globals.colors,
  )
}

@Composable
internal fun EditorParametersTrackColor(
  onUpdateLayer: (ColdProgressBarLayer) -> Unit,
  layer: ColdProgressBarLayer,
  compactListMode: Boolean,
  globals: Globals,
  shapes: ListItemShapes,
) {
  val sheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
  ListItem2Compact(
    content = { Text(stringResource(Res.string.editor_parameters_track_color)) },
    supportingContent = { Text(LocalScriptableDisplay.current.displayString(layer.trackColor)) },
    compactListMode = compactListMode,
    onClick = sheetState::expand,
    shapes = shapes,
  )

  ColorSelectorSheet(
    state = sheetState,
    onValueSelected = { onUpdateLayer(layer.copy(trackColor = it)) },
    value = layer.trackColor,
    globals = globals.colors,
  )
}

@Composable
internal fun EditorParametersGapSize(
  onUpdateLayer: (ColdProgressBarLayer) -> Unit,
  layer: ColdProgressBarLayer,
  compactListMode: Boolean,
  globals: Globals,
  shapes: ListItemShapes,
) {
  val sheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
  ListItem2Compact(
    content = { Text(stringResource(Res.string.editor_parameters_gap_size)) },
    supportingContent = { Text(LocalScriptableDisplay.current.displayString(layer.gapSize)) },
    compactListMode = compactListMode,
    onClick = sheetState::expand,
    shapes = shapes,
  )

  DpSelectorSheet(
    state = sheetState,
    onValueSelected = { onUpdateLayer(layer.copy(gapSize = it)) },
    value = layer.gapSize,
    globals = globals.dps,
  )
}

@Composable
internal fun EditorParametersAmplitude(
  onUpdateLayer: (ColdProgressBarLayer) -> Unit,
  layer: ColdProgressBarLayer,
  compactListMode: Boolean,
  globals: Globals,
  shapes: ListItemShapes,
) {
  val sheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
  ListItem2Compact(
    content = { Text(stringResource(Res.string.editor_parameters_amplitude)) },
    supportingContent = { Text(LocalScriptableDisplay.current.displayString(layer.amplitude)) },
    compactListMode = compactListMode,
    onClick = { sheetState.expand() },
    shapes = shapes,
  )

  DoubleSelectorSheet(
    state = sheetState,
    onValueSelected = { onUpdateLayer(layer.copy(amplitude = it)) },
    value = layer.amplitude,
    globals = globals.doubles,
    allowFraction = true,
    range = ColdProgressBarLayer.amplitudeRange,
  )
}

@Composable
internal fun EditorParametersWaveLength(
  onUpdateLayer: (ColdProgressBarLayer) -> Unit,
  layer: ColdProgressBarLayer,
  compactListMode: Boolean,
  globals: Globals,
  shapes: ListItemShapes,
) {
  val sheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
  ListItem2Compact(
    content = { Text(stringResource(Res.string.editor_parameters_wave_length)) },
    supportingContent = { Text(LocalScriptableDisplay.current.displayString(layer.waveLength)) },
    compactListMode = compactListMode,
    onClick = sheetState::expand,
    shapes = shapes,
  )

  DpSelectorSheet(
    state = sheetState,
    onValueSelected = { onUpdateLayer(layer.copy(waveLength = it)) },
    value = layer.waveLength,
    globals = globals.dps,
  )
}

@Composable
@Preview
private fun PreviewEditorParametersProgressBarLayer() = Preview2 {
  Column {
    EditorParametersProgressBarLayer(
      onUpdateLayer = {},
      layer = ColdProgressBarLayer(id = 0),
      compactListMode = false,
      globals = Globals(),
    )
  }
}
