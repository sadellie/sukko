package io.github.sadellie.sukko.feature.editor.modifiers

import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.composables.core.SheetDetent
import com.composables.core.rememberModalBottomSheetState
import io.github.sadellie.sukko.core.model.basic.LocalScriptableDisplay
import io.github.sadellie.sukko.core.model.modifier.ColdBorderModifier
import io.github.sadellie.sukko.core.ui.ListItem2Compact
import io.github.sadellie.sukko.core.ui.expand
import io.github.sadellie.sukko.core.ui.middleShapes
import io.github.sadellie.sukko.feature.editor.selector.brushsource.BrushSourceSelectorSheet
import io.github.sadellie.sukko.feature.editor.selector.DoubleSelectorSheet
import io.github.sadellie.sukko.feature.editor.selector.ShapeSelectorSheet
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.common_shape
import io.github.sadellie.sukko.resources.editor_modifiers_width
import org.jetbrains.compose.resources.stringResource
import sh.calvin.reorderable.ReorderableCollectionItemScope

@Composable
internal fun ReorderableCollectionItemScope.EditorModifierBorder(
  modifier: Modifier,
  widgetModifier: ColdBorderModifier,
  onUpdateModifier: (ColdBorderModifier) -> Unit,
  state: EditorModifierListItemState,
) {
  EditorModifierExpandableListItem(
    modifier = modifier,
    headlineText = stringResource(widgetModifier.displayName),
    supportingText = widgetModifier.color.displayValue(),
    state = state,
  ) {
    val widthSheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
    ListItem2Compact(
      content = { Text(stringResource(Res.string.editor_modifiers_width)) },
      supportingContent = {
        Text(LocalScriptableDisplay.current.displayString(widgetModifier.width))
      },
      compactListMode = state.compactListMode,
      onClick = widthSheetState::expand,
      shapes = ListItemDefaults.middleShapes,
    )
    DoubleSelectorSheet(
      state = widthSheetState,
      onValueSelected = { onUpdateModifier(widgetModifier.copy(width = it)) },
      value = widgetModifier.width,
      globals = state.globals,
      allowFraction = true,
    )

    val sheetColorState = rememberModalBottomSheetState(SheetDetent.Hidden)
    ListItem2Compact(
      content = { Text(stringResource(Res.string.common_shape)) },
      supportingContent = { Text(widgetModifier.color.displayValue()) },
      compactListMode = state.compactListMode,
      onClick = sheetColorState::expand,
      shapes = ListItemDefaults.middleShapes,
    )
    BrushSourceSelectorSheet(
      state = sheetColorState,
      onValueSelected = { onUpdateModifier(widgetModifier.copy(color = it)) },
      value = widgetModifier.color,
      globals = state.globals,
    )

    val shapeSheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
    ListItem2Compact(
      content = { Text(stringResource(Res.string.common_shape)) },
      supportingContent = { Text(stringResource(widgetModifier.shapeSource.displayName)) },
      compactListMode = state.compactListMode,
      onClick = shapeSheetState::expand,
      shapes = ListItemDefaults.middleShapes,
    )
    ShapeSelectorSheet(
      state = shapeSheetState,
      onValueSelected = { onUpdateModifier(widgetModifier.copy(shapeSource = it)) },
      value = widgetModifier.shapeSource,
    )
  }
}
