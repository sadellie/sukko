package io.github.sadellie.sukko.feature.editor

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.sadellie.sukko.core.designsystem.theme.ListArrangement
import io.github.sadellie.sukko.core.designsystem.theme.Sizes
import io.github.sadellie.sukko.core.ui.ListItem2
import io.github.sadellie.sukko.core.ui.RemoveButton
import io.github.sadellie.sukko.core.ui.listedShapes
import sh.calvin.reorderable.ReorderableCollectionItemScope
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
internal fun <T> EditorList(
  modifier: Modifier,
  onAddItemClick: () -> Unit,
  addButtonLabel: String,
  items: List<T>,
  contentPadding: PaddingValues,
  key: (T) -> Any,
  itemContent: @Composable EditorListItemScope<T>.(item: T) -> Unit,
) {
  Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(Sizes.small)) {
    val hapticFeedback = LocalHapticFeedback.current
    val listState = rememberLazyListState()
    var mutableItemsList by remember(items) { mutableStateOf(items) }
    var isInEditingMode by rememberSaveable { mutableStateOf(false) }

    ListControlButtons(
      onEditModeUpdate = { isInEditingMode = !isInEditingMode },
      isInEditingMode = isInEditingMode,
      onAddClick = onAddItemClick,
      addButtonLabel = addButtonLabel,
    )
    val reorderableLazyListState =
      rememberReorderableLazyListState(listState) { from, to ->
        mutableItemsList =
          mutableItemsList.toMutableList().apply { add(to.index, removeAt(from.index)) }
        hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
      }

    LazyColumn(
      state = listState,
      verticalArrangement = ListArrangement,
      contentPadding = contentPadding,
    ) {
      itemsIndexed(mutableItemsList, key = { _, item -> key(item) }) { index, item ->
        ReorderableItem(state = reorderableLazyListState, key = key(item)) {
          val itemScope =
            remember(index, item, isInEditingMode) {
              EditorListItemScope(
                reorderableCollectionItemScope = this,
                index = index,
                isInEditingMode = isInEditingMode,
                reorderedList = { mutableItemsList },
              )
            }
          itemScope.itemContent(item)
        }
      }
    }
  }
}

internal data class EditorListItemScope<T>(
  val reorderableCollectionItemScope: ReorderableCollectionItemScope,
  val index: Int,
  val isInEditingMode: Boolean,
  val reorderedList: () -> List<T>,
)

@Composable
@Preview
private fun PreviewEditorList() {
  var items by remember { mutableStateOf(List(20) { "Item $it" }) }
  EditorList(
    modifier = Modifier.fillMaxSize(),
    onAddItemClick = {},
    addButtonLabel = "Add item",
    items = items,
    contentPadding = PaddingValues(0.dp),
    key = { it },
  ) { item ->
    val itemScope = this@EditorList
    AnimatedContent(
      targetState = this.isInEditingMode,
      transitionSpec = { fadeIn() togetherWith fadeOut() },
    ) {
      if (it) {
        ListItem2(
          content = { Text(item) },
          leadingContent = {
            itemScope.reorderableCollectionItemScope.LayerListItemDragHandle(
              onDragStopped = { items = itemScope.reorderedList() }
            )
          },
          trailingContent = { RemoveButton({}) },
          shapes = ListItemDefaults.listedShapes(items.size, itemScope.index),
          onClick = {},
        )
      } else {
        ListItem2(
          content = { Text(item) },
          shapes = ListItemDefaults.listedShapes(items.size, itemScope.index),
          onClick = {},
        )
      }
    }
  }
}
