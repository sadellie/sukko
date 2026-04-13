package io.github.sadellie.sukko.feature.editor.parameters

import androidx.compose.animation.AnimatedContent
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
import io.github.sadellie.sukko.core.model.basic.ArrangementSource
import io.github.sadellie.sukko.core.model.basic.LocalScriptableDisplay
import io.github.sadellie.sukko.core.model.layer.ColdColumnLayer
import io.github.sadellie.sukko.core.ui.ExpandableListItem
import io.github.sadellie.sukko.core.ui.ListItem2Compact
import io.github.sadellie.sukko.core.ui.ModalBottomSheetWithItems
import io.github.sadellie.sukko.core.ui.expand
import io.github.sadellie.sukko.core.ui.lastShapes
import io.github.sadellie.sukko.core.ui.middleShapes
import io.github.sadellie.sukko.feature.editor.selector.FixedDoubleSelectorSheet
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.editor_parameters_alignment
import io.github.sadellie.sukko.resources.editor_parameters_arrangement
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun EditorParametersColumnLayer(
  onUpdateLayer: (ColdColumnLayer) -> Unit,
  layer: ColdColumnLayer,
  compactListMode: Boolean,
) {
  EditorParametersArrangement(
    onUpdateLayer = onUpdateLayer,
    layer = layer,
    compactListMode = compactListMode,
    shapes = ListItemDefaults.middleShapes,
  )
  EditorParametersAlignment(
    onUpdateLayer = onUpdateLayer,
    layer = layer,
    compactListMode = compactListMode,
    shapes = ListItemDefaults.lastShapes,
  )
}

@Composable
private fun EditorParametersArrangement(
  onUpdateLayer: (ColdColumnLayer) -> Unit,
  layer: ColdColumnLayer,
  compactListMode: Boolean,
  shapes: ListItemShapes,
) {
  val sheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
  ExpandableListItem(
    headlineText = stringResource(Res.string.editor_parameters_arrangement),
    supportingText = stringResource(layer.arrangementSource.displayName),
    compactListMode = compactListMode,
    shapes = shapes,
  ) {
    ListItem2Compact(
      content = { Text(stringResource(Res.string.editor_parameters_arrangement)) },
      supportingContent = { Text(stringResource(layer.arrangementSource.displayName)) },
      compactListMode = compactListMode,
      onClick = sheetState::expand,
      shapes = ListItemDefaults.middleShapes,
    )
    AnimatedContent(targetState = layer.arrangementSource) { arrangementSource ->
      Column {
        when (arrangementSource) {
          is ArrangementSource.SpacedBy ->
            EditorArrangementSourceSpacedBy(
              onUpdate = { onUpdateLayer(layer.copy(arrangementSource = it)) },
              arrangementSource = arrangementSource,
              compactListMode = compactListMode,
            )
          is ArrangementSource.SpacedByVertical ->
            EditorArrangementSourceSpacedByVertical(
              onUpdate = { onUpdateLayer(layer.copy(arrangementSource = it)) },
              arrangementSource = arrangementSource,
              compactListMode = compactListMode,
            )
          else -> Unit
        }
      }
    }
  }

  ModalBottomSheetWithItems(
    state = sheetState,
    items = ArrangementSource.allVertical,
    headlineText = { stringResource(it.displayName) },
    onClick = { onUpdateLayer(layer.copy(arrangementSource = it)) },
  )
}

@Composable
private fun EditorArrangementSourceSpacedBy(
  onUpdate: (ArrangementSource.SpacedBy) -> Unit,
  arrangementSource: ArrangementSource.SpacedBy,
  compactListMode: Boolean,
) {
  val sheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
  ListItem2Compact(
    content = { Text(stringResource(arrangementSource.displayName)) },
    supportingContent = {
      Text(LocalScriptableDisplay.current.displayString(arrangementSource.space))
    },
    compactListMode = compactListMode,
    onClick = sheetState::expand,
    shapes = ListItemDefaults.middleShapes,
  )
  FixedDoubleSelectorSheet(
    state = sheetState,
    onValueSelected = {
      val updatedArrangement = arrangementSource.copy(space = it)
      onUpdate(updatedArrangement)
    },
    value = arrangementSource.space,
    allowFraction = true,
  )
}

@Composable
private fun EditorArrangementSourceSpacedByVertical(
  onUpdate: (ArrangementSource.SpacedByVertical) -> Unit,
  arrangementSource: ArrangementSource.SpacedByVertical,
  compactListMode: Boolean,
) {
  val sheetSpaceState = rememberModalBottomSheetState(SheetDetent.Hidden)
  val sheetAlignmentState = rememberModalBottomSheetState(SheetDetent.Hidden)
  ListItem2Compact(
    content = { Text(stringResource(arrangementSource.displayName)) },
    supportingContent = {
      Text(LocalScriptableDisplay.current.displayString(arrangementSource.space))
    },
    compactListMode = compactListMode,
    onClick = sheetSpaceState::expand,
    shapes = ListItemDefaults.middleShapes,
  )
  ListItem2Compact(
    content = { Text(stringResource(Res.string.editor_parameters_alignment)) },
    supportingContent = { Text(stringResource(arrangementSource.alignmentSource.displayName)) },
    compactListMode = compactListMode,
    onClick = sheetAlignmentState::expand,
    shapes = ListItemDefaults.middleShapes,
  )

  FixedDoubleSelectorSheet(
    state = sheetSpaceState,
    onValueSelected = { onUpdate(arrangementSource.copy(space = it)) },
    value = arrangementSource.space,
    allowFraction = true,
  )

  ModalBottomSheetWithItems(
    state = sheetAlignmentState,
    items = AlignmentSource.allVertical,
    headlineText = { stringResource(it.displayName) },
    onClick = { onUpdate(arrangementSource.copy(alignmentSource = it)) },
  )
}

@Composable
private fun EditorParametersAlignment(
  onUpdateLayer: (ColdColumnLayer) -> Unit,
  layer: ColdColumnLayer,
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
    items = AlignmentSource.allHorizontal,
    headlineText = { stringResource(it.displayName) },
    onClick = { onUpdateLayer(layer.copy(alignmentSource = it)) },
  )
}

@Composable
@Preview
private fun PreviewEditorParametersColumnLayer() {
  var layer by remember { mutableStateOf(ColdColumnLayer(id = 0)) }
  Column {
    EditorParametersColumnLayer(
      onUpdateLayer = { layer = it },
      layer = layer,
      compactListMode = false,
    )
  }
}
