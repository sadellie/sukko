package io.github.sadellie.sukko.feature.editor

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import google.material.design.symbols.Add
import google.material.design.symbols.Check
import google.material.design.symbols.DragHandle
import google.material.design.symbols.Edit
import google.material.design.symbols.Symbols
import io.github.sadellie.sukko.core.designsystem.Preview2
import io.github.sadellie.sukko.core.designsystem.theme.Sizes
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.editor_list_control_done
import io.github.sadellie.sukko.resources.editor_list_control_edit_list
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import sh.calvin.reorderable.ReorderableCollectionItemScope

@Composable
internal fun ListControlButtons(
  modifier: Modifier = Modifier,
  onEditModeUpdate: () -> Unit,
  isInEditingMode: Boolean,
  onAddClick: () -> Unit,
  addButtonLabel: String,
) {
  Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(Sizes.small)) {
    ToggleButton(
      modifier = Modifier.heightIn(ButtonDefaults.MinHeight),
      shapes = ToggleButtonDefaults.shapes(),
      contentPadding = ButtonDefaults.SmallContentPadding,
      onCheckedChange = { onEditModeUpdate() },
      checked = isInEditingMode,
    ) {
      AnimatedContent(isInEditingMode) { editingModeEnabled ->
        Row {
          val label =
            stringResource(
              if (editingModeEnabled) Res.string.editor_list_control_done
              else Res.string.editor_list_control_edit_list
            )
          val icon = if (editingModeEnabled) Symbols.Check else Symbols.Edit

          Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(ButtonDefaults.SmallIconSize),
          )
          Spacer(Modifier.size(ButtonDefaults.IconSpacing))
          Text(text = label, minLines = 1, maxLines = 1)
        }
      }
    }
    FilledTonalButton(
      modifier = Modifier.heightIn(ButtonDefaults.MinHeight).weight(1f),
      shapes = ButtonDefaults.shapes(),
      contentPadding = ButtonDefaults.SmallContentPadding,
      onClick = onAddClick,
      enabled = !isInEditingMode,
    ) {
      Icon(
        imageVector = Symbols.Add,
        contentDescription = addButtonLabel,
        modifier = Modifier.size(ButtonDefaults.SmallIconSize),
      )
      Spacer(Modifier.size(ButtonDefaults.IconSpacing))
      Text(text = addButtonLabel, minLines = 1, maxLines = 1)
    }
  }
}

@Composable
fun ReorderableCollectionItemScope.LayerListItemDragHandle(
  modifier: Modifier = Modifier,
  onDragStopped: () -> Unit,
) {
  val hapticFeedback = LocalHapticFeedback.current
  IconButton(
    modifier =
      modifier
        .draggableHandle(
          onDragStarted = {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
          },
          onDragStopped = {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureEnd)
            onDragStopped()
          },
        )
        .size(
          IconButtonDefaults.smallContainerSize(IconButtonDefaults.IconButtonWidthOption.Narrow)
        ),
    onClick = {},
    shapes = IconButtonDefaults.shapes(),
  ) {
    Icon(
      imageVector = Symbols.DragHandle,
      contentDescription = null,
      modifier = Modifier.size(IconButtonDefaults.smallIconSize),
    )
  }
}

@Composable
@Preview
private fun PreviewListControlButtons() = Preview2 {
  Surface {
    var isInEditingMode by remember { mutableStateOf(false) }
    ListControlButtons(
      onEditModeUpdate = { isInEditingMode = !isInEditingMode },
      isInEditingMode = isInEditingMode,
      onAddClick = {},
      addButtonLabel = "Add item",
    )
  }
}
