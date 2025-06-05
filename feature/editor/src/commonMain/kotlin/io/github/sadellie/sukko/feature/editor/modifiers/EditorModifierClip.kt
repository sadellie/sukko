package io.github.sadellie.sukko.feature.editor.modifiers

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.composables.core.SheetDetent
import com.composables.core.rememberModalBottomSheetState
import io.github.sadellie.sukko.core.model.modifier.ColdClipModifier
import io.github.sadellie.sukko.core.ui.expand
import io.github.sadellie.sukko.feature.editor.selector.ShapeSelectorSheet
import org.jetbrains.compose.resources.stringResource
import sh.calvin.reorderable.ReorderableCollectionItemScope

@Composable
internal fun ReorderableCollectionItemScope.EditorModifierClip(
  modifier: Modifier,
  widgetModifier: ColdClipModifier,
  onUpdateModifier: (ColdClipModifier) -> Unit,
  state: EditorModifierListItemState,
) {
  val sheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
  EditorModifierFlatListItem(
    modifier = modifier.clickable { sheetState.expand() },
    headlineText = stringResource(widgetModifier.displayName),
    supportingText = stringResource(widgetModifier.shapeSource.displayName),
    state = state,
  )

  ShapeSelectorSheet(
    state = sheetState,
    onValueSelected = { onUpdateModifier(widgetModifier.copy(shapeSource = it)) },
    value = widgetModifier.shapeSource,
  )
}
