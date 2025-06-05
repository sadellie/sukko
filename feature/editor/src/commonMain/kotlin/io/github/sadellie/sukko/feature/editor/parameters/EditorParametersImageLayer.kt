package io.github.sadellie.sukko.feature.editor.parameters

import androidx.compose.foundation.clickable
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.composables.core.SheetDetent
import com.composables.core.rememberModalBottomSheetState
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.basic.LocalScriptableDisplay
import io.github.sadellie.sukko.core.model.layer.ColdImageLayer
import io.github.sadellie.sukko.core.model.layer.ContentScaleSource
import io.github.sadellie.sukko.core.ui.ListItem2Compact
import io.github.sadellie.sukko.core.ui.ModalBottomSheetWithItems
import io.github.sadellie.sukko.core.ui.expand
import io.github.sadellie.sukko.core.ui.hide
import io.github.sadellie.sukko.core.ui.lastShape
import io.github.sadellie.sukko.core.ui.middleShape
import io.github.sadellie.sukko.feature.editor.selector.ColorSelectorSheet
import io.github.sadellie.sukko.feature.editor.selector.DoubleSelectorSheet
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.editor_parameters_content_scale
import io.github.sadellie.sukko.resources.editor_parameters_scale_factor
import io.github.sadellie.sukko.resources.editor_parameters_tint
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun EditorParametersImageLayer(
  onUpdateLayer: (ColdImageLayer) -> Unit,
  layer: ColdImageLayer,
  compactListMode: Boolean,
  globals: Globals,
) {
  EditorParametersContentScale(
    onUpdateLayer = onUpdateLayer,
    layer = layer,
    compactListMode = compactListMode,
    globals = globals,
  )
  EditorParametersImageUri(
    onUpdateLayer = onUpdateLayer,
    layer = layer,
    compactListMode = compactListMode,
    globals = globals,
  )
  EditorParametersTint(
    onUpdateLayer = onUpdateLayer,
    layer = layer,
    compactListMode = compactListMode,
    globals = globals,
  )
}

@Composable
internal expect fun EditorParametersImageUri(
  onUpdateLayer: (ColdImageLayer) -> Unit,
  layer: ColdImageLayer,
  compactListMode: Boolean,
  globals: Globals,
)

@Composable
private fun EditorParametersContentScale(
  onUpdateLayer: (ColdImageLayer) -> Unit,
  layer: ColdImageLayer,
  compactListMode: Boolean,
  globals: Globals,
) {
  val contentScaleSheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
  ListItem2Compact(
    headlineContent = { Text(stringResource(Res.string.editor_parameters_content_scale)) },
    supportingContent = { Text(stringResource(layer.contentScale.displayName)) },
    compactListMode = compactListMode,
    modifier = Modifier.clickable { contentScaleSheetState.expand() },
    shape = ListItemDefaults.middleShape,
  )

  ModalBottomSheetWithItems(
    state = contentScaleSheetState,
    items = remember { ContentScaleSource.values() },
    onDismiss = contentScaleSheetState::hide,
    headlineText = { stringResource(it.displayName) },
    onClick = { onUpdateLayer(layer.copy(contentScale = it)) },
  )

  val scaleSource = layer.contentScale
  if (scaleSource is ContentScaleSource.FixedScale) {
    val contentScaleFactorState = rememberModalBottomSheetState(SheetDetent.Hidden)
    ListItem2Compact(
      headlineContent = { Text(stringResource(Res.string.editor_parameters_scale_factor)) },
      supportingContent = { Text(LocalScriptableDisplay.current.displayString(scaleSource.scale)) },
      compactListMode = compactListMode,
      modifier = Modifier.clickable { contentScaleFactorState.expand() },
      shape = ListItemDefaults.middleShape,
    )
    DoubleSelectorSheet(
      state = contentScaleFactorState,
      onValueSelected = { onUpdateLayer(layer.copy(contentScale = scaleSource.copy(scale = it))) },
      value = scaleSource.scale,
      range = ContentScaleSource.FixedScale.scaleRange,
      allowFraction = true,
      globals = globals.doubles,
    )
  }
}

@Composable
private fun EditorParametersTint(
  onUpdateLayer: (ColdImageLayer) -> Unit,
  layer: ColdImageLayer,
  compactListMode: Boolean,
  globals: Globals,
) {
  val sheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
  ListItem2Compact(
    headlineContent = { Text(stringResource(Res.string.editor_parameters_tint)) },
    supportingContent = {
      Text(layer.tint.let { LocalScriptableDisplay.current.displayString(it) })
    },
    compactListMode = compactListMode,
    modifier = Modifier.clickable { sheetState.expand() },
    shape = ListItemDefaults.lastShape,
  )

  ColorSelectorSheet(
    state = sheetState,
    onValueSelected = { onUpdateLayer(layer.copy(tint = it)) },
    value = layer.tint,
    globals = globals.colors,
  )
}
