package io.github.sadellie.sukko.feature.editor.modifiers

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.composables.core.SheetDetent
import com.composables.core.rememberModalBottomSheetState
import io.github.sadellie.sukko.core.model.basic.LocalScriptableDisplay
import io.github.sadellie.sukko.core.model.modifier.ColdFillMaxHeightModifier
import io.github.sadellie.sukko.core.ui.expand
import io.github.sadellie.sukko.feature.editor.selector.DoubleSelectorSheet
import org.jetbrains.compose.resources.stringResource
import sh.calvin.reorderable.ReorderableCollectionItemScope

@Composable
internal fun ReorderableCollectionItemScope.EditorModifierFillMaxHeight(
  modifier: Modifier,
  widgetModifier: ColdFillMaxHeightModifier,
  onUpdateModifier: (ColdFillMaxHeightModifier) -> Unit,
  state: EditorModifierListItemState,
) {
  val sheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
  EditorModifierFlatListItem(
    modifier = modifier,
    onClick = sheetState::expand,
    headlineText = stringResource(widgetModifier.displayName),
    supportingText = LocalScriptableDisplay.current.displayString(widgetModifier.fraction),
    state = state,
  )

  DoubleSelectorSheet(
    state = sheetState,
    onValueSelected = { onUpdateModifier(widgetModifier.copy(fraction = it)) },
    value = widgetModifier.fraction,
    range = ColdFillMaxHeightModifier.fractionRange,
    allowFraction = true,
    globals = state.globals,
  )
}
