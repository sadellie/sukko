package io.github.sadellie.sukko.feature.fontseditor

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavKey
import co.touchlab.kermit.Logger
import com.composables.core.ModalBottomSheetState
import com.composables.core.SheetDetent
import com.composables.core.rememberModalBottomSheetState
import google.material.design.symbols.Add
import google.material.design.symbols.Symbols
import io.github.sadellie.sukko.core.common.collectAsStateWithLifecycleKMP
import io.github.sadellie.sukko.core.common.stateIn
import io.github.sadellie.sukko.core.designsystem.Preview2
import io.github.sadellie.sukko.core.designsystem.theme.ListArrangement
import io.github.sadellie.sukko.core.designsystem.theme.Sizes
import io.github.sadellie.sukko.core.fontfiles.FontFile
import io.github.sadellie.sukko.core.fontfiles.FontFileCustomRepository
import io.github.sadellie.sukko.core.fontfiles.FontFilesList
import io.github.sadellie.sukko.core.ui.AlertDialogWithText
import io.github.sadellie.sukko.core.ui.AlertDialogWithTextField
import io.github.sadellie.sukko.core.ui.BackHandler
import io.github.sadellie.sukko.core.ui.EmptyScreen
import io.github.sadellie.sukko.core.ui.ListItem2
import io.github.sadellie.sukko.core.ui.LoadingBox
import io.github.sadellie.sukko.core.ui.ModalBottomSheetWithButtons
import io.github.sadellie.sukko.core.ui.NavigateUpButton
import io.github.sadellie.sukko.core.ui.ScaffoldWithLargeTopAppBar
import io.github.sadellie.sukko.core.ui.firstShape
import io.github.sadellie.sukko.core.ui.lastShape
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.common_delete
import io.github.sadellie.sukko.resources.common_rename
import io.github.sadellie.sukko.resources.fonts_editor_delete
import io.github.sadellie.sukko.resources.fonts_editor_delete_text
import io.github.sadellie.sukko.resources.fonts_editor_file_already_exists
import io.github.sadellie.sukko.resources.fonts_editor_font_file_name
import io.github.sadellie.sukko.resources.fonts_editor_rename
import io.github.sadellie.sukko.resources.fonts_editor_title
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.viewmodel.koinViewModel

@Serializable data object FontFilesEditorRoute : NavKey

@Composable
fun FontsEditorScene(onNavigateUp: () -> Unit) {
  val viewModel = koinViewModel<FontsEditorViewModel>()
  val uiState = viewModel.uiState.collectAsStateWithLifecycleKMP().value

  if (uiState == null) {
    EmptyScreen()
  } else {
    FontsEditorScreen(
      onNavigateUp = onNavigateUp,
      onImport = viewModel::import,
      onDelete = viewModel::delete,
      onRename = viewModel::rename,
      uiState = uiState,
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FontsEditorScreen(
  onNavigateUp: () -> Unit,
  onImport: (List<PlatformFile>) -> Unit,
  onDelete: (FontFile.Custom) -> Unit,
  onRename: (FontFile.Custom, String) -> Unit,
  uiState: FontsEditorUIState,
) {
  val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
  val snackbarHost = remember { SnackbarHostState() }
  val coroutineScope = rememberCoroutineScope()
  val error = uiState.errorFlow.collectAsStateWithLifecycle(null).value
  LaunchedEffect(error) {
    if (error is FileAlreadyExistsException) {
      Logger.e(TAG) { "error emitted $error" }
      coroutineScope.launch {
        snackbarHost.showSnackbar(
          message = getString(Res.string.fonts_editor_file_already_exists, error.file.name)
        )
      }
    }
  }

  ScaffoldWithLargeTopAppBar(
    snackbarHost = { SnackbarHost(snackbarHost) },
    title = { Text(stringResource(Res.string.fonts_editor_title)) },
    navigationIcon = { NavigateUpButton(onNavigateUp, enabled = !uiState.isImporting) },
    scrollBehavior = scrollBehavior,
    actions = { AddFontButton(onImport = onImport, enabled = !uiState.isImporting) },
  ) { paddingValues ->
    Crossfade(targetState = uiState.isImporting) { isImporting ->
      if (isImporting) {
        LoadingBox(modifier = Modifier.padding(paddingValues).fillMaxSize())
        BackHandler(uiState.isImporting) {}
      } else {
        FontsEditorScreenReady(
          modifier =
            Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
              .consumeWindowInsets(paddingValues)
              .fillMaxSize()
              .padding(horizontal = Sizes.large),
          onDelete = onDelete,
          onRename = onRename,
          uiState = uiState,
          contentPadding = paddingValues,
        )
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FontsEditorScreenReady(
  modifier: Modifier,
  onDelete: (FontFile.Custom) -> Unit,
  onRename: (FontFile.Custom, String) -> Unit,
  uiState: FontsEditorUIState,
  contentPadding: PaddingValues,
) {
  var selectedFontFile by remember { mutableStateOf<FontFile.Custom?>(null) }
  val sheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
  val coroutineScope = rememberCoroutineScope()
  FontFilesList(
    modifier = modifier,
    custom = uiState.fontFilesCustom,
    onClick = {
      if (it is FontFile.Custom) {
        coroutineScope.launch {
          sheetState.animateTo(SheetDetent.FullyExpanded)
          selectedFontFile = it
        }
      }
    },
    contentPadding = contentPadding,
  )

  var dialogState by remember { mutableStateOf<FontsEditorsAlertState?>(null) }
  FontFileSheet(
    state = sheetState,
    onDelete = { selectedFontFile?.let { dialogState = FontsEditorsAlertState.Delete(it) } },
    onRename = { selectedFontFile?.let { dialogState = FontsEditorsAlertState.Rename(it) } },
  )
  when (val currentState = dialogState) {
    is FontsEditorsAlertState.Delete ->
      AlertDialogWithText(
        onDismiss = { dialogState = null },
        onConfirm = { onDelete(currentState.fontFile) },
        title = stringResource(Res.string.fonts_editor_delete),
        text = stringResource(Res.string.fonts_editor_delete_text),
      )
    is FontsEditorsAlertState.Rename ->
      AlertDialogWithTextField(
        onDismiss = { dialogState = null },
        onConfirm = { newName ->
          onRename(currentState.fontFile, "$newName.${currentState.fontFile.fileExtension}")
        },
        textFieldState = rememberTextFieldState(currentState.fontFile.fileNameWithoutExtension),
        title = stringResource(Res.string.fonts_editor_rename),
        textFieldLabel = stringResource(Res.string.fonts_editor_font_file_name),
      )
    null -> Unit
  }
}

@Composable
private fun FontFileSheet(
  state: ModalBottomSheetState,
  onDelete: () -> Unit,
  onRename: () -> Unit,
) {
  ModalBottomSheetWithButtons(state) {
    Column(
      modifier =
        Modifier.verticalScroll(rememberScrollState())
          .padding(horizontal = Sizes.large)
          .clip(MaterialTheme.shapes.large),
      verticalArrangement = ListArrangement,
    ) {
      ListItem2(
        headlineContent = { Text(stringResource(Res.string.common_rename)) },
        modifier =
          Modifier.clickable {
            onRename()
            state.targetDetent = SheetDetent.Hidden
          },
        shape = ListItemDefaults.firstShape,
      )
      ListItem2(
        headlineContent = { Text(stringResource(Res.string.common_delete)) },
        modifier =
          Modifier.clickable {
            onDelete()
            state.targetDetent = SheetDetent.Hidden
          },
        shape = ListItemDefaults.lastShape,
      )
    }
  }
}

@Composable
private fun AddFontButton(onImport: (List<PlatformFile>) -> Unit, enabled: Boolean) {
  val filePickerLauncher =
    rememberFilePickerLauncher(
      type = FileKitType.File(extensions = setOf("ttf", "otf")),
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
    enabled = enabled,
  ) {
    Icon(
      imageVector = Symbols.Add,
      contentDescription = null,
      modifier = Modifier.size(IconButtonDefaults.smallIconSize),
    )
  }
}

private sealed interface FontsEditorsAlertState {
  data class Delete(val fontFile: FontFile.Custom) : FontsEditorsAlertState

  data class Rename(val fontFile: FontFile.Custom) : FontsEditorsAlertState
}

class FontsEditorViewModel(private val fontFileCustomRepository: FontFileCustomRepository) :
  ViewModel() {
  private val _allFontFiles = fontFileCustomRepository.loadAll()
  private val _isImporting = MutableStateFlow(false)
  private val _errorFlow = MutableSharedFlow<Throwable?>()

  internal val uiState =
    combine(_allFontFiles, _isImporting) { allFontFiles, isImporting ->
        FontsEditorUIState(
          fontFilesCustom = allFontFiles,
          errorFlow = _errorFlow as SharedFlow<Throwable?>,
          isImporting = isImporting,
        )
      }
      .stateIn(viewModelScope, null)

  fun import(selectedFiles: List<PlatformFile>) =
    viewModelScope.launch {
      try {
        _errorFlow.tryEmit(null)
        _isImporting.update { true }
        fontFileCustomRepository.importFontFiles(selectedFiles)
      } catch (e: Exception) {
        Logger.e(TAG, e) { "Failed to import" }
        _errorFlow.tryEmit(e)
      } finally {
        _isImporting.update { false }
      }
    }

  fun delete(fontFile: FontFile.Custom) =
    viewModelScope.launch {
      try {
        fontFileCustomRepository.delete(fontFile)
      } catch (e: Exception) {
        Logger.e(TAG, e) { "Failed to delete" }
      }
    }

  fun rename(fontFile: FontFile.Custom, newName: String) =
    viewModelScope.launch {
      try {
        fontFileCustomRepository.rename(fontFile, newName)
      } catch (e: Exception) {
        Logger.e(TAG, e) { "Failed to rename" }
        _errorFlow.tryEmit(e)
      }
    }
}

internal data class FontsEditorUIState(
  val fontFilesCustom: List<FontFile.Custom>,
  val errorFlow: SharedFlow<Throwable?>,
  val isImporting: Boolean,
)

private const val TAG = "FontsEditorScene"

@Composable
@Preview
private fun PreviewFontsEditorScreen(
  @PreviewParameter(FontsEditorUIStateCollection::class) uiState: FontsEditorUIState
) = Preview2 {
  FontsEditorScreen(
    onNavigateUp = {},
    onImport = {},
    onDelete = {},
    onRename = { _, _ -> },
    uiState = uiState,
  )
}

@Suppress("MagicNumber")
private class FontsEditorUIStateCollection(
  override val values: Sequence<FontsEditorUIState> =
    sequenceOf(
      FontsEditorUIState(
        fontFilesCustom = emptyList(),
        errorFlow = MutableSharedFlow(),
        isImporting = true,
      ),
      FontsEditorUIState(
        fontFilesCustom = emptyList(),
        errorFlow = MutableSharedFlow(),
        isImporting = false,
      ),
      FontsEditorUIState(
        fontFilesCustom = List(5) { FontFile.Custom("font $it") },
        errorFlow = MutableSharedFlow(),
        isImporting = false,
      ),
    )
) : PreviewParameterProvider<FontsEditorUIState>
