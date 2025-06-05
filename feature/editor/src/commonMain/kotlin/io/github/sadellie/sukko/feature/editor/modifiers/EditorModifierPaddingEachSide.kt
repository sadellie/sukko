package io.github.sadellie.sukko.feature.editor.modifiers

import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import com.composables.core.SheetDetent
import com.composables.core.rememberModalBottomSheetState
import io.github.sadellie.sukko.core.model.basic.LocalScriptableDisplay
import io.github.sadellie.sukko.core.model.modifier.ColdPaddingEachSideModifier
import io.github.sadellie.sukko.core.ui.ListItem2Compact
import io.github.sadellie.sukko.core.ui.expand
import io.github.sadellie.sukko.feature.editor.selector.DpSelectorSheet
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
      headlineContent = { Text(stringResource(Res.string.editor_modifiers_start)) },
      supportingContent = { Text(startDisplayString) },
      compactListMode = state.compactListMode,
      modifier = Modifier.clickable { startSheetState.expand() },
      shape = RectangleShape,
    )
    DpSelectorSheet(
      state = startSheetState,
      onValueSelected = { onUpdateModifier(widgetModifier.copy(start = it)) },
      value = widgetModifier.start,
      range = ColdPaddingEachSideModifier.valueRange,
      globals = state.globals.dps,
    )

    val endSheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
    ListItem2Compact(
      headlineContent = { Text(stringResource(Res.string.editor_modifiers_end)) },
      supportingContent = { Text(endDisplayString) },
      compactListMode = state.compactListMode,
      modifier = Modifier.clickable { endSheetState.expand() },
      shape = RectangleShape,
    )
    DpSelectorSheet(
      state = endSheetState,
      onValueSelected = { onUpdateModifier(widgetModifier.copy(end = it)) },
      value = widgetModifier.end,
      range = ColdPaddingEachSideModifier.valueRange,
      globals = state.globals.dps,
    )

    val topSheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
    ListItem2Compact(
      headlineContent = { Text(stringResource(Res.string.editor_modifiers_top)) },
      supportingContent = { Text(topDisplayString) },
      compactListMode = state.compactListMode,
      modifier = Modifier.clickable { topSheetState.expand() },
      shape = RectangleShape,
    )
    DpSelectorSheet(
      state = topSheetState,
      onValueSelected = { onUpdateModifier(widgetModifier.copy(top = it)) },
      value = widgetModifier.top,
      range = ColdPaddingEachSideModifier.valueRange,
      globals = state.globals.dps,
    )

    val bottomSheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
    ListItem2Compact(
      headlineContent = { Text(stringResource(Res.string.editor_modifiers_bottom)) },
      supportingContent = { Text(bottomDisplayString) },
      compactListMode = state.compactListMode,
      modifier = Modifier.clickable { bottomSheetState.expand() },
      shape = RectangleShape,
    )
    DpSelectorSheet(
      state = bottomSheetState,
      onValueSelected = { onUpdateModifier(widgetModifier.copy(bottom = it)) },
      value = widgetModifier.bottom,
      range = ColdPaddingEachSideModifier.valueRange,
      globals = state.globals.dps,
    )
  }
}
