package io.github.sadellie.sukko.feature.editor.modifiers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.composables.core.SheetDetent
import com.composables.core.rememberModalBottomSheetState
import io.github.sadellie.sukko.core.designsystem.Preview2
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.layer.ColdTextLayer
import io.github.sadellie.sukko.core.model.layer.Layer
import io.github.sadellie.sukko.core.model.modifier.ColdAlphaModifier
import io.github.sadellie.sukko.core.model.modifier.ColdAspectRatioModifier
import io.github.sadellie.sukko.core.model.modifier.ColdBackgroundColorModifier
import io.github.sadellie.sukko.core.model.modifier.ColdBorderModifier
import io.github.sadellie.sukko.core.model.modifier.ColdBoxAlignmentModifier
import io.github.sadellie.sukko.core.model.modifier.ColdClipModifier
import io.github.sadellie.sukko.core.model.modifier.ColdColumnAlignmentModifier
import io.github.sadellie.sukko.core.model.modifier.ColdColumnWeightModifier
import io.github.sadellie.sukko.core.model.modifier.ColdFillMaxHeightModifier
import io.github.sadellie.sukko.core.model.modifier.ColdFillMaxSizeModifier
import io.github.sadellie.sukko.core.model.modifier.ColdFillMaxWidthModifier
import io.github.sadellie.sukko.core.model.modifier.ColdHeightModifier
import io.github.sadellie.sukko.core.model.modifier.ColdOffsetModifier
import io.github.sadellie.sukko.core.model.modifier.ColdPaddingAllSidesModifier
import io.github.sadellie.sukko.core.model.modifier.ColdPaddingAxisModifier
import io.github.sadellie.sukko.core.model.modifier.ColdPaddingEachSideModifier
import io.github.sadellie.sukko.core.model.modifier.ColdRowAlignmentModifier
import io.github.sadellie.sukko.core.model.modifier.ColdRowWeightModifier
import io.github.sadellie.sukko.core.model.modifier.ColdSizeModifier
import io.github.sadellie.sukko.core.model.modifier.ColdWidthModifier
import io.github.sadellie.sukko.core.model.modifier.WidgetModifier
import io.github.sadellie.sukko.core.ui.ModalBottomSheetWithItems
import io.github.sadellie.sukko.core.ui.expand
import io.github.sadellie.sukko.core.ui.listedShapes
import io.github.sadellie.sukko.feature.editor.EditorEvent
import io.github.sadellie.sukko.feature.editor.EditorList
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.editor_modifiers_add_modifier
import org.jetbrains.compose.resources.stringResource
import sh.calvin.reorderable.ReorderableCollectionItemScope

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun EditorModifiersList(
  modifier: Modifier,
  onEvent: (EditorEvent.WidgetModifierAction) -> Unit,
  layer: Layer.Cold,
  parentLayer: Layer.Cold?,
  compactListMode: Boolean,
  contentPadding: PaddingValues,
  globals: Globals,
) {
  val sheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
  var expandedItem by rememberSaveable { mutableStateOf<Int?>(null) }
  EditorList(
    modifier = modifier,
    onAddItemClick = sheetState::expand,
    addButtonLabel = stringResource(Res.string.editor_modifiers_add_modifier),
    items = layer.widgetModifiers,
    contentPadding = contentPadding,
    key = { it.id },
  ) { widgetModifier ->
    val itemState =
      EditorModifierListItemState(
        isExpanded = expandedItem == widgetModifier.id,
        isInEditingMode = isInEditingMode,
        compactListMode = compactListMode,
        shapes = ListItemDefaults.listedShapes(index, layer.widgetModifiers.size),
        onExpandedUpdate = {
          expandedItem = if (expandedItem == widgetModifier.id) null else widgetModifier.id
        },
        onRemoveClick = {
          onEvent(EditorEvent.WidgetModifierAction.Delete(widgetModifier, layer.id))
        },
        onDragStopped = {
          onEvent(EditorEvent.WidgetModifierAction.Reorder(reorderedList(), layer.id))
        },
        globals = globals,
      )
    reorderableCollectionItemScope.EditorModifierListItem(
      modifier = Modifier,
      widgetModifier = widgetModifier,
      onUpdateModifier = { onEvent(EditorEvent.WidgetModifierAction.Update(it, layer.id)) },
      state = itemState,
    )
  }

  ModalBottomSheetWithItems(
    state = sheetState,
    items =
      remember(key1 = parentLayer) {
        WidgetModifier.Cold.getAllWidgetModifiers(parentLayer = parentLayer)
      },
    headlineText = { stringResource(it.displayName) },
    onClick = { onEvent(EditorEvent.WidgetModifierAction.Add(it, layer.id)) },
  )
}

@Suppress("CyclomaticComplexMethod", "LongMethod")
@Composable
private fun ReorderableCollectionItemScope.EditorModifierListItem(
  modifier: Modifier,
  widgetModifier: WidgetModifier.Cold,
  onUpdateModifier: (updatedModifier: WidgetModifier.Cold) -> Unit,
  state: EditorModifierListItemState,
) {
  when (widgetModifier) {
    is ColdSizeModifier ->
      EditorModifierSize(
        modifier = modifier,
        widgetModifier = widgetModifier,
        onUpdateModifier = onUpdateModifier,
        state = state,
      )
    is ColdFillMaxSizeModifier ->
      EditorModifierFillMaxSize(
        modifier = modifier,
        widgetModifier = widgetModifier,
        onUpdateModifier = onUpdateModifier,
        state = state,
      )
    is ColdWidthModifier ->
      EditorModifierWidth(
        modifier = modifier,
        widgetModifier = widgetModifier,
        onUpdateModifier = onUpdateModifier,
        state = state,
      )
    is ColdFillMaxWidthModifier ->
      EditorModifierFillMaxWidth(
        modifier = modifier,
        widgetModifier = widgetModifier,
        onUpdateModifier = onUpdateModifier,
        state = state,
      )
    is ColdHeightModifier ->
      EditorModifierHeight(
        modifier = modifier,
        widgetModifier = widgetModifier,
        onUpdateModifier = onUpdateModifier,
        state = state,
      )
    is ColdFillMaxHeightModifier ->
      EditorModifierFillMaxHeight(
        modifier = modifier,
        widgetModifier = widgetModifier,
        onUpdateModifier = onUpdateModifier,
        state = state,
      )
    is ColdBackgroundColorModifier ->
      EditorModifierBackground(
        modifier = modifier,
        widgetModifier = widgetModifier,
        onUpdateModifier = onUpdateModifier,
        state = state,
      )
    is ColdBorderModifier ->
      EditorModifierBorder(
        modifier = modifier,
        widgetModifier = widgetModifier,
        onUpdateModifier = onUpdateModifier,
        state = state,
      )
    is ColdBoxAlignmentModifier ->
      EditorModifierBoxAlignment(
        modifier = modifier,
        widgetModifier = widgetModifier,
        onUpdateModifier = onUpdateModifier,
        state = state,
      )
    is ColdRowAlignmentModifier ->
      EditorModifierRowAlignment(
        modifier = modifier,
        widgetModifier = widgetModifier,
        onUpdateModifier = onUpdateModifier,
        state = state,
      )
    is ColdColumnAlignmentModifier ->
      EditorModifierColumnAlignment(
        modifier = modifier,
        widgetModifier = widgetModifier,
        onUpdateModifier = onUpdateModifier,
        state = state,
      )
    is ColdRowWeightModifier ->
      EditorModifierRowWeight(
        modifier = modifier,
        widgetModifier = widgetModifier,
        onUpdateModifier = onUpdateModifier,
        state = state,
      )
    is ColdColumnWeightModifier ->
      EditorModifierColumnWeight(
        modifier = modifier,
        widgetModifier = widgetModifier,
        onUpdateModifier = onUpdateModifier,
        state = state,
      )
    is ColdPaddingAllSidesModifier ->
      EditorModifierPaddingAllSides(
        modifier = modifier,
        widgetModifier = widgetModifier,
        onUpdateModifier = onUpdateModifier,
        state = state,
      )
    is ColdPaddingAxisModifier ->
      EditorModifierPaddingAxis(
        modifier = modifier,
        widgetModifier = widgetModifier,
        onUpdateModifier = onUpdateModifier,
        state = state,
      )
    is ColdPaddingEachSideModifier ->
      EditorModifierPaddingEachSide(
        modifier = modifier,
        widgetModifier = widgetModifier,
        onUpdateModifier = onUpdateModifier,
        state = state,
      )
    is ColdClipModifier ->
      EditorModifierClip(
        modifier = modifier,
        widgetModifier = widgetModifier,
        onUpdateModifier = onUpdateModifier,
        state = state,
      )
    is ColdOffsetModifier ->
      EditorModifierOffset(
        modifier = modifier,
        widgetModifier = widgetModifier,
        onUpdateModifier = onUpdateModifier,
        state = state,
      )
    is ColdAlphaModifier ->
      EditorModifierAlpha(
        modifier = modifier,
        widgetModifier = widgetModifier,
        onUpdateModifier = onUpdateModifier,
        state = state,
      )
    is ColdAspectRatioModifier ->
      EditorModifierAspectRatio(
        modifier = modifier,
        widgetModifier = widgetModifier,
        onUpdateModifier = onUpdateModifier,
        state = state,
      )
  }
}

@Preview
@Composable
private fun PreviewEditorModifiersList() = Preview2 {
  val layer = remember {
    ColdTextLayer(
      id = 0,
      parentId = null,
      widgetModifiers =
        listOf(
            ColdSizeModifier(id = 0),
            ColdFillMaxSizeModifier(id = 0),
            ColdWidthModifier(id = 0),
            ColdFillMaxWidthModifier(id = 0),
            ColdHeightModifier(id = 0),
            ColdFillMaxHeightModifier(id = 0),
            ColdBackgroundColorModifier(id = 0),
            ColdClipModifier(id = 0),
            ColdPaddingAllSidesModifier(id = 0),
            ColdPaddingAxisModifier(id = 0),
            ColdPaddingEachSideModifier(id = 0),
            ColdBoxAlignmentModifier(id = 0),
            ColdColumnAlignmentModifier(id = 0),
            ColdRowAlignmentModifier(id = 0),
            ColdColumnWeightModifier(id = 0),
            ColdRowWeightModifier(id = 0),
            ColdOffsetModifier(id = 0),
            ColdBorderModifier(id = 0),
            ColdAlphaModifier(id = 0),
            ColdAspectRatioModifier(id = 0),
          )
          .mapIndexed { index, widgetModifier -> widgetModifier.updateId(index) },
    )
  }

  EditorModifiersList(
    modifier = Modifier.background(MaterialTheme.colorScheme.surface).fillMaxSize(),
    layer = layer,
    parentLayer = null,
    onEvent = {},
    compactListMode = false,
    contentPadding = PaddingValues(0.dp),
    globals = Globals(),
  )
}
