package io.github.sadellie.sukko.feature.editor.modifiers

import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import com.composables.core.SheetDetent
import com.composables.core.rememberModalBottomSheetState
import io.github.sadellie.sukko.core.model.modifier.ColdBackgroundColorModifier
import io.github.sadellie.sukko.core.ui.ListItem2Compact
import io.github.sadellie.sukko.core.ui.expand
import io.github.sadellie.sukko.feature.editor.selector.BrushSourceSelectorSheet
import io.github.sadellie.sukko.feature.editor.selector.ShapeSelectorSheet
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.editor_modifiers_color
import io.github.sadellie.sukko.resources.editor_modifiers_shape
import org.jetbrains.compose.resources.stringResource
import sh.calvin.reorderable.ReorderableCollectionItemScope

@Composable
internal fun ReorderableCollectionItemScope.EditorModifierBackground(
  modifier: Modifier,
  widgetModifier: ColdBackgroundColorModifier,
  onUpdateModifier: (ColdBackgroundColorModifier) -> Unit,
  state: EditorModifierListItemState,
) {
  EditorModifierExpandableListItem(
    modifier = modifier,
    headlineText = stringResource(widgetModifier.displayName),
    supportingText = widgetModifier.color.displayValue(),
    state = state,
  ) {
    val brushSheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
    ListItem2Compact(
      headlineContent = { Text(stringResource(Res.string.editor_modifiers_color)) },
      supportingContent = { Text(widgetModifier.color.displayValue()) },
      compactListMode = state.compactListMode,
      modifier = Modifier.clickable { brushSheetState.expand() },
      shape = RectangleShape,
    )

    BrushSourceSelectorSheet(
      state = brushSheetState,
      onValueSelected = { onUpdateModifier(widgetModifier.copy(color = it)) },
      value = widgetModifier.color,
      globals = state.globals,
    )

    val shapeSheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
    ListItem2Compact(
      headlineContent = { Text(stringResource(Res.string.editor_modifiers_shape)) },
      supportingContent = { Text(stringResource(widgetModifier.shapeSource.displayName)) },
      compactListMode = state.compactListMode,
      modifier = Modifier.clickable { shapeSheetState.expand() },
      shape = RectangleShape,
    )
    ShapeSelectorSheet(
      state = shapeSheetState,
      onValueSelected = { onUpdateModifier(widgetModifier.copy(shapeSource = it)) },
      value = widgetModifier.shapeSource,
    )
  }
}
