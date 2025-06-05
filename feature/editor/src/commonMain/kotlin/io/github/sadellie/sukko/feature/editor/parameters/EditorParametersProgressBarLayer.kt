package io.github.sadellie.sukko.feature.editor.parameters

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
import io.github.sadellie.sukko.core.ui.lastShape
import io.github.sadellie.sukko.core.ui.middleShape
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
import org.jetbrains.compose.ui.tooling.preview.Preview

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
  )
  EditorParametersProgressBarType(
    onUpdateLayer = onUpdateLayer,
    layer = layer,
    compactListMode = compactListMode,
  )
  EditorParametersColor(
    onUpdateLayer = onUpdateLayer,
    layer = layer,
    compactListMode = compactListMode,
    globals = globals,
  )
  EditorParametersTrackColor(
    onUpdateLayer = onUpdateLayer,
    layer = layer,
    compactListMode = compactListMode,
    globals = globals,
  )
  EditorParametersGapSize(
    onUpdateLayer = onUpdateLayer,
    layer = layer,
    compactListMode = compactListMode,
    globals = globals,
  )
  EditorParametersAmplitude(
    onUpdateLayer = onUpdateLayer,
    layer = layer,
    compactListMode = compactListMode,
    globals = globals,
  )
  EditorParametersWaveLength(
    onUpdateLayer = onUpdateLayer,
    layer = layer,
    compactListMode = compactListMode,
    globals = globals,
  )
}

@Composable
internal fun EditorParametersProgress(
  onUpdateLayer: (ColdProgressBarLayer) -> Unit,
  layer: ColdProgressBarLayer,
  compactListMode: Boolean,
  globals: Globals,
) {
  val sheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
  ListItem2Compact(
    headlineContent = { Text(stringResource(Res.string.editor_parameters_progress)) },
    supportingContent = { Text(LocalScriptableDisplay.current.displayString(layer.progress)) },
    compactListMode = compactListMode,
    modifier = Modifier.clickable { sheetState.expand() },
    shape = ListItemDefaults.middleShape,
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
) {
  var showDialog by rememberSaveable { mutableStateOf(false) }
  ListItem2Compact(
    modifier = Modifier.clickable { showDialog = true }.fillMaxWidth(),
    headlineContent = { Text(stringResource(Res.string.editor_parameters_type)) },
    supportingContent = { Text(stringResource(layer.progressBarType.res)) },
    compactListMode = compactListMode,
    shape = ListItemDefaults.middleShape,
  )

  if (showDialog) {
    AlertDialogWithRadioItems(
      title = stringResource(Res.string.editor_parameters_type),
      onDismiss = { showDialog = false },
      items = remember { ProgressBarType.entries },
      key = { it },
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
) {
  val sheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
  ListItem2Compact(
    headlineContent = { Text(stringResource(Res.string.editor_parameters_color)) },
    supportingContent = { Text(LocalScriptableDisplay.current.displayString(layer.color)) },
    compactListMode = compactListMode,
    modifier = Modifier.clickable { sheetState.expand() },
    shape = ListItemDefaults.middleShape,
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
) {
  val sheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
  ListItem2Compact(
    headlineContent = { Text(stringResource(Res.string.editor_parameters_track_color)) },
    supportingContent = { Text(LocalScriptableDisplay.current.displayString(layer.trackColor)) },
    compactListMode = compactListMode,
    modifier = Modifier.clickable { sheetState.expand() },
    shape = ListItemDefaults.middleShape,
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
) {
  val sheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
  ListItem2Compact(
    headlineContent = { Text(stringResource(Res.string.editor_parameters_gap_size)) },
    supportingContent = { Text(LocalScriptableDisplay.current.displayString(layer.gapSize)) },
    compactListMode = compactListMode,
    modifier = Modifier.clickable { sheetState.expand() },
    shape = ListItemDefaults.middleShape,
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
) {
  val sheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
  ListItem2Compact(
    headlineContent = { Text(stringResource(Res.string.editor_parameters_amplitude)) },
    supportingContent = { Text(LocalScriptableDisplay.current.displayString(layer.amplitude)) },
    compactListMode = compactListMode,
    modifier = Modifier.clickable { sheetState.expand() },
    shape = ListItemDefaults.middleShape,
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
) {
  val sheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
  ListItem2Compact(
    headlineContent = { Text(stringResource(Res.string.editor_parameters_wave_length)) },
    supportingContent = { Text(LocalScriptableDisplay.current.displayString(layer.waveLength)) },
    compactListMode = compactListMode,
    modifier = Modifier.clickable { sheetState.expand() },
    shape = ListItemDefaults.lastShape,
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
