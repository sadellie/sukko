package io.github.sadellie.sukko.feature.editor.selector

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.annotation.ExperimentalCoilApi
import com.composables.core.ModalBottomSheetState
import io.github.sadellie.sukko.core.common.collectAsStateWithLifecycleKMP
import io.github.sadellie.sukko.core.common.observe
import io.github.sadellie.sukko.core.common.stateIn
import io.github.sadellie.sukko.core.data.IconPackCustomRepository
import io.github.sadellie.sukko.core.designsystem.Preview2
import io.github.sadellie.sukko.core.designsystem.theme.Sizes
import io.github.sadellie.sukko.core.iconfiles.IconFile
import io.github.sadellie.sukko.core.iconfiles.IconFilesGrid
import io.github.sadellie.sukko.core.iconfiles.IconPack
import io.github.sadellie.sukko.core.ui.AlertDialogWithListItems
import io.github.sadellie.sukko.core.ui.ListItem2
import io.github.sadellie.sukko.core.ui.LoadingBox
import io.github.sadellie.sukko.core.ui.ModalBottomSheet2
import io.github.sadellie.sukko.core.ui.SearchBar
import io.github.sadellie.sukko.core.ui.SheetContentWithButtons
import io.github.sadellie.sukko.core.ui.hide
import io.github.sadellie.sukko.core.ui.singleShape
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.common_not_selected
import io.github.sadellie.sukko.resources.editor_selector_icon_pack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun IconSelectorSheet(
  state: ModalBottomSheetState,
  onValueSelected: (newValue: IconFile) -> Unit,
  value: IconFile?,
) {
  ModalBottomSheet2(state = state) {
    val viewModel = koinViewModel<IconSelectorViewModel>()
    val uiState = viewModel.uiState.collectAsStateWithLifecycleKMP().value
    LaunchedEffect(Unit) {
      viewModel.cleanUpSheet(value)
      viewModel.observeInputFilters()
    }

    SheetContentWithButtons(
      onDismiss = state::hide,
      onConfirm = {
        if (uiState?.selectedIconFile != null) onValueSelected(uiState.selectedIconFile)
      },
      isConfirmButtonEnabled = uiState?.selectedIconFile != null,
      sheetContent = {
        if (uiState == null) {
          LoadingBox(Modifier.fillMaxWidth().padding(Sizes.large))
        } else {
          IconSelectorSheetContent(
            onIconPackChange = viewModel::updateSelectedIconPack,
            onClick = viewModel::updateSelectedIconFile,
            onDismiss = state::hide,
            uiState = uiState,
          )
        }
      },
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IconSelectorSheetContent(
  onIconPackChange: (selectedIconPack: IconPack) -> Unit,
  onClick: (IconFile) -> Unit,
  onDismiss: () -> Unit,
  uiState: IconSelectorUIState,
) {
  Column(
    modifier = Modifier.padding(horizontal = Sizes.large),
    verticalArrangement = Arrangement.spacedBy(Sizes.small),
  ) {
    var showIconPackSelector by rememberSaveable { mutableStateOf(false) }
    ListItem2(
      headlineContent = { Text(stringResource(Res.string.editor_selector_icon_pack)) },
      supportingContent = {
        Text(uiState.iconPack?.name ?: stringResource(Res.string.common_not_selected))
      },
      modifier =
        Modifier.clip(MaterialTheme.shapes.large).clickable { showIconPackSelector = true },
      shape = ListItemDefaults.singleShape,
    )

    SearchBar(
      modifier = Modifier.fillMaxWidth(),
      state = uiState.textFieldState,
      navigateUp = onDismiss,
    )

    AnimatedVisibility(visible = uiState.iconPack != null, modifier = Modifier.weight(1f)) {
      Crossfade(
        modifier =
          Modifier.fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceBright),
        targetState = uiState.iconFiles,
      ) { iconFiles ->
        if (iconFiles == null) {
          LoadingBox(modifier = Modifier.fillMaxSize())
        } else {
          IconFilesGrid(
            modifier = Modifier.fillMaxSize(),
            iconFiles = iconFiles,
            selectedIconFile = uiState.selectedIconFile,
            onClick = onClick,
            contentPadding = PaddingValues(Sizes.small),
          )
        }
      }
    }

    if (showIconPackSelector) {
      AlertDialogWithListItems(
        title = stringResource(Res.string.editor_selector_icon_pack),
        onDismiss = { showIconPackSelector = false },
        items = uiState.iconPacks,
        key = { it.iconPackId },
        headlineText = { it.name },
        onClick = onIconPackChange,
      )
    }
  }
}

class IconSelectorViewModel(private val iconPackCustomRepository: IconPackCustomRepository) :
  ViewModel() {
  val textFieldState = TextFieldState()
  private val _selectedIconFile = MutableStateFlow<IconFile?>(null)
  private val _selectedIconPack = MutableStateFlow<IconPack?>(null)
  // null when loading
  private val _iconFiles = MutableStateFlow<List<IconFile>?>(null)
  private var _job: Job? = null
  private val _allCustomIconPacks = iconPackCustomRepository.getAll()

  internal val uiState =
    combine(_selectedIconPack, _selectedIconFile, _iconFiles, _allCustomIconPacks) {
        selectedIconPack,
        selectedIconFile,
        iconFiles,
        allCustomIconPacks ->
        return@combine IconSelectorUIState(
          textFieldState = textFieldState,
          iconPack = selectedIconPack,
          iconPacks = IconPack.builtIns() + allCustomIconPacks,
          iconFiles = iconFiles,
          selectedIconFile = selectedIconFile,
        )
      }
      .stateIn(viewModelScope, null)

  fun updateSelectedIconFile(newValue: IconFile?) = _selectedIconFile.update { newValue }

  fun updateSelectedIconPack(newValue: IconPack?) = _selectedIconPack.update { newValue }

  fun cleanUpSheet(initialValue: IconFile?) {
    _iconFiles.update { null }
    updateSelectedIconFile(initialValue)
    updateSelectedIconPack(initialValue?.iconPack)
    textFieldState.setTextAndPlaceCursorAtEnd(initialValue?.name ?: "")
  }

  suspend fun observeInputFilters() {
    val queryFlow = textFieldState.observe()
    combine(_selectedIconPack, queryFlow) { iconPack, query ->
        updateIconFiles(iconPack, query.toString())
      }
      .collectLatest {}
  }

  private fun updateIconFiles(iconPack: IconPack?, query: String) {
    _job?.cancel()
    _job =
      viewModelScope.launch(Dispatchers.Default) {
        _iconFiles.update { null }
        if (iconPack == null) {
          _iconFiles.update { emptyList() }
          return@launch
        }
        var iconFiles = iconPackCustomRepository.getIconFilesFromIconPack(iconPack)
        if (query.isNotBlank()) iconFiles = iconFiles.filter { query in it.name }
        _iconFiles.update { iconFiles }
      }
  }
}

internal data class IconSelectorUIState(
  val textFieldState: TextFieldState,
  val iconPack: IconPack?,
  val iconPacks: List<IconPack>,
  val iconFiles: List<IconFile>?,
  val selectedIconFile: IconFile?,
)

@OptIn(ExperimentalCoilApi::class)
@Composable
@Preview
private fun PreviewIconSelectorSheetContent() = Preview2 {
  val iconFiles = remember {
    List(80) {
      IconFile(fileName = "icon_file_$it.svg", iconPack = IconPack.MaterialSymbolsRounded)
    }
  }
  var selectedIconFile by remember { mutableStateOf<IconFile?>(iconFiles[1]) }

  IconSelectorSheetContent(
    onIconPackChange = {},
    onClick = { selectedIconFile = it },
    onDismiss = {},
    uiState =
      IconSelectorUIState(
        iconPack = IconPack.MaterialSymbolsRounded,
        iconPacks = emptyList(),
        iconFiles = iconFiles,
        selectedIconFile = selectedIconFile,
        textFieldState = rememberTextFieldState(),
      ),
  )
}
