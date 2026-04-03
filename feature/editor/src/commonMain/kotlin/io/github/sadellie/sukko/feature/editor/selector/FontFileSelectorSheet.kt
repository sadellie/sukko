package io.github.sadellie.sukko.feature.editor.selector

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.sadellie.sukko.core.common.collectAsStateWithLifecycleKMP
import io.github.sadellie.sukko.core.common.stateIn
import io.github.sadellie.sukko.core.designsystem.Preview2
import io.github.sadellie.sukko.core.designsystem.theme.Sizes
import io.github.sadellie.sukko.core.fontfiles.FontFile
import io.github.sadellie.sukko.core.fontfiles.FontFileCustomRepository
import io.github.sadellie.sukko.core.fontfiles.FontFilesList
import io.github.sadellie.sukko.core.ui.LoadingBox
import io.github.sadellie.sukko.core.ui.SheetContentWithButtons
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.common_cancel
import io.github.sadellie.sukko.resources.common_confirm
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun FontFileSelectorSheetContent(
  onDismissRequest: () -> Unit,
  onValueSelected: (newValue: FontFile) -> Unit,
  value: FontFile?,
  dismissLabel: String = stringResource(Res.string.common_cancel),
  confirmLabel: String = stringResource(Res.string.common_confirm),
) {
  val viewModel = koinViewModel<FontFileSelectorViewModel> { parametersOf(value) }
  val uiState = viewModel.uiState.collectAsStateWithLifecycleKMP().value
  SheetContentWithButtons(
    onDismiss = onDismissRequest,
    onConfirm = { if (uiState.selectedFontFile != null) onValueSelected(uiState.selectedFontFile) },
    isConfirmButtonEnabled = uiState.selectedFontFile != null,
    dismissLabel = dismissLabel,
    confirmLabel = confirmLabel,
    sheetContent = {
      FontFileSelectorSheetInnerContent(
        uiState = uiState,
        onSelectFontFile = viewModel::selectFontFile,
      )
    },
  )
}

@Composable
private fun FontFileSelectorSheetInnerContent(
  uiState: FontFileSelectorUIState,
  onSelectFontFile: (FontFile) -> Unit,
) {
  Crossfade(targetState = uiState.fontFiles) { fontFiles ->
    if (fontFiles == null) {
      LoadingBox(modifier = Modifier.fillMaxWidth().padding(Sizes.large))
    } else {
      FontFilesList(
        modifier = Modifier.fillMaxWidth().padding(horizontal = Sizes.large),
        custom = fontFiles,
        onClick = onSelectFontFile,
        selectedFontFile = uiState.selectedFontFile,
      )
    }
  }
}

internal class FontFileSelectorViewModel(
  initialValue: FontFile?,
  fontFileCustomRepository: FontFileCustomRepository,
) : ViewModel() {
  private val _fontFiles = fontFileCustomRepository.loadAll().stateIn(viewModelScope, null)
  private val _selectedFontFile = MutableStateFlow(initialValue)

  internal val uiState =
    combine(_fontFiles, _selectedFontFile) { fontFiles, selectedFontFile ->
        FontFileSelectorUIState(fontFiles = fontFiles, selectedFontFile = selectedFontFile)
      }
      .stateIn(viewModelScope, FontFileSelectorUIState(null, initialValue))

  fun selectFontFile(fontFile: FontFile) = _selectedFontFile.update { fontFile }
}

internal data class FontFileSelectorUIState(
  val fontFiles: List<FontFile.Custom>?,
  val selectedFontFile: FontFile?,
)

@Composable
@Preview
private fun PreviewFontFileSelectorSheetInnerContent() = Preview2 {
  val fontFiles = remember { List(9) { FontFile.Custom(fileName = "font_$it.otf") } }
  var selectedFontFile by remember { mutableStateOf<FontFile?>(FontFile.System) }
  FontFileSelectorSheetInnerContent(
    uiState = FontFileSelectorUIState(fontFiles = fontFiles, selectedFontFile = selectedFontFile)
  ) {
    selectedFontFile = it
  }
}
