package io.github.sadellie.sukko.feature.editor.modifiers

import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.composables.core.SheetDetent
import com.composables.core.rememberModalBottomSheetState
import io.github.sadellie.sukko.core.model.basic.LocalScriptableDisplay
import io.github.sadellie.sukko.core.model.modifier.ColdPaddingAxisModifier
import io.github.sadellie.sukko.core.ui.ListItem2Compact
import io.github.sadellie.sukko.core.ui.expand
import io.github.sadellie.sukko.core.ui.middleShapes
import io.github.sadellie.sukko.feature.editor.selector.DpSelectorSheet
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.editor_modifiers_horizontal
import io.github.sadellie.sukko.resources.editor_modifiers_vertical
import org.jetbrains.compose.resources.stringResource
import sh.calvin.reorderable.ReorderableCollectionItemScope

@Composable
internal fun ReorderableCollectionItemScope.EditorModifierPaddingAxis(
  modifier: Modifier,
  widgetModifier: ColdPaddingAxisModifier,
  onUpdateModifier: (ColdPaddingAxisModifier) -> Unit,
  state: EditorModifierListItemState,
) {
  val scriptableDisplay = LocalScriptableDisplay.current
  val horizontalDisplayString = scriptableDisplay.displayString(widgetModifier.horizontal)
  val verticalDisplayString = scriptableDisplay.displayString(widgetModifier.vertical)
  EditorModifierExpandableListItem(
    modifier = modifier,
    headlineText = stringResource(widgetModifier.displayName),
    supportingText = "$horizontalDisplayString | $verticalDisplayString",
    state = state,
  ) {
    val horizontalSheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
    ListItem2Compact(
      content = { Text(stringResource(Res.string.editor_modifiers_horizontal)) },
      supportingContent = { Text(horizontalDisplayString) },
      compactListMode = state.compactListMode,
      onClick = horizontalSheetState::expand,
      shapes = ListItemDefaults.middleShapes,
    )
    DpSelectorSheet(
      state = horizontalSheetState,
      onValueSelected = { onUpdateModifier(widgetModifier.copy(horizontal = it)) },
      value = widgetModifier.horizontal,
      range = ColdPaddingAxisModifier.valueRange,
      globals = state.globals.dps,
    )

    val verticalSheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
    ListItem2Compact(
      content = { Text(stringResource(Res.string.editor_modifiers_vertical)) },
      supportingContent = { Text(verticalDisplayString) },
      compactListMode = state.compactListMode,
      onClick = verticalSheetState::expand,
      shapes = ListItemDefaults.middleShapes,
    )
    DpSelectorSheet(
      state = verticalSheetState,
      onValueSelected = { onUpdateModifier(widgetModifier.copy(vertical = it)) },
      value = widgetModifier.vertical,
      range = ColdPaddingAxisModifier.valueRange,
      globals = state.globals.dps,
    )
  }
}
