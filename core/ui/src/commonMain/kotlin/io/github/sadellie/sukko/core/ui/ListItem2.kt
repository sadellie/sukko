package io.github.sadellie.sukko.core.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import io.github.sadellie.sukko.core.designsystem.theme.ListArrangement
import io.github.sadellie.sukko.core.designsystem.theme.Sizes
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ListItem2Compact(
  modifier: Modifier = Modifier,
  headlineContent: @Composable () -> Unit,
  compactListMode: Boolean,
  supportingContent: @Composable (() -> Unit)? = null,
  leadingContent: @Composable (() -> Unit)? = null,
  trailingContent: @Composable (() -> Unit)? = null,
  colors: ListItemColors =
    ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceBright),
  tonalElevation: Dp = ListItemDefaults.Elevation,
  shadowElevation: Dp = ListItemDefaults.Elevation,
  shape: Shape,
) {
  Crossfade(compactListMode) {
    if (it) {
      ListItem2(
        modifier = modifier,
        headlineContent = headlineContent,
        supportingContent = null,
        leadingContent = leadingContent,
        trailingContent = trailingContent,
        colors = colors,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
        shape = shape,
      )
    } else {
      ListItem2(
        modifier = modifier,
        headlineContent = headlineContent,
        supportingContent = supportingContent,
        leadingContent = leadingContent,
        trailingContent = trailingContent,
        colors = colors,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
        shape = shape,
      )
    }
  }
}

@Composable
fun ListItem2(
  modifier: Modifier = Modifier,
  headlineContent: @Composable () -> Unit,
  overlineContent: @Composable (() -> Unit)? = null,
  supportingContent: @Composable (() -> Unit)? = null,
  leadingContent: @Composable (() -> Unit)? = null,
  trailingContent: @Composable (() -> Unit)? = null,
  colors: ListItemColors =
    ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceBright),
  tonalElevation: Dp = ListItemDefaults.Elevation,
  shadowElevation: Dp = ListItemDefaults.Elevation,
  shape: Shape,
) {
  ListItem(
    modifier = Modifier.clip(shape).then(modifier),
    overlineContent = overlineContent,
    headlineContent = headlineContent,
    supportingContent = supportingContent,
    leadingContent = leadingContent,
    trailingContent = trailingContent,
    colors = colors,
    tonalElevation = tonalElevation,
    shadowElevation = shadowElevation,
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
    ListItem2(headlineContent = { Text("List item") }, shape = ListItemDefaults.listedShape(0, 2))
    ListItem2(
      headlineContent = { Text("List item") },
      supportingContent = { Text("Item supporting text") },
      shape = ListItemDefaults.listedShape(1, 2),
    )
  }
}
