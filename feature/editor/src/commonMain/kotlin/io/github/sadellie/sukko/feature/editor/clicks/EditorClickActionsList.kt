package io.github.sadellie.sukko.feature.editor.clicks

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ListItemShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.composables.core.SheetDetent
import com.composables.core.rememberModalBottomSheetState
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.basic.ClickAction
import io.github.sadellie.sukko.core.model.basic.LocalScriptableDisplay
import io.github.sadellie.sukko.core.model.layer.ColdTextLayer
import io.github.sadellie.sukko.core.model.layer.Layer
import io.github.sadellie.sukko.core.ui.ListItem2Compact
import io.github.sadellie.sukko.core.ui.ModalBottomSheetWithItems
import io.github.sadellie.sukko.core.ui.RemoveButton
import io.github.sadellie.sukko.core.ui.expand
import io.github.sadellie.sukko.core.ui.listedShapes
import io.github.sadellie.sukko.feature.editor.EditorEvent
import io.github.sadellie.sukko.feature.editor.EditorList
import io.github.sadellie.sukko.feature.editor.LayerListItemDragHandle
import io.github.sadellie.sukko.feature.editor.selector.AppSelectorSheet
import io.github.sadellie.sukko.feature.editor.selector.ScriptActionSelectorSheet
import io.github.sadellie.sukko.feature.editor.selector.StringSelectorSheet
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.common_not_selected
import io.github.sadellie.sukko.resources.editor_click_actions_add_click_action
import org.jetbrains.compose.resources.stringResource
import sh.calvin.reorderable.ReorderableCollectionItemScope

@Composable
internal fun EditorClickActionsList(
  modifier: Modifier,
  onEvent: (EditorEvent.ClickActionAction) -> Unit,
  layer: Layer.Cold,
  compactListMode: Boolean,
  contentPadding: PaddingValues,
  globals: Globals,
) {
  val sheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
  EditorList(
    modifier = modifier,
    onAddItemClick = { sheetState.expand() },
    addButtonLabel = stringResource(Res.string.editor_click_actions_add_click_action),
    items = layer.clickActions,
    contentPadding = contentPadding,
    key = { it.id },
  ) { clickAction ->
    reorderableCollectionItemScope.EditorClickActionsListItem(
      modifier = Modifier,
      onUpdate = { onEvent(EditorEvent.ClickActionAction.Update(it, layer.id)) },
      onDragStopped = { onEvent(EditorEvent.ClickActionAction.Reorder(reorderedList(), layer.id)) },
      onRemoveClick = { onEvent(EditorEvent.ClickActionAction.Delete(clickAction, layer.id)) },
      clickAction = clickAction,
      isInEditingMode = isInEditingMode,
      compactListMode = compactListMode,
      globals = globals,
      shapes = ListItemDefaults.listedShapes(index, layer.clickActions.size),
    )
  }

  ModalBottomSheetWithItems(
    state = sheetState,
    items = remember { ClickAction.Cold.values() },
    headlineText = { stringResource(it.displayName) },
    onClick = { onEvent(EditorEvent.ClickActionAction.Add(it, layer.id)) },
  )
}

@Composable
private fun ReorderableCollectionItemScope.EditorClickActionsListItem(
  modifier: Modifier,
  onUpdate: (ClickAction.Cold) -> Unit,
  onDragStopped: () -> Unit,
  onRemoveClick: () -> Unit,
  clickAction: ClickAction.Cold,
  isInEditingMode: Boolean,
  compactListMode: Boolean,
  globals: Globals,
  shapes: ListItemShapes,
) {
  when (clickAction) {
    is ClickAction.LaunchApp -> {
      val sheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
      EditorClickActionsListItemBasic(
        modifier = modifier,
        onClick = { sheetState.expand() },
        onRemoveClick = onRemoveClick,
        compactListMode = compactListMode,
        isInEditingMode = isInEditingMode,
        headlineText = stringResource(clickAction.displayName),
        supportingText = clickAction.label ?: stringResource(Res.string.common_not_selected),
        onDragStopped = onDragStopped,
        shapes = shapes,
      )
      AppSelectorSheet(
        state = sheetState,
        onValueSelected = { label, packageName ->
          onUpdate(clickAction.copy(label = label, packageName = packageName))
        },
        packageName = clickAction.packageName,
      )
    }
    is ClickAction.Cold.OpenLink -> {
      val sheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
      EditorClickActionsListItemBasic(
        modifier = modifier,
        onClick = { sheetState.expand() },
        onRemoveClick = onRemoveClick,
        compactListMode = compactListMode,
        isInEditingMode = isInEditingMode,
        headlineText = stringResource(clickAction.displayName),
        supportingText = LocalScriptableDisplay.current.displayString(clickAction.url),
        onDragStopped = onDragStopped,
        shapes = shapes,
      )
      StringSelectorSheet(
        state = sheetState,
        onValueSelected = { onUpdate(clickAction.copy(url = it)) },
        value = clickAction.url,
        globals = globals,
      )
    }
    is ClickAction.RunScript -> {
      val sheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
      EditorClickActionsListItemBasic(
        modifier = modifier,
        onClick = { sheetState.expand() },
        onRemoveClick = onRemoveClick,
        compactListMode = compactListMode,
        isInEditingMode = isInEditingMode,
        headlineText = stringResource(clickAction.displayName),
        onDragStopped = onDragStopped,
        shapes = shapes,
      )
      ScriptActionSelectorSheet(
        state = sheetState,
        onConfirm = { onUpdate(clickAction.copy(script = it)) },
        script = clickAction.script,
        globals = globals,
      )
    }
    is ClickAction.MediaPause,
    is ClickAction.MediaPlay,
    is ClickAction.MediaSkipToNext,
    is ClickAction.MediaSkipToPrevious,
    is ClickAction.MediaOpenPlayer ->
      EditorClickActionsListItemBasic(
        modifier = modifier,
        onRemoveClick = onRemoveClick,
        compactListMode = compactListMode,
        isInEditingMode = isInEditingMode,
        headlineText = stringResource(clickAction.displayName),
        onDragStopped = onDragStopped,
        shapes = shapes,
      )
  }
}

@Composable
private fun ReorderableCollectionItemScope.EditorClickActionsListItemBasic(
  modifier: Modifier,
  onClick: (() -> Unit) = {},
  onDragStopped: () -> Unit,
  onRemoveClick: () -> Unit,
  headlineText: String,
  supportingText: String? = null,
  compactListMode: Boolean,
  isInEditingMode: Boolean,
  shapes: ListItemShapes,
) {
  val spatialSpec = MaterialTheme.motionScheme.defaultSpatialSpec<Float>()
  val interactionSource = remember { MutableInteractionSource() }
  AnimatedContent(
    targetState = isInEditingMode,
    transitionSpec = { fadeIn(spatialSpec) togetherWith fadeOut(spatialSpec) },
  ) { editing ->
    if (editing) {
      ListItem2Compact(
        modifier = modifier,
        content = { Text(headlineText) },
        supportingContent = supportingText?.let { { Text(it) } },
        leadingContent = {
          LayerListItemDragHandle(
            interactionSource = interactionSource,
            onDragStopped = onDragStopped,
          )
        },
        trailingContent = { RemoveButton(onRemoveClick) },
        compactListMode = compactListMode,
        shapes = shapes,
        onClick = {},
        interactionSource = interactionSource,
      )
    } else {
      ListItem2Compact(
        modifier = modifier,
        content = { Text(headlineText) },
        supportingContent = supportingText?.let { { Text(it) } },
        compactListMode = compactListMode,
        shapes = shapes,
        onClick = onClick,
        interactionSource = interactionSource,
      )
    }
  }
}

@Composable
@Preview
private fun PreviewEditorClickActionsList() {
  val layer = remember {
    ColdTextLayer(
      id = 0,
      parentId = null,
      clickActions =
        ClickAction.Cold.values().mapIndexed { index, clickAction -> clickAction.updateId(index) },
    )
  }
  EditorClickActionsList(
    modifier = Modifier.background(MaterialTheme.colorScheme.surface).fillMaxSize(),
    layer = layer,
    onEvent = {},
    compactListMode = false,
    contentPadding = PaddingValues(0.dp),
    globals = Globals(),
  )
}
