package io.github.sadellie.sukko.feature.editor.modifiers

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.ListItemShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.ui.ExpandableButton
import io.github.sadellie.sukko.core.ui.ExpandableListItem
import io.github.sadellie.sukko.core.ui.ListItem2Compact
import io.github.sadellie.sukko.core.ui.RemoveButton
import io.github.sadellie.sukko.feature.editor.LayerListItemDragHandle
import sh.calvin.reorderable.ReorderableCollectionItemScope

@Immutable
internal data class EditorModifierListItemState(
  val isExpanded: Boolean,
  val isInEditingMode: Boolean,
  val compactListMode: Boolean,
  val onExpandedUpdate: () -> Unit,
  val onRemoveClick: () -> Unit,
  val onDragStopped: () -> Unit,
  val shapes: ListItemShapes,
  val globals: Globals,
)

@Composable
internal fun ReorderableCollectionItemScope.EditorModifierFlatListItem(
  modifier: Modifier,
  headlineText: String,
  supportingText: String,
  state: EditorModifierListItemState,
  onClick: () -> Unit,
) {
  val spatialSpec = MaterialTheme.motionScheme.defaultSpatialSpec<Float>()
  AnimatedContent(
    targetState = state.isInEditingMode,
    transitionSpec = { fadeIn(spatialSpec) togetherWith fadeOut(spatialSpec) },
  ) { editing ->
    if (editing) {
      ListItem2Compact(
        modifier = modifier,
        compactListMode = state.compactListMode,
        content = { Text(headlineText) },
        supportingContent = { Text(supportingText) },
        leadingContent = { LayerListItemDragHandle(onDragStopped = state.onDragStopped) },
        trailingContent = { RemoveButton(state.onRemoveClick) },
        shapes = state.shapes,
        onClick = onClick,
      )
    } else {
      ListItem2Compact(
        modifier = modifier,
        compactListMode = state.compactListMode,
        content = { Text(headlineText) },
        supportingContent = { Text(supportingText) },
        shapes = state.shapes,
        onClick = onClick,
      )
    }
  }
}

@Composable
internal fun ReorderableCollectionItemScope.EditorModifierExpandableListItem(
  modifier: Modifier,
  headlineText: String,
  supportingText: String,
  state: EditorModifierListItemState,
  expandedContent: @Composable ColumnScope.() -> Unit,
) {
  val spatialSpec = MaterialTheme.motionScheme.defaultSpatialSpec<Float>()
  AnimatedContent(
    targetState = state.isInEditingMode,
    transitionSpec = { fadeIn(spatialSpec) togetherWith fadeOut(spatialSpec) },
  ) { editing ->
    if (editing) {
      ListItem2Compact(
        modifier = modifier,
        onClick = state.onExpandedUpdate,
        compactListMode = state.compactListMode,
        content = { Text(headlineText) },
        supportingContent = { Text(supportingText) },
        leadingContent = { LayerListItemDragHandle(onDragStopped = state.onDragStopped) },
        trailingContent = { RemoveButton(state.onRemoveClick) },
        shapes = state.shapes,
      )
    } else {
      ExpandableListItem(
        modifier = modifier,
        headlineText = headlineText,
        supportingText = supportingText,
        isExpanded = state.isExpanded,
        onExpandedUpdate = state.onExpandedUpdate,
        expandedContent = expandedContent,
        compactListMode = state.compactListMode,
        shapes = state.shapes,
        trailingContent = {
          ExpandableTrailingButton(
            onExpandedUpdate = state.onExpandedUpdate,
            onRemoveClick = state.onRemoveClick,
            isExpanded = state.isExpanded,
            isInEditingMode = state.isInEditingMode,
          )
        },
      )
    }
  }
}

@Composable
private fun ExpandableTrailingButton(
  onExpandedUpdate: () -> Unit,
  onRemoveClick: () -> Unit,
  isExpanded: Boolean,
  isInEditingMode: Boolean,
) {
  Crossfade(isInEditingMode) {
    if (it) {
      RemoveButton(onRemoveClick)
    } else {
      ExpandableButton(onExpandedUpdate, isExpanded)
    }
  }
}
