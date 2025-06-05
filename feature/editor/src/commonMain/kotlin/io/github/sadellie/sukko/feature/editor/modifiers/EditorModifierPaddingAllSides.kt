package io.github.sadellie.sukko.feature.editor.modifiers

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.composables.core.SheetDetent
import com.composables.core.rememberModalBottomSheetState
import io.github.sadellie.sukko.core.model.basic.LocalScriptableDisplay
import io.github.sadellie.sukko.core.model.modifier.ColdPaddingAllSidesModifier
import io.github.sadellie.sukko.core.ui.expand
import io.github.sadellie.sukko.feature.editor.selector.DpSelectorSheet
import org.jetbrains.compose.resources.stringResource
import sh.calvin.reorderable.ReorderableCollectionItemScope

@Composable
internal fun ReorderableCollectionItemScope.EditorModifierPaddingAllSides(
  modifier: Modifier,
  widgetModifier: ColdPaddingAllSidesModifier,
  onUpdateModifier: (ColdPaddingAllSidesModifier) -> Unit,
  state: EditorModifierListItemState,
) {
  val sheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
  EditorModifierFlatListItem(
    modifier = modifier.clickable { sheetState.expand() },
    headlineText = stringResource(widgetModifier.displayName),
    supportingText = LocalScriptableDisplay.current.displayString(widgetModifier.all),
    state = state,
  )

  DpSelectorSheet(
    state = sheetState,
    onValueSelected = { onUpdateModifier(widgetModifier.copy(all = it)) },
    value = widgetModifier.all,
    range = ColdPaddingAllSidesModifier.valueRange,
    globals = state.globals.dps,
  )
}
