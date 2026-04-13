package io.github.sadellie.sukko.feature.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarExitDirection
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.composables.core.SheetDetent
import com.composables.core.rememberModalBottomSheetState
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import dev.zacsweers.metrox.viewmodel.metroViewModel
import google.material.design.symbols.Add
import google.material.design.symbols.Check
import google.material.design.symbols.DeleteForever
import google.material.design.symbols.Edit
import google.material.design.symbols.EmojiPeople
import google.material.design.symbols.FileExport
import google.material.design.symbols.Settings
import google.material.design.symbols.Symbols
import io.github.sadellie.sukko.core.common.EXPORT_EXTENSION
import io.github.sadellie.sukko.core.common.collectAsStateWithLifecycleKMP
import io.github.sadellie.sukko.core.common.stateIn
import io.github.sadellie.sukko.core.data.WidgetDataPresetCustomRepository
import io.github.sadellie.sukko.core.designsystem.LocalFilesDirPath
import io.github.sadellie.sukko.core.designsystem.PreviewScreenSizesContainer
import io.github.sadellie.sukko.core.designsystem.theme.ListArrangement
import io.github.sadellie.sukko.core.designsystem.theme.Sizes
import io.github.sadellie.sukko.core.importexport.WidgetDataPresetExportImport
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.WidgetDataPreset
import io.github.sadellie.sukko.core.ui.AlertDialogWithText
import io.github.sadellie.sukko.core.ui.AlertDialogWithTextField
import io.github.sadellie.sukko.core.ui.EmptyScreen
import io.github.sadellie.sukko.core.ui.ListItem2
import io.github.sadellie.sukko.core.ui.ModalBottomSheetWithButtons
import io.github.sadellie.sukko.core.ui.ScaffoldWithLargeTopAppBar
import io.github.sadellie.sukko.core.ui.ScenePlaceholder
import io.github.sadellie.sukko.core.ui.WidgetDataPresetList
import io.github.sadellie.sukko.core.ui.firstShapes
import io.github.sadellie.sukko.core.ui.hide
import io.github.sadellie.sukko.core.ui.lastShapes
import io.github.sadellie.sukko.core.ui.middleShapes
import io.github.sadellie.sukko.core.ui.singleShapes
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.common_delete
import io.github.sadellie.sukko.resources.common_preset_list_placeholder_text
import io.github.sadellie.sukko.resources.common_preset_list_placeholder_title
import io.github.sadellie.sukko.resources.common_preset_name
import io.github.sadellie.sukko.resources.common_rename
import io.github.sadellie.sukko.resources.home_add_widget
import io.github.sadellie.sukko.resources.home_presets_apply_preset_text
import io.github.sadellie.sukko.resources.home_presets_apply_preset_title
import io.github.sadellie.sukko.resources.home_presets_delete
import io.github.sadellie.sukko.resources.home_presets_delete_text
import io.github.sadellie.sukko.resources.home_presets_export
import io.github.sadellie.sukko.resources.home_presets_export_text
import io.github.sadellie.sukko.resources.home_presets_open_widgets
import io.github.sadellie.sukko.resources.home_presets_rename_preset
import io.github.sadellie.sukko.resources.home_presets_title
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitDialogSettings
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberFileSaverLauncher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
internal actual fun PresetsScene(
  onNavigateToWidgets: () -> Unit,
  onNavigateToImportPreset: (selectedFile: PlatformFile) -> Unit,
  onNavigateToSettings: () -> Unit,
  onAddWidget: () -> Unit,
  toolBarNestedScrollConnection: NestedScrollConnection,
) {
  val viewModel = metroViewModel<PresetsViewModel>()
  val uiState = viewModel.uiState.collectAsStateWithLifecycleKMP().value

  if (uiState == null) {
    EmptyScreen()
  } else {
    PresetsScreen(
      uiState = uiState,
      onAddWidget = onAddWidget,
      onRename = viewModel::renamePreset,
      onDelete = viewModel::deletePreset,
      onExportPreset = viewModel::exportPreset,
      onNavigateToWidgets = onNavigateToWidgets,
      onNavigateToImportPreset = onNavigateToImportPreset,
      onNavigateToSettings = onNavigateToSettings,
      toolBarNestedScrollConnection = toolBarNestedScrollConnection,
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PresetsScreen(
  uiState: PresetsUIState,
  onAddWidget: () -> Unit,
  onRename: (preset: WidgetDataPreset, newName: String) -> Unit,
  onDelete: (preset: WidgetDataPreset) -> Unit,
  onExportPreset: (preset: WidgetDataPreset, file: PlatformFile) -> Unit,
  onNavigateToWidgets: () -> Unit,
  onNavigateToImportPreset: (selectedFile: PlatformFile) -> Unit,
  onNavigateToSettings: () -> Unit,
  toolBarNestedScrollConnection: NestedScrollConnection,
) {
  val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
  ScaffoldWithLargeTopAppBar(
    title = { Text(stringResource(Res.string.home_presets_title)) },
    actions = {
      PresetScreenTopBarActions(
        onNavigateToImportPreset = onNavigateToImportPreset,
        onNavigateToSettings = onNavigateToSettings,
      )
    },
    scrollBehavior = scrollBehavior,
  ) { padding ->
    val filesDirPath = LocalFilesDirPath.current
    var currentPresetSheet by remember { mutableStateOf<WidgetDataPreset?>(null) }
    val sheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
    val coroutineScope = rememberCoroutineScope()
    WidgetDataPresetList(
      modifier =
        Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
          .nestedScroll(toolBarNestedScrollConnection)
          .consumeWindowInsets(padding)
          .padding(horizontal = Sizes.large)
          .fillMaxSize(),
      widgetDataPresetsCustom = uiState.customWidgetDataPresets,
      widgetDataPresetsBuiltIn = remember { WidgetDataPreset.builtIns() },
      key = { it.presetId },
      previewSrc = { it.getPreviewPath(filesDirPath).toString() },
      name = { it.name },
      onClick = {
        coroutineScope.launch {
          sheetState.animateTo(SheetDetent.FullyExpanded)
          currentPresetSheet = it
        }
      },
      placeholder = {
        ScenePlaceholder(
          modifier = Modifier.padding(Sizes.large).fillMaxWidth(),
          icon = Symbols.EmojiPeople,
          title = stringResource(Res.string.common_preset_list_placeholder_title),
          text = stringResource(Res.string.common_preset_list_placeholder_text),
          onClick = onNavigateToWidgets,
          actionLabel = stringResource(Res.string.home_presets_open_widgets),
        )
      },
      contentPadding = padding,
    )

    val launcher =
      rememberFileSaverLauncher(FileKitDialogSettings()) { file ->
        if (file != null) {
          currentPresetSheet?.let { preset -> onExportPreset(preset, file) }
        }
      }
    currentPresetSheet?.let { preset ->
      ModalBottomSheetWithButtons(sheetState) {
        PresetSheetContent(
          onRename = {
            onRename(preset, it)
            sheetState.hide()
          },
          onExport = {
            launcher.launch(preset.name, defaultExtension = EXPORT_EXTENSION)
            sheetState.hide()
          },
          onDelete = {
            onDelete(preset)
            sheetState.hide()
          },
          onAddWidget = {
            onAddWidget()
            sheetState.hide()
          },
          preset = preset,
        )
      }
    }
  }
}

@Composable
private fun PresetScreenTopBarActions(
  onNavigateToImportPreset: (PlatformFile) -> Unit,
  onNavigateToSettings: () -> Unit,
) {
  val filePickerLauncher =
    rememberFilePickerLauncher(FileKitType.File(EXPORT_EXTENSION)) {
      if (it != null) onNavigateToImportPreset(it)
    }
  IconButton(
    modifier = Modifier.size(IconButtonDefaults.smallContainerSize()),
    shapes = IconButtonDefaults.shapes(),
    onClick = filePickerLauncher::launch,
  ) {
    Icon(
      imageVector = Symbols.Add,
      contentDescription = null,
      modifier = Modifier.size(IconButtonDefaults.mediumIconSize),
    )
  }

  Spacer(Modifier.width(2.dp))

  FilledTonalIconButton(
    modifier = Modifier.size(IconButtonDefaults.smallContainerSize()),
    shapes = IconButtonDefaults.shapes(),
    onClick = onNavigateToSettings,
  ) {
    Icon(
      imageVector = Symbols.Settings,
      contentDescription = null,
      modifier = Modifier.size(IconButtonDefaults.mediumIconSize),
    )
  }
}

@Composable
private fun PresetSheetContent(
  onRename: (newPresetName: String) -> Unit,
  onExport: () -> Unit,
  onDelete: () -> Unit,
  onAddWidget: () -> Unit,
  preset: WidgetDataPreset,
) {
  var dialogState by rememberSaveable { mutableStateOf<DialogState?>(null) }
  Column(
    modifier = Modifier.verticalScroll(rememberScrollState()).padding(horizontal = Sizes.large),
    verticalArrangement = ListArrangement,
  ) {
    val customPreset = preset is WidgetDataPreset.Custom
    ListItem2(
      content = { Text(stringResource(Res.string.home_presets_apply_preset_title)) },
      leadingContent = { Icon(Symbols.Check, contentDescription = null) },
      onClick = { dialogState = DialogState.APPLY },
      shapes = if (customPreset) ListItemDefaults.firstShapes else ListItemDefaults.singleShapes,
    )
    if (customPreset) {
      ListItem2(
        content = { Text(stringResource(Res.string.common_rename)) },
        leadingContent = { Icon(Symbols.Edit, contentDescription = null) },
        onClick = { dialogState = DialogState.RENAME },
        shapes = ListItemDefaults.middleShapes,
      )
      ListItem2(
        content = { Text(stringResource(Res.string.home_presets_export)) },
        leadingContent = { Icon(Symbols.FileExport, contentDescription = null) },
        onClick = { dialogState = DialogState.EXPORT },
        shapes = ListItemDefaults.middleShapes,
      )
      ListItem2(
        content = { Text(stringResource(Res.string.common_delete)) },
        leadingContent = { Icon(Symbols.DeleteForever, contentDescription = null) },
        onClick = { dialogState = DialogState.DELETE },
        shapes = ListItemDefaults.lastShapes,
      )
    }
  }
  when (dialogState) {
    DialogState.APPLY ->
      AlertDialogWithText(
        onDismiss = { dialogState = null },
        onConfirm = onAddWidget,
        icon = Symbols.Check,
        title = stringResource(Res.string.home_presets_apply_preset_title),
        text = stringResource(Res.string.home_presets_apply_preset_text),
        confirmButtonLabel = stringResource(Res.string.home_add_widget),
      )
    DialogState.RENAME ->
      AlertDialogWithTextField(
        title = stringResource(Res.string.home_presets_rename_preset),
        icon = Symbols.Edit,
        onDismiss = { dialogState = null },
        onConfirm = { onRename(it) },
        confirmButtonLabel = stringResource(Res.string.common_rename),
        textFieldState = rememberTextFieldState(preset.name),
        textFieldLabel = stringResource(Res.string.common_preset_name),
      )
    DialogState.EXPORT ->
      AlertDialogWithText(
        onDismiss = { dialogState = null },
        icon = Symbols.FileExport,
        onConfirm = onExport,
        title = stringResource(Res.string.home_presets_export),
        text = stringResource(Res.string.home_presets_export_text),
      )
    DialogState.DELETE ->
      AlertDialogWithText(
        onDismiss = { dialogState = null },
        icon = Symbols.DeleteForever,
        onConfirm = onDelete,
        confirmButtonLabel = stringResource(Res.string.common_delete),
        title = stringResource(Res.string.home_presets_delete),
        text = stringResource(Res.string.home_presets_delete_text),
      )
    null -> Unit
  }
}

@Inject
@ViewModelKey
@ContributesIntoMap(AppScope::class)
class PresetsViewModel(
  private val widgetDataPresetCustomRepository: WidgetDataPresetCustomRepository,
  private val widgetDataPresetExportImport: WidgetDataPresetExportImport,
) : ViewModel() {
  @OptIn(ExperimentalCoroutinesApi::class)
  internal val uiState =
    widgetDataPresetCustomRepository
      .allWidgetDataPresets(decodeExtra = false)
      .mapLatest { PresetsUIState(customWidgetDataPresets = it) }
      .stateIn(viewModelScope, null)

  fun renamePreset(preset: WidgetDataPreset, newName: String) =
    viewModelScope.launch {
      if (preset !is WidgetDataPreset.Custom) return@launch
      widgetDataPresetCustomRepository.rename(preset.presetId, newName)
    }

  fun exportPreset(preset: WidgetDataPreset, destination: PlatformFile) {
    viewModelScope.launch {
      if (preset !is WidgetDataPreset.Custom) return@launch
      try {
        widgetDataPresetExportImport.export(preset.presetId, destination)
      } catch (e: Exception) {
        Logger.e(throwable = e, tag = TAG) { "Failed to export preset with id ${preset.presetId}" }
      }
    }
  }

  fun deletePreset(preset: WidgetDataPreset) =
    viewModelScope.launch {
      if (preset !is WidgetDataPreset.Custom) return@launch
      widgetDataPresetCustomRepository.delete(preset.presetId)
    }
}

internal data class PresetsUIState(val customWidgetDataPresets: List<WidgetDataPreset.Custom>)

private enum class DialogState {
  APPLY,
  RENAME,
  EXPORT,
  DELETE,
}

private const val TAG = "PresetScene"

@Composable
@Preview
private fun PreviewPresetSheetContent() {
  PresetSheetContent(
    onRename = {},
    onExport = {},
    onDelete = {},
    onAddWidget = {},
    preset =
      WidgetDataPreset.Custom(
        presetId = 0,
        name = "Preset name",
        layers = emptyList(),
        globals = Globals(),
      ),
  )
}

@Preview
@PreviewScreenSizes
@Composable
private fun PreviewPresetsScreen(
  @PreviewParameter(PresetUIStateCollection::class) uiState: PresetsUIState
) = PreviewScreenSizesContainer {
  PresetsScreen(
    uiState = uiState,
    onRename = { _, _ -> },
    onDelete = {},
    onAddWidget = {},
    onExportPreset = { _, _ -> },
    onNavigateToWidgets = {},
    onNavigateToImportPreset = {},
    onNavigateToSettings = {},
    toolBarNestedScrollConnection =
      FloatingToolbarDefaults.exitAlwaysScrollBehavior(FloatingToolbarExitDirection.Bottom),
  )
}

@Suppress("MagicNumber")
private class PresetUIStateCollection(
  override val values: Sequence<PresetsUIState> =
    sequenceOf(
      PresetsUIState(
        customWidgetDataPresets =
          List(4) {
            WidgetDataPreset.Custom(
              presetId = it.toLong(),
              name = "Preset $it",
              layers = emptyList(),
              globals = Globals(),
            )
          }
      ),
      PresetsUIState(customWidgetDataPresets = emptyList()),
    )
) : PreviewParameterProvider<PresetsUIState>
