package io.github.sadellie.sukko.core.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ListItemElevation
import androidx.compose.material3.ListItemShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import io.github.sadellie.sukko.core.designsystem.theme.ListArrangement
import io.github.sadellie.sukko.core.designsystem.theme.Sizes

@Composable
fun ListItem2Compact(
  onClick: () -> Unit,
  shapes: ListItemShapes,
  compactListMode: Boolean,
  modifier: Modifier = Modifier,
  selected: Boolean = false,
  enabled: Boolean = true,
  leadingContent: @Composable (() -> Unit)? = null,
  trailingContent: @Composable (() -> Unit)? = null,
  overlineContent: @Composable (() -> Unit)? = null,
  supportingContent: @Composable (() -> Unit)? = null,
  verticalAlignment: Alignment.Vertical = ListItemDefaults.verticalAlignment(),
  onLongClick: (() -> Unit)? = null,
  onLongClickLabel: String? = null,
  colors: ListItemColors =
    ListItemDefaults.segmentedColors(containerColor = MaterialTheme.colorScheme.surfaceBright),
  elevation: ListItemElevation = ListItemDefaults.elevation(),
  contentPadding: PaddingValues = ListItemDefaults.ContentPadding,
  interactionSource: MutableInteractionSource? = null,
  content: @Composable () -> Unit,
) {
  Crossfade(compactListMode) {
    if (it) {
      ListItem2(
        selected = selected,
        onClick = onClick,
        shapes = shapes,
        modifier = modifier,
        enabled = enabled,
        leadingContent = leadingContent,
        trailingContent = trailingContent,
        overlineContent = overlineContent,
        supportingContent = null,
        verticalAlignment = verticalAlignment,
        onLongClick = onLongClick,
        onLongClickLabel = onLongClickLabel,
        colors = colors,
        elevation = elevation,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = content,
      )
    } else {
      ListItem2(
        selected = selected,
        onClick = onClick,
        shapes = shapes,
        modifier = modifier,
        enabled = enabled,
        leadingContent = leadingContent,
        trailingContent = trailingContent,
        overlineContent = overlineContent,
        supportingContent = supportingContent,
        verticalAlignment = verticalAlignment,
        onLongClick = onLongClick,
        onLongClickLabel = onLongClickLabel,
        colors = colors,
        elevation = elevation,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = content,
      )
    }
  }
}

@Composable
fun ListItem2(
  onClick: () -> Unit,
  shapes: ListItemShapes,
  modifier: Modifier = Modifier,
  selected: Boolean = false,
  enabled: Boolean = true,
  leadingContent: @Composable (() -> Unit)? = null,
  trailingContent: @Composable (() -> Unit)? = null,
  overlineContent: @Composable (() -> Unit)? = null,
  supportingContent: @Composable (() -> Unit)? = null,
  verticalAlignment: Alignment.Vertical = ListItemDefaults.verticalAlignment(),
  onLongClick: (() -> Unit)? = null,
  onLongClickLabel: String? = null,
  colors: ListItemColors =
    ListItemDefaults.segmentedColors(containerColor = MaterialTheme.colorScheme.surfaceBright),
  elevation: ListItemElevation = ListItemDefaults.elevation(),
  contentPadding: PaddingValues = ListItemDefaults.ContentPadding,
  interactionSource: MutableInteractionSource? = null,
  content: @Composable () -> Unit,
) {
  SegmentedListItem(
    selected = selected,
    onClick = onClick,
    shapes = shapes,
    modifier = modifier,
    enabled = enabled,
    leadingContent = leadingContent,
    trailingContent = trailingContent,
    overlineContent = overlineContent,
    supportingContent = supportingContent,
    verticalAlignment = verticalAlignment,
    onLongClick = onLongClick,
    onLongClickLabel = onLongClickLabel,
    colors = colors,
    elevation = elevation,
    contentPadding = contentPadding,
    interactionSource = interactionSource,
    content = content,
  )
}

@Stable
fun ListItemDefaults.listedShape(indexInList: Int, listSize: Int): Shape {
  val isFirst = indexInList == 0
  val isLast = indexInList == listSize - 1

  return when {
    isFirst && isLast -> singleShape
    isFirst -> firstShape
    isLast -> lastShape
    else -> middleShape
  }
}

@Composable
fun ListItemDefaults.listedShapes(indexInList: Int, listSize: Int): ListItemShapes {
  return segmentedShapes(
    index = indexInList,
    count = listSize,
    defaultShapes = shapes(if (listSize == 1) singleShape else middleShape),
  )
}

@Stable
val ListItemDefaults.firstShapes: ListItemShapes
  @Composable get() = shapes(ListItemDefaults.firstShape)

@Stable
val ListItemDefaults.middleShapes: ListItemShapes
  @Composable get() = shapes(ListItemDefaults.middleShape)

@Stable
val ListItemDefaults.lastShapes: ListItemShapes
  @Composable get() = shapes(ListItemDefaults.lastShape)

@Stable
val ListItemDefaults.singleShapes: ListItemShapes
  @Composable get() = shapes(ListItemDefaults.singleShape)

@Suppress("UnusedReceiverParameter")
@Stable
val ListItemDefaults.firstShape: Shape
  get() = RoundedCornerShape(Sizes.large, Sizes.large, Sizes.extraSmall, Sizes.extraSmall)

@Suppress("UnusedReceiverParameter")
@Stable
val ListItemDefaults.middleShape: Shape
  get() = RoundedCornerShape(Sizes.extraSmall)

@Suppress("UnusedReceiverParameter")
@Stable
val ListItemDefaults.lastShape: Shape
  get() = RoundedCornerShape(Sizes.extraSmall, Sizes.extraSmall, Sizes.large, Sizes.large)

@Suppress("UnusedReceiverParameter")
@Stable
val ListItemDefaults.singleShape: Shape
  get() = RoundedCornerShape(Sizes.large)

@Composable
@Preview
private fun PreviewListItem2() {
  Column(verticalArrangement = ListArrangement) {
    ListItem2(
      shapes = ListItemDefaults.firstShapes,
      content = { Text("List item") },
      supportingContent = { Text("List item") },
      onClick = {},
    )
    ListItem2Compact(
      shapes = ListItemDefaults.firstShapes,
      content = { Text("List item") },
      supportingContent = { Text("List item") },
      onClick = {},
      compactListMode = false,
    )
    ListItem2Compact(
      shapes = ListItemDefaults.firstShapes,
      content = { Text("List item") },
      supportingContent = { Text("List item") },
      onClick = {},
      compactListMode = true,
    )
  }
}
