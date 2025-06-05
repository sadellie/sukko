package io.github.sadellie.sukko.feature.editor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.composables.core.SheetDetent
import com.composables.core.rememberModalBottomSheetState
import google.material.design.symbols.Add
import google.material.design.symbols.Symbols
import io.github.sadellie.sukko.core.designsystem.Preview2
import io.github.sadellie.sukko.core.designsystem.theme.ListArrangement
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.basic.GlobalValue
import io.github.sadellie.sukko.core.ui.AlertDialogWithText
import io.github.sadellie.sukko.core.ui.AlertDialogWithTextField
import io.github.sadellie.sukko.core.ui.DropDownMenuWithButton
import io.github.sadellie.sukko.core.ui.ListHeader
import io.github.sadellie.sukko.core.ui.ListItem2
import io.github.sadellie.sukko.core.ui.ModalBottomSheetWithItems
import io.github.sadellie.sukko.core.ui.expand
import io.github.sadellie.sukko.core.ui.listedShape
import io.github.sadellie.sukko.feature.editor.selector.BooleanSelectorSheet
import io.github.sadellie.sukko.feature.editor.selector.ColorSelectorSheet
import io.github.sadellie.sukko.feature.editor.selector.DoubleSelectorSheet
import io.github.sadellie.sukko.feature.editor.selector.DpSelectorSheet
import io.github.sadellie.sukko.feature.editor.selector.SpSelectorSheet
import io.github.sadellie.sukko.feature.editor.selector.StringSelectorSheet
import io.github.sadellie.sukko.feature.editor.selector.TextStyleSelectorSheet
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.common_delete
import io.github.sadellie.sukko.resources.common_rename
import io.github.sadellie.sukko.resources.editor_globals_list_add_global
import io.github.sadellie.sukko.resources.editor_globals_list_boolean
import io.github.sadellie.sukko.resources.editor_globals_list_color
import io.github.sadellie.sukko.resources.editor_globals_list_delete_global
import io.github.sadellie.sukko.resources.editor_globals_list_delete_global_text
import io.github.sadellie.sukko.resources.editor_globals_list_dp
import io.github.sadellie.sukko.resources.editor_globals_list_label
import io.github.sadellie.sukko.resources.editor_globals_list_number
import io.github.sadellie.sukko.resources.editor_globals_list_rename_global
import io.github.sadellie.sukko.resources.editor_globals_list_sp
import io.github.sadellie.sukko.resources.editor_globals_list_string
import io.github.sadellie.sukko.resources.editor_globals_list_text_style
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider

@Composable
internal fun EditorGlobalsList(
  modifier: Modifier,
  contentPadding: PaddingValues,
  onEvent: (event: EditorEvent.GlobalAction) -> Unit,
  globals: Globals,
) {
  Column(modifier = modifier) {
    AddGlobalButton(
      modifier = Modifier.fillMaxWidth(),
      onAddGlobal = { onEvent(EditorEvent.GlobalAction.Add(it)) },
    )

    if (globals.isEmpty()) return@Column
    LazyColumn(
      modifier = Modifier.fillMaxWidth(),
      contentPadding = contentPadding,
      verticalArrangement = ListArrangement,
    ) {
      globalItems(
        items = globals.colors,
        keyPrefix = "colors",
        header = Res.string.editor_globals_list_color,
        onEvent = onEvent,
        globals = globals,
      )
      globalItems(
        items = globals.strings,
        keyPrefix = "strings",
        header = Res.string.editor_globals_list_string,
        onEvent = onEvent,
        globals = globals,
      )
      globalItems(
        items = globals.booleans,
        keyPrefix = "booleans",
        header = Res.string.editor_globals_list_boolean,
        onEvent = onEvent,
        globals = globals,
      )
      globalItems(
        items = globals.doubles,
        keyPrefix = "doubles",
        header = Res.string.editor_globals_list_number,
        onEvent = onEvent,
        globals = globals,
      )
      globalItems(
        items = globals.dps,
        keyPrefix = "dps",
        header = Res.string.editor_globals_list_dp,
        onEvent = onEvent,
        globals = globals,
      )
      globalItems(
        items = globals.sps,
        keyPrefix = "sps",
        header = Res.string.editor_globals_list_sp,
        onEvent = onEvent,
        globals = globals,
      )
      globalItems(
        items = globals.textStyles,
        keyPrefix = "textStyles",
        header = Res.string.editor_globals_list_text_style,
        onEvent = onEvent,
        globals = globals,
      )
    }
  }
}

@Composable
private fun AddGlobalButton(modifier: Modifier, onAddGlobal: (newGlobal: GlobalValue<*>) -> Unit) {
  val sheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
  var selectedScriptableType by remember { mutableStateOf<GlobalType?>(null) }
  val addGlobalLabel = stringResource(Res.string.editor_globals_list_add_global)
  FilledTonalButton(
    modifier = modifier.heightIn(ButtonDefaults.MinHeight),
    shapes = ButtonDefaults.shapes(),
    contentPadding = ButtonDefaults.SmallContentPadding,
    onClick = { sheetState.expand() },
  ) {
    Icon(
      imageVector = Symbols.Add,
      contentDescription = addGlobalLabel,
      modifier = Modifier.size(ButtonDefaults.SmallIconSize),
    )
    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
    Text(text = addGlobalLabel, minLines = 1, maxLines = 1)
  }

  ModalBottomSheetWithItems(
    state = sheetState,
    items = remember { GlobalType.entries },
    headlineText = { stringResource(it.displayName) },
    onClick = { selectedScriptableType = it },
  )

  selectedScriptableType?.let { globalType ->
    val textFieldState = rememberTextFieldState()
    AlertDialogWithTextField(
      textFieldLabel = stringResource(Res.string.editor_globals_list_label),
      onDismiss = { selectedScriptableType = null },
      onConfirm = onConfirm@{
          val label = textFieldState.text.toString()
          if (label.isBlank()) return@onConfirm
          val globalToAdd =
            when (globalType) {
              GlobalType.COLOR -> GlobalValue.GlobalColor(label = label)
              GlobalType.STRING -> GlobalValue.GlobalString(label = label)
              GlobalType.BOOLEAN -> GlobalValue.GlobalBoolean(label = label)
              GlobalType.NUMBER -> GlobalValue.GlobalDouble(label = label)
              GlobalType.DP -> GlobalValue.GlobalDp(label = label)
              GlobalType.SP -> GlobalValue.GlobalSp(label = label)
              GlobalType.TEXT_STYLE -> GlobalValue.GlobalTextStyle(label = label)
            }
          onAddGlobal(globalToAdd)
        },
      textFieldState = textFieldState,
      title = addGlobalLabel,
    )
  }
}

private fun LazyListScope.globalItems(
  items: List<GlobalValue<*>>,
  keyPrefix: String,
  header: StringResource,
  onEvent: (event: EditorEvent.GlobalAction) -> Unit,
  globals: Globals,
) {
  if (items.isEmpty()) return
  item("header.$keyPrefix") { ListHeader(stringResource(header)) }
  itemsIndexed(items = items, key = { _, item -> "$keyPrefix.${item.id}" }) { index, item ->
    GlobalListItem(
      modifier = Modifier.fillMaxWidth(),
      global = item,
      onDelete = { onEvent(EditorEvent.GlobalAction.Delete(item)) },
      onUpdate = { onEvent(EditorEvent.GlobalAction.Update(it)) },
      globals = globals,
      shape = ListItemDefaults.listedShape(index, items.size),
    )
  }
}

@Composable
private fun <T> GlobalListItem(
  modifier: Modifier,
  onUpdate: (newGlobal: GlobalValue<*>) -> Unit,
  onDelete: () -> Unit,
  global: GlobalValue<T>,
  globals: Globals,
  shape: Shape,
) {
  val sheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
  ListItem2(
    modifier = modifier.clickable { sheetState.expand() },
    headlineContent = { Text(global.label) },
    trailingContent = {
      GlobalListItemMenu(
        globalLabel = global.label,
        onRename = { newName -> onUpdate(global.updateLabel(newName)) },
        onDelete = onDelete,
      )
    },
    shape = shape,
  )

  when (global) {
    is GlobalValue.GlobalColor ->
      ColorSelectorSheet(
        state = sheetState,
        onValueSelected = { newValue -> onUpdate(global.updateValue(newValue)) },
        value = global.value,
        globals = remember(globals.colors, global) { globals.colors - global },
      )
    is GlobalValue.GlobalString ->
      StringSelectorSheet(
        state = sheetState,
        onValueSelected = { newValue -> onUpdate(global.updateValue(newValue)) },
        value = global.value,
        globals = remember(globals.strings, global) { globals.strings - global },
      )
    is GlobalValue.GlobalBoolean ->
      BooleanSelectorSheet(
        state = sheetState,
        onValueSelected = { newValue -> onUpdate(global.updateValue(newValue)) },
        value = global.value,
        globals = remember(globals.booleans, global) { globals.booleans - global },
      )
    is GlobalValue.GlobalDouble ->
      DoubleSelectorSheet(
        state = sheetState,
        onValueSelected = { newValue -> onUpdate(global.updateValue(newValue)) },
        value = global.value,
        allowFraction = true,
        globals = remember(globals.doubles, global) { globals.doubles - global },
      )
    is GlobalValue.GlobalDp ->
      DpSelectorSheet(
        state = sheetState,
        onValueSelected = { newValue -> onUpdate(global.updateValue(newValue)) },
        value = global.value,
        globals = remember(globals.dps, global) { globals.dps - global },
      )
    is GlobalValue.GlobalSp ->
      SpSelectorSheet(
        state = sheetState,
        onValueSelected = { newValue -> onUpdate(global.updateValue(newValue)) },
        value = global.value,
        globals = remember(globals.sps, global) { globals.sps - global },
      )
    is GlobalValue.GlobalTextStyle ->
      TextStyleSelectorSheet(
        state = sheetState,
        onValueSelected = { newValue -> onUpdate(global.updateValue(newValue)) },
        value = global.value,
        globals = globals,
      )
  }
}

@Composable
private fun GlobalListItemMenu(
  globalLabel: String,
  onRename: (newName: String) -> Unit,
  onDelete: () -> Unit,
) {
  DropDownMenuWithButton {
    var showRenameDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    DropdownMenuItem(
      text = { Text(stringResource(Res.string.common_rename)) },
      onClick = { showRenameDialog = true },
    )
    if (showRenameDialog) {
      val textFieldState = rememberTextFieldState(globalLabel)
      AlertDialogWithTextField(
        onDismiss = { showRenameDialog = false },
        onConfirm = {
          val newName = textFieldState.text.toString()
          if (newName.isNotBlank()) onRename(newName)
        },
        textFieldLabel = stringResource(Res.string.editor_globals_list_label),
        textFieldState = textFieldState,
        title = stringResource(Res.string.editor_globals_list_rename_global),
      )
    }

    DropdownMenuItem(
      text = { Text(stringResource(Res.string.common_delete)) },
      onClick = { showDeleteDialog = true },
    )
    if (showDeleteDialog) {
      AlertDialogWithText(
        onDismiss = { showRenameDialog = false },
        onConfirm = onDelete,
        title = stringResource(Res.string.editor_globals_list_delete_global),
        text = stringResource(Res.string.editor_globals_list_delete_global_text),
      )
    }
  }
}

private enum class GlobalType(val displayName: StringResource) {
  COLOR(Res.string.editor_globals_list_color),
  STRING(Res.string.editor_globals_list_string),
  BOOLEAN(Res.string.editor_globals_list_boolean),
  NUMBER(Res.string.editor_globals_list_number),
  DP(Res.string.editor_globals_list_dp),
  SP(Res.string.editor_globals_list_sp),
  TEXT_STYLE(Res.string.editor_globals_list_text_style),
}

@Composable
@Preview
private fun PreviewEditorGlobalsList(
  @PreviewParameter(GlobalsListPreviewProvider::class) initialGlobals: Globals
) = Preview2 {
  var globals by remember { mutableStateOf(initialGlobals) }
  EditorGlobalsList(
    modifier = Modifier,
    contentPadding = PaddingValues(0.dp),
    onEvent = {
      globals =
        when (it) {
          is EditorEvent.GlobalAction.Add -> globals.addGlobal(it.globalToAdd)
          is EditorEvent.GlobalAction.Delete -> globals.deleteGlobal(it.globalToDelete)
          is EditorEvent.GlobalAction.Update -> globals.updateGlobal(it.globalToUpdate)
        }
    },
    globals = globals,
  )
}

private class GlobalsListPreviewProvider : PreviewParameterProvider<Globals> {
  override val values =
    sequenceOf(
      Globals(strings = emptyList(), textStyles = emptyList()),
      Globals(
        strings = List(4) { GlobalValue.GlobalString(id = it.toLong(), label = "Item $it") },
        textStyles = emptyList(),
      ),
      Globals(
        strings = emptyList(),
        textStyles = List(4) { GlobalValue.GlobalTextStyle(id = it.toLong(), label = "Item $it") },
      ),
      Globals(
        strings = List(4) { GlobalValue.GlobalString(id = it.toLong(), label = "Item $it") },
        textStyles = List(4) { GlobalValue.GlobalTextStyle(id = it.toLong(), label = "Item $it") },
        booleans = List(4) { GlobalValue.GlobalBoolean(id = it.toLong(), label = "Item $it") },
        doubles = List(4) { GlobalValue.GlobalDouble(id = it.toLong(), label = "Item $it") },
        dps = List(4) { GlobalValue.GlobalDp(id = it.toLong(), label = "Item $it") },
        sps = List(4) { GlobalValue.GlobalSp(id = it.toLong(), label = "Item $it") },
        colors = List(4) { GlobalValue.GlobalColor(id = it.toLong(), label = "Item $it") },
      ),
    )
}
