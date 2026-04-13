package io.github.sadellie.sukko.feature.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.DropdownMenuGroup
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.tooling.preview.Preview
import google.material.design.symbols.Check
import google.material.design.symbols.Edit
import google.material.design.symbols.Info
import google.material.design.symbols.LibraryAdd
import google.material.design.symbols.Refresh
import google.material.design.symbols.Save
import google.material.design.symbols.SaveAs
import google.material.design.symbols.Symbols
import io.github.sadellie.sukko.core.designsystem.LocalWindowSize
import io.github.sadellie.sukko.core.ui.AlertDialogWithText
import io.github.sadellie.sukko.core.ui.AlertDialogWithTextField
import io.github.sadellie.sukko.core.ui.DropDownMenuWithFilledTonalButton
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.common_rename
import io.github.sadellie.sukko.resources.common_save
import io.github.sadellie.sukko.resources.common_widget_info
import io.github.sadellie.sukko.resources.common_widget_name
import io.github.sadellie.sukko.resources.editor_3d_view
import io.github.sadellie.sukko.resources.editor_compact_list
import io.github.sadellie.sukko.resources.editor_force_update
import io.github.sadellie.sukko.resources.editor_full_screen_list
import io.github.sadellie.sukko.resources.editor_highlight_selected_layer
import io.github.sadellie.sukko.resources.editor_load_from_preset
import io.github.sadellie.sukko.resources.editor_rename_widget
import io.github.sadellie.sukko.resources.editor_save_as_preset
import io.github.sadellie.sukko.resources.editor_save_as_preset_require_save
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun EditorScreenDropDownMenu(
  onRename: (newWidgetName: String) -> Unit,
  onForceUpdate: () -> Unit,
  onNavigateToSaveAsPreset: () -> Unit,
  onNavigateToPresetSelector: () -> Unit,
  onNavigateToWidgetInfo: () -> Unit,
  isWidgetDataSaved: Boolean,
  widgetDataSaverState: WidgetDataSaverState,
  isInFullscreen: Boolean,
  onFullScreenClick: (Boolean) -> Unit,
  compactListMode: Boolean,
  onCompactListModeClick: (Boolean) -> Unit,
  highlightSelectedLayer: Boolean,
  onHighlightSelectedLayerClick: (Boolean) -> Unit,
  explodeLayers: Boolean,
  onExplodeLayerClick: (Boolean) -> Unit,
  widgetName: String?,
) {
  val dialogState = rememberSaveable { mutableStateOf<EditorScreenAlertDialogState?>(null) }

  DropDownMenuWithFilledTonalButton {
    EditorScreenDropDownMenuContent(
      onRename = {
        dialogState.value = EditorScreenAlertDialogState.RENAME
        this@DropDownMenuWithFilledTonalButton.closeMenu()
      },
      onForceUpdate = onForceUpdate,
      onSaveAsPreset = {
        if (isWidgetDataSaved) {
          onNavigateToSaveAsPreset()
        } else {
          dialogState.value = EditorScreenAlertDialogState.SAVE_AS_PRESET_REQUIRE_SAVE
        }
        this@DropDownMenuWithFilledTonalButton.closeMenu()
      },
      onLoadFromPreset = {
        onNavigateToPresetSelector()
        this@DropDownMenuWithFilledTonalButton.closeMenu()
      },
      widgetDataSaverState = widgetDataSaverState,
      isInFullscreen = isInFullscreen,
      onFullScreenClick = onFullScreenClick,
      compactListMode = compactListMode,
      onCompactListModeClick = onCompactListModeClick,
      highlightSelectedLayer = highlightSelectedLayer,
      onHighlightSelectedLayerClick = onHighlightSelectedLayerClick,
      onNavigateToWidgetInfo = {
        onNavigateToWidgetInfo()
        this@DropDownMenuWithFilledTonalButton.closeMenu()
      },
      explodeLayers = explodeLayers,
      onExplodeLayerClick = onExplodeLayerClick,
    )
  }

  when (dialogState.value) {
    EditorScreenAlertDialogState.RENAME ->
      AlertDialogWithTextField(
        title = stringResource(Res.string.editor_rename_widget),
        onDismiss = { dialogState.value = null },
        onConfirm = { onRename(it) },
        icon = Symbols.Edit,
        confirmButtonLabel = stringResource(Res.string.common_rename),
        textFieldState = rememberTextFieldState(widgetName ?: ""),
        textFieldLabel = stringResource(Res.string.common_widget_name),
      )
    EditorScreenAlertDialogState.SAVE_AS_PRESET_REQUIRE_SAVE ->
      AlertDialogWithText(
        title = stringResource(Res.string.editor_save_as_preset),
        onDismiss = { dialogState.value = null },
        onConfirm = { onForceUpdate() },
        icon = Symbols.Save,
        confirmButtonLabel = stringResource(Res.string.common_save),
        text = stringResource(Res.string.editor_save_as_preset_require_save),
      )
    null -> Unit
  }
}

@Composable
private fun EditorScreenDropDownMenuContent(
  onRename: () -> Unit,
  onForceUpdate: () -> Unit,
  onSaveAsPreset: () -> Unit,
  onNavigateToWidgetInfo: () -> Unit,
  onLoadFromPreset: () -> Unit,
  widgetDataSaverState: WidgetDataSaverState,
  isInFullscreen: Boolean,
  onFullScreenClick: (Boolean) -> Unit,
  compactListMode: Boolean,
  onCompactListModeClick: (Boolean) -> Unit,
  highlightSelectedLayer: Boolean,
  onHighlightSelectedLayerClick: (Boolean) -> Unit,
  explodeLayers: Boolean,
  onExplodeLayerClick: (Boolean) -> Unit,
) {
  DropdownMenuGroup(shapes = MenuDefaults.groupShape(0, 2)) {
    DropdownMenuItem(
      shape = MenuDefaults.leadingItemShape,
      text = { Text(stringResource(Res.string.common_rename)) },
      onClick = onRename,
      leadingIcon = { Icon(Symbols.Edit, contentDescription = null) },
    )
    DropdownMenuItem(
      shape = MenuDefaults.middleItemShape,
      text = { Text(stringResource(Res.string.editor_save_as_preset)) },
      onClick = onSaveAsPreset,
      leadingIcon = { Icon(Symbols.SaveAs, contentDescription = null) },
      enabled = widgetDataSaverState !is WidgetDataSaverState.Running,
    )
    DropdownMenuItem(
      shape = MenuDefaults.middleItemShape,
      text = { Text(stringResource(Res.string.editor_load_from_preset)) },
      onClick = onLoadFromPreset,
      leadingIcon = { Icon(Symbols.LibraryAdd, contentDescription = null) },
    )
    DropdownMenuItem(
      shape = MenuDefaults.middleItemShape,
      text = { Text(stringResource(Res.string.common_widget_info)) },
      onClick = onNavigateToWidgetInfo,
      leadingIcon = { Icon(Symbols.Info, contentDescription = null) },
    )
    DropdownMenuItem(
      shape = MenuDefaults.trailingItemShape,
      text = { Text(stringResource(Res.string.editor_force_update)) },
      leadingIcon = { Icon(Symbols.Refresh, contentDescription = null) },
      onClick = onForceUpdate,
    )
  }
  DropdownMenuGroup(shapes = MenuDefaults.groupShape(1, 2)) {
    if (LocalWindowSize.current.widthSizeClass == WindowWidthSizeClass.Compact) {
      DropdownMenuItem(
        checked = isInFullscreen,
        onCheckedChange = onFullScreenClick,
        text = { Text(stringResource(Res.string.editor_full_screen_list)) },
        shapes = MenuDefaults.itemShapes(MenuDefaults.leadingItemShape),
        checkedLeadingIcon = { Icon(Symbols.Check, null) },
      )
    }
    DropdownMenuItem(
      checked = compactListMode,
      onCheckedChange = onCompactListModeClick,
      text = { Text(stringResource(Res.string.editor_compact_list)) },
      shapes = MenuDefaults.itemShapes(MenuDefaults.middleItemShape),
      checkedLeadingIcon = { Icon(Symbols.Check, null) },
    )
    DropdownMenuItem(
      checked = highlightSelectedLayer,
      onCheckedChange = onHighlightSelectedLayerClick,
      text = { Text(stringResource(Res.string.editor_highlight_selected_layer)) },
      shapes = MenuDefaults.itemShapes(MenuDefaults.middleItemShape),
      checkedLeadingIcon = { Icon(Symbols.Check, null) },
    )
    DropdownMenuItem(
      checked = explodeLayers,
      onCheckedChange = onExplodeLayerClick,
      text = { Text(stringResource(Res.string.editor_3d_view)) },
      shapes = MenuDefaults.itemShapes(MenuDefaults.trailingItemShape),
      checkedLeadingIcon = { Icon(Symbols.Check, null) },
    )
  }
}

private enum class EditorScreenAlertDialogState {
  RENAME,
  SAVE_AS_PRESET_REQUIRE_SAVE,
}

@Composable
@Preview
private fun PreviewEditorScreenDropDownMenuContent() {
  Column(verticalArrangement = Arrangement.spacedBy(MenuDefaults.GroupSpacing)) {
    EditorScreenDropDownMenuContent(
      onRename = {},
      onForceUpdate = {},
      onSaveAsPreset = {},
      onLoadFromPreset = {},
      widgetDataSaverState = remember { WidgetDataSaverState.NotRunning },
      isInFullscreen = false,
      onFullScreenClick = {},
      compactListMode = true,
      onCompactListModeClick = {},
      highlightSelectedLayer = false,
      onHighlightSelectedLayerClick = {},
      onNavigateToWidgetInfo = {},
      explodeLayers = false,
      onExplodeLayerClick = {},
    )
  }
}
