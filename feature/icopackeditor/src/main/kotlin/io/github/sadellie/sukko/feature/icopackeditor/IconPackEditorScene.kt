package io.github.sadellie.sukko.feature.icopackeditor

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavKey
import co.touchlab.kermit.Logger
import com.composables.core.SheetDetent
import com.composables.core.rememberModalBottomSheetState
import google.material.design.symbols.Add
import google.material.design.symbols.DeleteForever
import google.material.design.symbols.Edit
import google.material.design.symbols.Symbols
import io.github.sadellie.sukko.core.common.collectAsStateWithLifecycleKMP
import io.github.sadellie.sukko.core.common.stateIn
import io.github.sadellie.sukko.core.data.IconPackCustomRepository
import io.github.sadellie.sukko.core.designsystem.Preview2
import io.github.sadellie.sukko.core.designsystem.theme.ListArrangement
import io.github.sadellie.sukko.core.designsystem.theme.Sizes
import io.github.sadellie.sukko.core.iconfiles.IconFile
import io.github.sadellie.sukko.core.iconfiles.IconFilesGrid
import io.github.sadellie.sukko.core.iconfiles.IconPack
import io.github.sadellie.sukko.core.ui.AlertDialogWithText
import io.github.sadellie.sukko.core.ui.AlertDialogWithTextField
import io.github.sadellie.sukko.core.ui.EmptyScreen
import io.github.sadellie.sukko.core.ui.ListItem2
import io.github.sadellie.sukko.core.ui.LoadingScaffold
import io.github.sadellie.sukko.core.ui.LoadingScaffoldWithLargeTopAppBar
import io.github.sadellie.sukko.core.ui.ModalBottomSheetWithButtons
import io.github.sadellie.sukko.core.ui.NavigateUpButton
import io.github.sadellie.sukko.core.ui.ScaffoldWithLargeTopAppBar
import io.github.sadellie.sukko.core.ui.firstShape
import io.github.sadellie.sukko.core.ui.lastShape
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.common_delete
import io.github.sadellie.sukko.resources.common_rename
import io.github.sadellie.sukko.resources.icon_pack_editor_delete_text
import io.github.sadellie.sukko.resources.icon_pack_editor_delete_title
import io.github.sadellie.sukko.resources.icon_pack_editor_icon_file_name
import io.github.sadellie.sukko.resources.icon_pack_editor_rename
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Serializable data class IconPackEditor(val iconPack: IconPack) : NavKey

@Composable
fun IconPackEditorScene(onNavigateUp: () -> Unit, iconPack: IconPack) {
  val viewModel =
    koinViewModel<IconPackEditorViewModel>(key = iconPack.iconPackId.toString()) {
      parametersOf(iconPack)
    }
  val uiState = viewModel.uiState.collectAsStateWithLifecycleKMP().value

  if (uiState == null) {
    EmptyScreen()
  } else {
    IconPackEditorScreen(
      onNavigateUp = onNavigateUp,
      onImport = viewModel::import,
      onDelete = viewModel::delete,
      onRename = viewModel::rename,
      uiState = uiState,
    )
  }
}

@Composable
private fun IconPackEditorScreen(
  onNavigateUp: () -> Unit,
  onImport: (List<PlatformFile>) -> Unit,
  onDelete: (IconFile) -> Unit,
  onRename: (IconFile, newName: String) -> Unit,
  uiState: IconPackEditorUIState,
) {
  Crossfade(uiState) { currentState ->
    when (currentState) {
      IconPackEditorUIState.Importing -> LoadingScaffold(disableBack = true)
      is IconPackEditorUIState.Loading ->
        LoadingScaffoldWithLargeTopAppBar(
          onNavigateUp = onNavigateUp,
          disableBack = false,
          title = currentState.iconPack.name,
        )
      is IconPackEditorUIState.Ready ->
        IconPackEditorScreenReady(
          onNavigateUp = onNavigateUp,
          onImport = onImport,
          onDelete = onDelete,
          onRename = onRename,
          uiState = currentState,
        )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IconPackEditorScreenReady(
  onNavigateUp: () -> Unit,
  onImport: (List<PlatformFile>) -> Unit,
  onDelete: (IconFile) -> Unit,
  onRename: (IconFile, newName: String) -> Unit,
  uiState: IconPackEditorUIState.Ready,
) {
  var alertState by remember { mutableStateOf<IconPackEditorAlertState?>(null) }
  var selectedIconFile by remember { mutableStateOf<IconFile?>(null) }
  val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
  ScaffoldWithLargeTopAppBar(
    title = { Text(uiState.iconPack.name, maxLines = 1) },
    navigationIcon = { NavigateUpButton(onNavigateUp) },
    actions = {
      if (uiState.iconPack is IconPack.Custom) {
        val filePickerLauncher =
          rememberFilePickerLauncher(
            type = FileKitType.File(extension = IconFile.EXTENSION),
            mode = FileKitMode.Multiple(),
          ) { picked ->
            if (picked != null) onImport(picked)
          }
        FilledIconButton(
          onClick = filePickerLauncher::launch,
          shapes = IconButtonDefaults.shapes(),
          modifier =
            Modifier.size(
              IconButtonDefaults.smallContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide)
            ),
        ) {
          Icon(
            imageVector = Symbols.Add,
            contentDescription = null,
            modifier = Modifier.size(IconButtonDefaults.smallIconSize),
          )
        }
      }
    },
    scrollBehavior = scrollBehavior,
  ) { paddingValues ->
    IconFilesGrid(
      modifier =
        Modifier.padding(paddingValues)
          .fillMaxSize()
          .nestedScroll(scrollBehavior.nestedScrollConnection)
          .padding(Sizes.large)
          .clip(MaterialTheme.shapes.large)
          .background(MaterialTheme.colorScheme.surfaceBright),
      iconFiles = uiState.iconFiles,
      onClick = { if (it.iconPack is IconPack.Custom) selectedIconFile = it },
      contentPadding = PaddingValues(Sizes.small),
    )
  }

  selectedIconFile?.let { iconFile ->
    val sheetState = rememberModalBottomSheetState(SheetDetent.FullyExpanded)
    ModalBottomSheetWithButtons(sheetState) {
      Column(
        modifier =
          Modifier.verticalScroll(rememberScrollState())
            .padding(start = Sizes.large, end = Sizes.large, bottom = Sizes.large)
            .clip(MaterialTheme.shapes.large),
        verticalArrangement = ListArrangement,
      ) {
        ListItem2(
          headlineContent = { Text(stringResource(Res.string.common_rename)) },
          leadingContent = { Icon(Symbols.Edit, contentDescription = null) },
          modifier =
            Modifier.clickable {
              alertState = IconPackEditorAlertState.Rename(iconFile)
              sheetState.targetDetent = SheetDetent.Hidden
            },
          shape = ListItemDefaults.firstShape,
        )
        ListItem2(
          headlineContent = { Text(stringResource(Res.string.common_delete)) },
          leadingContent = { Icon(Symbols.DeleteForever, contentDescription = null) },
          modifier =
            Modifier.clickable {
              alertState = IconPackEditorAlertState.Delete(iconFile)
              sheetState.targetDetent = SheetDetent.Hidden
            },
          shape = ListItemDefaults.lastShape,
        )
      }
    }
  }

  when (val state = alertState) {
    is IconPackEditorAlertState.Delete ->
      AlertDialogWithText(
        onDismiss = { alertState = null },
        onConfirm = { onDelete(state.iconFile) },
        title = stringResource(Res.string.icon_pack_editor_delete_title),
        text = stringResource(Res.string.icon_pack_editor_delete_text),
      )
    is IconPackEditorAlertState.Rename ->
      AlertDialogWithTextField(
        onDismiss = { alertState = null },
        onConfirm = { onRename(state.iconFile, "$it.${IconFile.EXTENSION}") },
        textFieldState = rememberTextFieldState(state.iconFile.name),
        title = stringResource(Res.string.icon_pack_editor_rename),
        textFieldLabel = stringResource(Res.string.icon_pack_editor_icon_file_name),
      )
    null -> Unit
  }
}

class IconPackEditorViewModel(
  private val iconPack: IconPack,
  private val iconPackCustomRepository: IconPackCustomRepository,
) : ViewModel() {
  private val _allIconFiles =
    iconPackCustomRepository.getIconFiles(iconPack).stateIn(viewModelScope, null)
  private val _isImporting = MutableStateFlow(false)

  internal val uiState =
    combine(_allIconFiles, _isImporting) { allIconFiles, isImporting ->
        if (isImporting) return@combine IconPackEditorUIState.Importing
        if (allIconFiles == null) return@combine IconPackEditorUIState.Loading(iconPack)
        IconPackEditorUIState.Ready(iconPack, allIconFiles)
      }
      .stateIn(viewModelScope, null)

  fun import(selectedFiles: List<PlatformFile>) =
    viewModelScope.launch {
      if (iconPack !is IconPack.Custom) return@launch
      try {
        _isImporting.update { true }
        iconPackCustomRepository.importIconFiles(iconPack, selectedFiles)
      } catch (e: Exception) {
        Logger.e(TAG, e) { "Failed to import" }
      } finally {
        _isImporting.update { false }
      }
    }

  fun delete(iconFile: IconFile) =
    viewModelScope.launch { iconPackCustomRepository.deleteIconFile(iconFile) }

  fun rename(iconFile: IconFile, newName: String) =
    viewModelScope.launch { iconPackCustomRepository.renameIconFile(iconFile, newName) }
}

internal sealed interface IconPackEditorUIState {
  data object Importing : IconPackEditorUIState

  data class Loading(val iconPack: IconPack) : IconPackEditorUIState

  data class Ready(val iconPack: IconPack, val iconFiles: List<IconFile>) : IconPackEditorUIState
}

private sealed interface IconPackEditorAlertState {
  data class Delete(val iconFile: IconFile) : IconPackEditorAlertState

  data class Rename(val iconFile: IconFile) : IconPackEditorAlertState
}

private const val TAG = "IconPackEditorScene"

@Composable
@Preview
private fun PreviewIconPackEditorScreen(
  @PreviewParameter(IconPackEditorUIStateCollection::class) uiState: IconPackEditorUIState
) = Preview2 {
  IconPackEditorScreen(
    onNavigateUp = {},
    onImport = {},
    onDelete = {},
    onRename = { _, _ -> },
    uiState = uiState,
  )
}

@Suppress("MagicNumber")
private class IconPackEditorUIStateCollection(
  override val values: Sequence<IconPackEditorUIState> =
    sequenceOf(
      IconPackEditorUIState.Importing,
      IconPackEditorUIState.Loading(iconPack = IconPack.MaterialSymbolsRounded),
      IconPackEditorUIState.Ready(
        iconPack = IconPack.MaterialSymbolsRounded,
        iconFiles =
          List(8) {
            IconFile(
              fileName = "/0/test_icon_$it.svg",
              iconPack = IconPack.Custom(0, "Icon pack 1"),
            )
          },
      ),
      IconPackEditorUIState.Ready(
        iconPack = IconPack.Custom(0, "Icon pack 1"),
        iconFiles = emptyList(),
      ),
      IconPackEditorUIState.Ready(
        iconPack = IconPack.Custom(0, "Icon pack 1"),
        iconFiles =
          List(80) {
            IconFile(
              fileName = "/0/test_icon_$it.svg",
              iconPack = IconPack.Custom(0, "Icon pack 1"),
            )
          },
      ),
    )
) : PreviewParameterProvider<IconPackEditorUIState>
