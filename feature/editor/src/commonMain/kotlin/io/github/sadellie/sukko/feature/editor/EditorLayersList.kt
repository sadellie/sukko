package io.github.sadellie.sukko.feature.editor

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.composables.core.SheetDetent
import com.composables.core.rememberModalBottomSheetState
import google.material.design.symbols.Edit
import google.material.design.symbols.Symbols
import io.github.sadellie.sukko.core.designsystem.Preview2
import io.github.sadellie.sukko.core.model.layer.ColdBoxLayer
import io.github.sadellie.sukko.core.model.layer.ColdColumnLayer
import io.github.sadellie.sukko.core.model.layer.ColdImageLayer
import io.github.sadellie.sukko.core.model.layer.ColdProgressBarLayer
import io.github.sadellie.sukko.core.model.layer.ColdRowLayer
import io.github.sadellie.sukko.core.model.layer.ColdTextLayer
import io.github.sadellie.sukko.core.model.layer.Layer
import io.github.sadellie.sukko.core.ui.AlertDialogWithText
import io.github.sadellie.sukko.core.ui.AlertDialogWithTextField
import io.github.sadellie.sukko.core.ui.DropDownMenuWithButton
import io.github.sadellie.sukko.core.ui.ListItem2Compact
import io.github.sadellie.sukko.core.ui.ModalBottomSheetWithItems
import io.github.sadellie.sukko.core.ui.RemoveButton
import io.github.sadellie.sukko.core.ui.expand
import io.github.sadellie.sukko.core.ui.listedShape
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.common_delete
import io.github.sadellie.sukko.resources.common_rename
import io.github.sadellie.sukko.resources.editor_layer_name_placeholder
import io.github.sadellie.sukko.resources.editor_layers_add_layer_title
import io.github.sadellie.sukko.resources.editor_layers_delete_layer_text
import io.github.sadellie.sukko.resources.editor_layers_delete_layer_title
import io.github.sadellie.sukko.resources.editor_layers_layer_name
import io.github.sadellie.sukko.resources.editor_layers_rename_layer_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import sh.calvin.reorderable.ReorderableCollectionItemScope

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun EditorLayersList(
  modifier: Modifier,
  onNavigateToLayer: (Int?) -> Unit,
  onEvent: (EditorEvent.LayerAction) -> Unit,
  layers: List<Layer.Cold>,
  parentLayerId: Int?,
  compactListMode: Boolean,
  contentPadding: PaddingValues,
) {
  var alertState by remember { mutableStateOf<AlertState?>(null) }
  val sheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
  EditorList(
    modifier = modifier,
    onAddItemClick = { sheetState.expand() },
    addButtonLabel = stringResource(Res.string.editor_layers_add_layer_title),
    items = layers,
    contentPadding = contentPadding,
    key = { "${it.id}-" },
  ) { layer ->
    reorderableCollectionItemScope.LayerListItem(
      modifier = Modifier,
      onClick = { onNavigateToLayer(layer.id) },
      layer = layer,
      onRenameClick = { alertState = AlertState.Rename(layer) },
      onDeleteClick = { alertState = AlertState.Delete(layer) },
      onDragStopped = { onEvent(EditorEvent.LayerAction.Reorder(reorderedList())) },
      isInEditingMode = isInEditingMode,
      compactListMode = compactListMode,
      shape = ListItemDefaults.listedShape(index, layers.size),
    )
  }

  when (val currentAlertState = alertState) {
    is AlertState.Delete ->
      AlertDialogWithText(
        title = stringResource(Res.string.editor_layers_delete_layer_title),
        onDismiss = { alertState = null },
        onConfirm = { onEvent(EditorEvent.LayerAction.Delete(currentAlertState.layer.id)) },
        confirmButtonLabel = stringResource(Res.string.common_delete),
        text = stringResource(Res.string.editor_layers_delete_layer_text),
      )
    is AlertState.Rename ->
      AlertDialogWithTextField(
        title = stringResource(Res.string.editor_layers_rename_layer_title),
        onDismiss = { alertState = null },
        onConfirm = {
          val updatedLayer = currentAlertState.layer.updateName(it)
          onEvent(EditorEvent.LayerAction.Update(updatedLayer))
        },
        confirmButtonLabel = stringResource(Res.string.common_rename),
        textFieldState = rememberTextFieldState(currentAlertState.layer.name ?: ""),
        textFieldLabel = stringResource(Res.string.editor_layers_layer_name),
      )
    null -> Unit
  }

  ModalBottomSheetWithItems(
    state = sheetState,
    items =
      remember(parentLayerId) {
        listOf(
          ColdColumnLayer(id = 0, parentId = parentLayerId),
          ColdRowLayer(id = 0, parentId = parentLayerId),
          ColdBoxLayer(id = 0, parentId = parentLayerId),
          ColdTextLayer(id = 0, parentId = parentLayerId),
          ColdImageLayer(id = 0, parentId = parentLayerId),
          ColdProgressBarLayer(id = 0, parentId = parentLayerId),
        )
      },
    headlineText = { stringResource(it.displayName) },
    supportText = { stringResource(it.displayDescription) },
    leadingContent = { Icon(it.icon, null) },
    onClick = { onEvent(EditorEvent.LayerAction.Add(it)) },
  )
}

@Composable
private fun ReorderableCollectionItemScope.LayerListItem(
  modifier: Modifier,
  onClick: () -> Unit,
  layer: Layer.Cold,
  onRenameClick: () -> Unit,
  onDeleteClick: () -> Unit,
  onDragStopped: () -> Unit,
  isInEditingMode: Boolean,
  compactListMode: Boolean,
  shape: Shape,
) {
  val spatialSpec = MaterialTheme.motionScheme.defaultSpatialSpec<Float>()
  AnimatedContent(
    targetState = isInEditingMode,
    transitionSpec = { fadeIn(spatialSpec) togetherWith fadeOut(spatialSpec) },
  ) {
    val headlineText =
      layer.name ?: stringResource(Res.string.editor_layer_name_placeholder, layer.id)
    if (it) {
      ListItem2Compact(
        modifier = modifier,
        compactListMode = compactListMode,
        headlineContent = { Text(headlineText) },
        supportingContent = { Text(stringResource(layer.displayName)) },
        leadingContent = { LayerListItemDragHandle(onDragStopped = onDragStopped) },
        trailingContent = { RemoveButton(onDeleteClick) },
        shape = shape,
      )
    } else {
      ListItem2Compact(
        modifier = modifier.clickable(onClick = onClick),
        compactListMode = compactListMode,
        headlineContent = { Text(headlineText) },
        supportingContent = { Text(stringResource(layer.displayName)) },
        trailingContent = { LayerListItemDropDownMenu(onRenameClick) },
        shape = shape,
      )
    }
  }
}

@Composable
private fun LayerListItemDropDownMenu(onRenameClick: () -> Unit) {
  DropDownMenuWithButton {
    DropdownMenuItem(
      text = { Text(stringResource(Res.string.common_rename)) },
      onClick = {
        onRenameClick()
        closeMenu()
      },
      leadingIcon = { Icon(Symbols.Edit, contentDescription = null) },
    )
  }
}

private sealed interface AlertState {
  data class Delete(val layer: Layer.Cold) : AlertState

  data class Rename(val layer: Layer.Cold) : AlertState
}

@Preview
@Composable
private fun PreviewEditorLayersList() = Preview2 {
  var layers by remember {
    mutableStateOf(List<Layer.Cold>(15) { ColdTextLayer(id = it, parentId = null) })
  }
  EditorLayersList(
    modifier = Modifier.background(MaterialTheme.colorScheme.surface).fillMaxSize(),
    onNavigateToLayer = {},
    onEvent = {},
    layers = layers,
    parentLayerId = null,
    compactListMode = false,
    contentPadding = PaddingValues(0.dp),
  )
}
