package io.github.sadellie.sukko.feature.editor.modifiers

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.composables.core.SheetDetent
import com.composables.core.rememberModalBottomSheetState
import io.github.sadellie.sukko.core.model.basic.AlignmentSource
import io.github.sadellie.sukko.core.model.modifier.ColdRowAlignmentModifier
import io.github.sadellie.sukko.core.ui.ModalBottomSheetWithItems
import io.github.sadellie.sukko.core.ui.expand
import org.jetbrains.compose.resources.stringResource
import sh.calvin.reorderable.ReorderableCollectionItemScope

@Composable
internal fun ReorderableCollectionItemScope.EditorModifierRowAlignment(
  modifier: Modifier,
  widgetModifier: ColdRowAlignmentModifier,
  onUpdateModifier: (ColdRowAlignmentModifier) -> Unit,
  state: EditorModifierListItemState,
) {
  val sheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
  EditorModifierFlatListItem(
    modifier = modifier.clickable { sheetState.expand() },
    headlineText = stringResource(widgetModifier.displayName),
    supportingText = stringResource(widgetModifier.alignmentSource.displayName),
    state = state,
  )

  ModalBottomSheetWithItems(
    state = sheetState,
    items = AlignmentSource.allVertical,
    headlineText = { stringResource(it.displayName) },
    onClick = { onUpdateModifier(widgetModifier.copy(alignmentSource = it)) },
  )
}
