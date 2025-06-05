package io.github.sadellie.sukko.feature.editor.parameters

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.ui.graphics.RectangleShape
import com.composables.core.SheetDetent
import com.composables.core.rememberModalBottomSheetState
import io.github.sadellie.sukko.core.model.basic.AlignmentSource
import io.github.sadellie.sukko.core.model.basic.ArrangementSource
import io.github.sadellie.sukko.core.model.layer.ColdRowLayer
import io.github.sadellie.sukko.core.ui.ExpandableListItem
import io.github.sadellie.sukko.core.ui.ListItem2Compact
import io.github.sadellie.sukko.core.ui.ModalBottomSheetWithItems
import io.github.sadellie.sukko.core.ui.expand
import io.github.sadellie.sukko.core.ui.lastShape
import io.github.sadellie.sukko.core.ui.middleShape
import io.github.sadellie.sukko.feature.editor.selector.FixedDpSelectorSheet
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.editor_parameters_alignment
import io.github.sadellie.sukko.resources.editor_parameters_arrangement
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun EditorParametersRowLayer(
  onUpdateLayer: (ColdRowLayer) -> Unit,
  layer: ColdRowLayer,
  compactListMode: Boolean,
) {
  EditorParametersArrangement(
    onUpdateLayer = onUpdateLayer,
    layer = layer,
    compactListMode = compactListMode,
  )
  EditorParametersAlignment(
    onUpdateLayer = onUpdateLayer,
    layer = layer,
    compactListMode = compactListMode,
  )
}

@Composable
private fun EditorParametersArrangement(
  onUpdateLayer: (ColdRowLayer) -> Unit,
  layer: ColdRowLayer,
  compactListMode: Boolean,
) {
  val sheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
  ExpandableListItem(
    headlineText = stringResource(Res.string.editor_parameters_arrangement),
    supportingText = stringResource(layer.arrangementSource.displayName),
    compactListMode = compactListMode,
    shape = ListItemDefaults.middleShape,
  ) {
    ListItem2Compact(
      headlineContent = { Text(stringResource(Res.string.editor_parameters_arrangement)) },
      supportingContent = { Text(stringResource(layer.arrangementSource.displayName)) },
      compactListMode = compactListMode,
      modifier = Modifier.clickable { sheetState.expand() },
      shape = RectangleShape,
    )
    AnimatedContent(targetState = layer.arrangementSource) { arrangementSource ->
      Column {
        when (arrangementSource) {
          is ArrangementSource.SpacedBy ->
            EditorArrangementSourceSpacedBy(
              onUpdate = { onUpdateLayer(layer.copy(arrangementSource = it)) },
              compactListMode = compactListMode,
              arrangementSource = arrangementSource,
            )
          is ArrangementSource.SpacedByHorizontal ->
            EditorArrangementSourceSpacedByHorizontal(
              onUpdate = { onUpdateLayer(layer.copy(arrangementSource = it)) },
              compactListMode = compactListMode,
              arrangementSource = arrangementSource,
            )
          else -> Unit
        }
      }
    }
  }

  ModalBottomSheetWithItems(
    state = sheetState,
    items = ArrangementSource.allHorizontal,
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
    headlineContent = { Text(stringResource(arrangementSource.displayName)) },
    supportingContent = { Text(arrangementSource.space.toString()) },
    compactListMode = compactListMode,
    modifier = Modifier.clickable { sheetState.expand() },
    shape = RectangleShape,
  )
  FixedDpSelectorSheet(
    state = sheetState,
    onValueSelected = {
      val updatedArrangement = arrangementSource.copy(space = it)
      onUpdate(updatedArrangement)
    },
    value = arrangementSource.space,
  )
}

@Composable
private fun EditorArrangementSourceSpacedByHorizontal(
  onUpdate: (ArrangementSource.SpacedByHorizontal) -> Unit,
  arrangementSource: ArrangementSource.SpacedByHorizontal,
  compactListMode: Boolean,
) {
  val sheetSpaceState = rememberModalBottomSheetState(SheetDetent.Hidden)
  val sheetAlignmentState = rememberModalBottomSheetState(SheetDetent.Hidden)
  ListItem2Compact(
    headlineContent = { Text(stringResource(arrangementSource.displayName)) },
    supportingContent = { Text(arrangementSource.space.toString()) },
    compactListMode = compactListMode,
    modifier = Modifier.clickable { sheetSpaceState.expand() },
    shape = RectangleShape,
  )
  ListItem2Compact(
    headlineContent = { Text(stringResource(Res.string.editor_parameters_alignment)) },
    supportingContent = { Text(stringResource(arrangementSource.alignmentSource.displayName)) },
    compactListMode = compactListMode,
    modifier = Modifier.clickable { sheetAlignmentState.expand() },
    shape = RectangleShape,
  )

  FixedDpSelectorSheet(
    state = sheetSpaceState,
    onValueSelected = { onUpdate(arrangementSource.copy(space = it)) },
    value = arrangementSource.space,
  )
  ModalBottomSheetWithItems(
    state = sheetAlignmentState,
    items = AlignmentSource.allHorizontal,
    headlineText = { stringResource(it.displayName) },
    onClick = { onUpdate(arrangementSource.copy(alignmentSource = it)) },
  )
}

@Composable
private fun EditorParametersAlignment(
  onUpdateLayer: (ColdRowLayer) -> Unit,
  layer: ColdRowLayer,
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
    items = AlignmentSource.allVertical,
    headlineText = { stringResource(it.displayName) },
    onClick = { onUpdateLayer(layer.copy(alignmentSource = it)) },
  )
}

@Composable
@Preview
private fun PreviewEditorParametersRowLayer() {
  var layer by remember { mutableStateOf(ColdRowLayer(id = 0)) }
  Column {
    EditorParametersRowLayer(onUpdateLayer = { layer = it }, layer = layer, compactListMode = false)
  }
}
