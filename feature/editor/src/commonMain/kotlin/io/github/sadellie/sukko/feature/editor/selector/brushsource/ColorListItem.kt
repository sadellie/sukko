package io.github.sadellie.sukko.feature.editor.selector.brushsource

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ListItemShapes
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.RoundedPolygon
import io.github.sadellie.sukko.core.model.basic.LocalScriptableDisplay
import io.github.sadellie.sukko.core.model.basic.ScriptableColor
import io.github.sadellie.sukko.core.ui.ListItem2
import io.github.sadellie.sukko.core.ui.RemoveButton

@Composable
internal fun ColorListItem(
  modifier: Modifier,
  scriptableColor: ScriptableColor,
  color: Color,
  isSelected: Boolean = false,
  onClick: () -> Unit,
  onRemove: (() -> Unit)? = null,
  onIconClick: () -> Unit = {},
  isRemoveEnabled: Boolean = true,
  polygon: RoundedPolygon = MaterialShapes.Pill,
  shapes: ListItemShapes,
) {
  val selectedTransition = updateTransition(isSelected)
  val backgroundColor =
    selectedTransition.animateColor {
      if (it) MaterialTheme.colorScheme.primaryContainer
      else MaterialTheme.colorScheme.surfaceBright
    }
  val headlineColor =
    selectedTransition.animateColor {
      if (it) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
    }

  ListItem2(
    modifier = modifier,
    onClick = onClick,
    colors =
      ListItemDefaults.colors(
        containerColor = backgroundColor.value,
        headlineColor = headlineColor.value,
      ),
    content = { Text(LocalScriptableDisplay.current.displayString(scriptableColor)) },
    leadingContent = {
      val shape = polygon.toShape()
      Box(
        Modifier.clip(shape)
          .clickable(onClick = onIconClick)
          .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape)
          .background(color)
          .size(40.dp)
      )
    },
    trailingContent =
      onRemove?.let { { RemoveButton(onRemoveClick = onRemove, enabled = isRemoveEnabled) } },
    shapes = shapes,
  )
}
