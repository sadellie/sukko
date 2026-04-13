package io.github.sadellie.sukko.feature.editor.modifiers

import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.composables.core.SheetDetent
import com.composables.core.rememberModalBottomSheetState
import io.github.sadellie.sukko.core.model.basic.LocalScriptableDisplay
import io.github.sadellie.sukko.core.model.modifier.ColdPaddingEachSideModifier
import io.github.sadellie.sukko.core.ui.ListItem2Compact
import io.github.sadellie.sukko.core.ui.expand
import io.github.sadellie.sukko.core.ui.middleShapes
import io.github.sadellie.sukko.feature.editor.selector.DoubleSelectorSheet
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.editor_modifiers_bottom
import io.github.sadellie.sukko.resources.editor_modifiers_end
import io.github.sadellie.sukko.resources.editor_modifiers_start
import io.github.sadellie.sukko.resources.editor_modifiers_top
import org.jetbrains.compose.resources.stringResource
import sh.calvin.reorderable.ReorderableCollectionItemScope

@Composable
internal fun ReorderableCollectionItemScope.EditorModifierPaddingEachSide(
  modifier: Modifier,
  widgetModifier: ColdPaddingEachSideModifier,
  onUpdateModifier: (ColdPaddingEachSideModifier) -> Unit,
  state: EditorModifierListItemState,
) {
  val scriptableDisplay = LocalScriptableDisplay.current
  val startDisplayString = scriptableDisplay.displayString(widgetModifier.start)
  val endDisplayString = scriptableDisplay.displayString(widgetModifier.end)
  val topDisplayString = scriptableDisplay.displayString(widgetModifier.top)
  val bottomDisplayString = scriptableDisplay.displayString(widgetModifier.bottom)
  EditorModifierExpandableListItem(
    modifier = modifier,
    headlineText = stringResource(widgetModifier.displayName),
    supportingText =
      "$startDisplayString | $endDisplayString | $topDisplayString | $bottomDisplayString",
    state = state,
  ) {
    val startSheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
    ListItem2Compact(
      content = { Text(stringResource(Res.string.editor_modifiers_start)) },
      supportingContent = { Text(startDisplayString) },
      compactListMode = state.compactListMode,
      onClick = startSheetState::expand,
      shapes = ListItemDefaults.middleShapes,
    )
    DoubleSelectorSheet(
      state = startSheetState,
      onValueSelected = { onUpdateModifier(widgetModifier.copy(start = it)) },
      value = widgetModifier.start,
      range = ColdPaddingEachSideModifier.valueRange,
      globals = state.globals,
      allowFraction = true,
    )

    val endSheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
    ListItem2Compact(
      content = { Text(stringResource(Res.string.editor_modifiers_end)) },
      supportingContent = { Text(endDisplayString) },
      compactListMode = state.compactListMode,
      onClick = endSheetState::expand,
      shapes = ListItemDefaults.middleShapes,
    )
    DoubleSelectorSheet(
      state = endSheetState,
      onValueSelected = { onUpdateModifier(widgetModifier.copy(end = it)) },
      value = widgetModifier.end,
      range = ColdPaddingEachSideModifier.valueRange,
      globals = state.globals,
      allowFraction = true,
    )

    val topSheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
    ListItem2Compact(
      content = { Text(stringResource(Res.string.editor_modifiers_top)) },
      supportingContent = { Text(topDisplayString) },
      compactListMode = state.compactListMode,
      onClick = topSheetState::expand,
      shapes = ListItemDefaults.middleShapes,
    )
    DoubleSelectorSheet(
      state = topSheetState,
      onValueSelected = { onUpdateModifier(widgetModifier.copy(top = it)) },
      value = widgetModifier.top,
      range = ColdPaddingEachSideModifier.valueRange,
      globals = state.globals,
      allowFraction = true,
    )

    val bottomSheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
    ListItem2Compact(
      content = { Text(stringResource(Res.string.editor_modifiers_bottom)) },
      supportingContent = { Text(bottomDisplayString) },
      compactListMode = state.compactListMode,
      onClick = bottomSheetState::expand,
      shapes = ListItemDefaults.middleShapes,
    )
    DoubleSelectorSheet(
      state = bottomSheetState,
      onValueSelected = { onUpdateModifier(widgetModifier.copy(bottom = it)) },
      value = widgetModifier.bottom,
      range = ColdPaddingEachSideModifier.valueRange,
      globals = state.globals,
      allowFraction = true,
    )
  }
}
