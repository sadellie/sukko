package io.github.sadellie.sukko.feature.editor.modifiers

import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.composables.core.SheetDetent
import com.composables.core.rememberModalBottomSheetState
import io.github.sadellie.sukko.core.model.basic.LocalScriptableDisplay
import io.github.sadellie.sukko.core.model.modifier.ColdOffsetModifier
import io.github.sadellie.sukko.core.ui.ListItem2Compact
import io.github.sadellie.sukko.core.ui.expand
import io.github.sadellie.sukko.core.ui.middleShapes
import io.github.sadellie.sukko.feature.editor.selector.DpSelectorSheet
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.editor_modifiers_offset_x
import io.github.sadellie.sukko.resources.editor_modifiers_offset_y
import org.jetbrains.compose.resources.stringResource
import sh.calvin.reorderable.ReorderableCollectionItemScope

@Composable
internal fun ReorderableCollectionItemScope.EditorModifierOffset(
  modifier: Modifier,
  widgetModifier: ColdOffsetModifier,
  onUpdateModifier: (ColdOffsetModifier) -> Unit,
  state: EditorModifierListItemState,
) {
  val scriptableDisplay = LocalScriptableDisplay.current
  val xDisplayString = scriptableDisplay.displayString(widgetModifier.x)
  val yDisplayString = scriptableDisplay.displayString(widgetModifier.y)
  EditorModifierExpandableListItem(
    modifier = modifier,
    headlineText = stringResource(widgetModifier.displayName),
    supportingText = "$xDisplayString | $yDisplayString",
    state = state,
  ) {
    val xSheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
    ListItem2Compact(
      content = { Text(stringResource(Res.string.editor_modifiers_offset_x)) },
      supportingContent = { Text(xDisplayString) },
      compactListMode = state.compactListMode,
      onClick = xSheetState::expand,
      shapes = ListItemDefaults.middleShapes,
    )
    DpSelectorSheet(
      state = xSheetState,
      onValueSelected = { onUpdateModifier(widgetModifier.copy(x = it)) },
      value = widgetModifier.x,
      globals = state.globals.dps,
    )

    val ySheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
    ListItem2Compact(
      content = { Text(stringResource(Res.string.editor_modifiers_offset_y)) },
      supportingContent = { Text(yDisplayString) },
      compactListMode = state.compactListMode,
      onClick = ySheetState::expand,
      shapes = ListItemDefaults.middleShapes,
    )
    DpSelectorSheet(
      state = ySheetState,
      onValueSelected = { onUpdateModifier(widgetModifier.copy(y = it)) },
      value = widgetModifier.y,
      globals = state.globals.dps,
    )
  }
}
