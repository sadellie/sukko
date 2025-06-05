package io.github.sadellie.sukko.feature.editor.modifiers

import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import com.composables.core.SheetDetent
import com.composables.core.rememberModalBottomSheetState
import io.github.sadellie.sukko.core.model.basic.LocalScriptableDisplay
import io.github.sadellie.sukko.core.model.modifier.ColdRowWeightModifier
import io.github.sadellie.sukko.core.ui.ListItem2Compact
import io.github.sadellie.sukko.core.ui.expand
import io.github.sadellie.sukko.feature.editor.selector.BooleanSelectorSheet
import io.github.sadellie.sukko.feature.editor.selector.DoubleSelectorSheet
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.editor_modifiers_fill
import io.github.sadellie.sukko.resources.editor_modifiers_weight
import org.jetbrains.compose.resources.stringResource
import sh.calvin.reorderable.ReorderableCollectionItemScope

@Composable
internal fun ReorderableCollectionItemScope.EditorModifierRowWeight(
  modifier: Modifier,
  widgetModifier: ColdRowWeightModifier,
  onUpdateModifier: (ColdRowWeightModifier) -> Unit,
  state: EditorModifierListItemState,
) {
  val scriptableDisplay = LocalScriptableDisplay.current
  val weightDisplayString = scriptableDisplay.displayString(widgetModifier.weight)
  EditorModifierExpandableListItem(
    modifier = modifier,
    headlineText = stringResource(widgetModifier.displayName),
    supportingText = weightDisplayString,
    state = state,
  ) {
    val weightSheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
    ListItem2Compact(
      headlineContent = { Text(stringResource(Res.string.editor_modifiers_weight)) },
      supportingContent = { Text(weightDisplayString) },
      compactListMode = state.compactListMode,
      modifier = Modifier.clickable { weightSheetState.expand() },
      shape = RectangleShape,
    )
    DoubleSelectorSheet(
      state = weightSheetState,
      onValueSelected = { onUpdateModifier(widgetModifier.copy(weight = it)) },
      value = widgetModifier.weight,
      range = ColdRowWeightModifier.weightRange,
      allowFraction = true,
      globals = state.globals.doubles,
    )

    val fillSheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
    ListItem2Compact(
      headlineContent = { Text(stringResource(Res.string.editor_modifiers_fill)) },
      supportingContent = { Text(scriptableDisplay.displayString(widgetModifier.fill)) },
      compactListMode = state.compactListMode,
      modifier = Modifier.clickable { fillSheetState.expand() },
      shape = RectangleShape,
    )
    BooleanSelectorSheet(
      state = fillSheetState,
      onValueSelected = { onUpdateModifier(widgetModifier.copy(fill = it)) },
      value = widgetModifier.fill,
      globals = state.globals.booleans,
    )
  }
}
