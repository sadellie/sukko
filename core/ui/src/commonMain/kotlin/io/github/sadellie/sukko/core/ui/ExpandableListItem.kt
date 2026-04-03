package io.github.sadellie.sukko.core.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ListItemShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import google.material.design.symbols.ChevronRight
import google.material.design.symbols.Symbols
import io.github.sadellie.sukko.core.designsystem.theme.ListArrangement

@Composable
fun ExpandableListItem(
  modifier: Modifier = Modifier,
  headlineText: String,
  supportingText: String? = null,
  leadingContent: (@Composable () -> Unit)? = null,
  compactListMode: Boolean,
  shapes: ListItemShapes,
  expandedContent: @Composable ColumnScope.() -> Unit,
) {
  var isExpanded by rememberSaveable { mutableStateOf(false) }
  ExpandableListItem(
    modifier = modifier,
    headlineText = headlineText,
    supportingText = supportingText,
    isExpanded = isExpanded,
    onExpandedUpdate = { isExpanded = !isExpanded },
    leadingContent = leadingContent,
    shapes = shapes,
    trailingContent = { ExpandableButton({ isExpanded = !isExpanded }, isExpanded) },
    compactListMode = compactListMode,
    expandedContent = expandedContent,
  )
}

@Composable
fun ExpandableListItem(
  modifier: Modifier = Modifier,
  headlineText: String,
  supportingText: String? = null,
  isExpanded: Boolean,
  onExpandedUpdate: () -> Unit,
  leadingContent: (@Composable () -> Unit)? = null,
  compactListMode: Boolean,
  shapes: ListItemShapes,
  trailingContent: (@Composable () -> Unit) = { ExpandableButton(onExpandedUpdate, isExpanded) },
  expandedContent: @Composable ColumnScope.() -> Unit,
) {
  Column(Modifier.clip(shapes.shape).then(modifier), verticalArrangement = ListArrangement) {
    ListItem2Compact(
      content = { Text(headlineText) },
      supportingContent = supportingText?.let { { Text(it) } },
      leadingContent = leadingContent,
      trailingContent = trailingContent,
      onClick = onExpandedUpdate,
      compactListMode = compactListMode,
      shapes = ListItemDefaults.middleShapes,
    )
    AnimatedVisibility(visible = isExpanded) {
      Column(content = expandedContent, verticalArrangement = ListArrangement)
    }
  }
}

@Composable
fun ExpandableButton(onExpandedUpdate: () -> Unit, isExpanded: Boolean) {
  IconButton(onClick = onExpandedUpdate, shapes = IconButtonDefaults.shapes()) {
    val rotation =
      animateFloatAsState(
        targetValue = if (isExpanded) -90f else 90f,
        animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec(),
      )
    Icon(
      imageVector = Symbols.ChevronRight,
      contentDescription = null,
      modifier = Modifier.rotate(rotation.value),
    )
  }
}

@Composable
@Preview
private fun PreviewExpandableListItem() {
  var isExpanded by rememberSaveable { mutableStateOf(true) }
  ExpandableListItem(
    modifier = Modifier.fillMaxWidth(),
    headlineText = "Title",
    supportingText = "Support",
    onExpandedUpdate = { isExpanded = !isExpanded },
    expandedContent = {
      repeat(5) {
        ListItem2Compact(
          content = { Text("Headline $it") },
          supportingContent = { Text("Supporting $it") },
          compactListMode = false,
          shapes = ListItemDefaults.middleShapes,
          onClick = {},
        )
      }
    },
    leadingContent = null,
    isExpanded = isExpanded,
    compactListMode = false,
    shapes = ListItemDefaults.singleShapes,
  )
}
