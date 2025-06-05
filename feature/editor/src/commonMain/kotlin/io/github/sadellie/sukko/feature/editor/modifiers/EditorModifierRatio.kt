package io.github.sadellie.sukko.feature.editor.modifiers

import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import com.composables.core.SheetDetent
import com.composables.core.rememberModalBottomSheetState
import io.github.sadellie.sukko.core.model.basic.LocalScriptableDisplay
import io.github.sadellie.sukko.core.model.modifier.ColdAspectRatioModifier
import io.github.sadellie.sukko.core.ui.ListItem2Compact
import io.github.sadellie.sukko.core.ui.expand
import io.github.sadellie.sukko.feature.editor.selector.BooleanSelectorSheet
import io.github.sadellie.sukko.feature.editor.selector.DoubleSelectorSheet
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.editor_modifiers_match_height_constraints_first
import io.github.sadellie.sukko.resources.editor_modifiers_ratio
import org.jetbrains.compose.resources.stringResource
import sh.calvin.reorderable.ReorderableCollectionItemScope

@Composable
internal fun ReorderableCollectionItemScope.EditorModifierAspectRatio(
  modifier: Modifier,
  widgetModifier: ColdAspectRatioModifier,
  onUpdateModifier: (ColdAspectRatioModifier) -> Unit,
  state: EditorModifierListItemState,
) {
  val scriptableDisplay = LocalScriptableDisplay.current
  val ratioDisplayString = scriptableDisplay.displayString(widgetModifier.ratio)
  EditorModifierExpandableListItem(
    modifier = modifier,
    headlineText = stringResource(widgetModifier.displayName),
    supportingText = ratioDisplayString,
    state = state,
  ) {
    val weightSheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
    ListItem2Compact(
      headlineContent = { Text(stringResource(Res.string.editor_modifiers_ratio)) },
      supportingContent = { Text(ratioDisplayString) },
      compactListMode = state.compactListMode,
      modifier = Modifier.clickable { weightSheetState.expand() },
      shape = RectangleShape,
    )
    DoubleSelectorSheet(
      state = weightSheetState,
      onValueSelected = { onUpdateModifier(widgetModifier.copy(ratio = it)) },
      value = widgetModifier.ratio,
      range = ColdAspectRatioModifier.ratioRange,
      allowFraction = true,
      globals = state.globals.doubles,
    )

    val fillSheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
    ListItem2Compact(
      headlineContent = {
        Text(stringResource(Res.string.editor_modifiers_match_height_constraints_first))
      },
      supportingContent = {
        Text(scriptableDisplay.displayString(widgetModifier.matchHeightConstraintsFirst))
      },
      compactListMode = state.compactListMode,
      modifier = Modifier.clickable { fillSheetState.expand() },
      shape = RectangleShape,
    )
    BooleanSelectorSheet(
      state = fillSheetState,
      onValueSelected = { onUpdateModifier(widgetModifier.copy(matchHeightConstraintsFirst = it)) },
      value = widgetModifier.matchHeightConstraintsFirst,
      globals = state.globals.booleans,
    )
  }
}
