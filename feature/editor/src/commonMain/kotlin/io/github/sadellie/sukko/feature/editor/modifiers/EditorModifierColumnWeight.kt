package io.github.sadellie.sukko.feature.editor.modifiers

import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.composables.core.SheetDetent
import com.composables.core.rememberModalBottomSheetState
import io.github.sadellie.sukko.core.model.basic.LocalScriptableDisplay
import io.github.sadellie.sukko.core.model.modifier.ColdColumnWeightModifier
import io.github.sadellie.sukko.core.ui.ListItem2Compact
import io.github.sadellie.sukko.core.ui.expand
import io.github.sadellie.sukko.core.ui.middleShapes
import io.github.sadellie.sukko.feature.editor.selector.BooleanSelectorSheet
import io.github.sadellie.sukko.feature.editor.selector.DoubleSelectorSheet
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.common_fill
import io.github.sadellie.sukko.resources.editor_modifiers_weight
import org.jetbrains.compose.resources.stringResource
import sh.calvin.reorderable.ReorderableCollectionItemScope

@Composable
internal fun ReorderableCollectionItemScope.EditorModifierColumnWeight(
  modifier: Modifier,
  widgetModifier: ColdColumnWeightModifier,
  onUpdateModifier: (ColdColumnWeightModifier) -> Unit,
  state: EditorModifierListItemState,
) {
  val scriptableDisplay = LocalScriptableDisplay.current
  EditorModifierExpandableListItem(
    modifier = modifier,
    headlineText = stringResource(widgetModifier.displayName),
    supportingText = scriptableDisplay.displayString(widgetModifier.weight),
    state = state,
  ) {
    val weightSheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
    ListItem2Compact(
      content = { Text(stringResource(Res.string.editor_modifiers_weight)) },
      supportingContent = { Text(scriptableDisplay.displayString(widgetModifier.weight)) },
      compactListMode = state.compactListMode,
      onClick = weightSheetState::expand,
      shapes = ListItemDefaults.middleShapes,
    )
    DoubleSelectorSheet(
      state = weightSheetState,
      onValueSelected = { onUpdateModifier(widgetModifier.copy(weight = it)) },
      value = widgetModifier.weight,
      range = ColdColumnWeightModifier.weightRange,
      allowFraction = true,
      globals = state.globals.doubles,
    )

    val fillSheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
    ListItem2Compact(
      content = { Text(stringResource(Res.string.common_fill)) },
      supportingContent = { Text(scriptableDisplay.displayString(widgetModifier.fill)) },
      compactListMode = state.compactListMode,
      onClick = fillSheetState::expand,
      shapes = ListItemDefaults.middleShapes,
    )
    BooleanSelectorSheet(
      state = fillSheetState,
      onValueSelected = { onUpdateModifier(widgetModifier.copy(fill = it)) },
      value = widgetModifier.fill,
      globals = state.globals.booleans,
    )
  }
}
