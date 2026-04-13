package io.github.sadellie.sukko.feature.editor.selector.scripteditor

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import dev.zacsweers.metrox.viewmodel.metroViewModel
import io.github.sadellie.sukko.core.common.collectAsStateWithLifecycleKMP
import io.github.sadellie.sukko.core.designsystem.theme.Sizes
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.ui.BackHandler
import io.github.sadellie.sukko.core.ui.SheetContentWithButtons
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.common_cancel
import io.github.sadellie.sukko.resources.common_confirm
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ScriptEditorSheetContent(
  initialInput: String,
  globals: Globals,
  enableGlobalOverrides: Boolean = false,
  onDismiss: () -> Unit,
  onConfirm: (String) -> Unit,
  dismissLabel: String = stringResource(Res.string.common_cancel),
  confirmLabel: String = stringResource(Res.string.common_confirm),
) {
  val docsViewModel = metroViewModel<DocsViewModel>()
  val inputPageViewModel =
    assistedMetroViewModel<InputPageViewModel, InputPageViewModel.Factory> {
      create(initialInput, enableGlobalOverrides, globals)
    }
  SheetContentWithButtons(
    onDismiss = onDismiss,
    onConfirm = {
      val uiState = inputPageViewModel.uiState.value ?: return@SheetContentWithButtons
      onConfirm(uiState.input.text.toString())
    },
    isConfirmButtonEnabled = true,
    dismissLabel = dismissLabel,
    confirmLabel = confirmLabel,
  ) {
    ScriptEditor(
      modifier = Modifier.fillMaxSize().padding(horizontal = Sizes.large),
      inputPageViewModel = inputPageViewModel,
      docsViewModel = docsViewModel,
    )
  }
}

@Composable
internal fun ScriptEditor(
  modifier: Modifier,
  inputPageViewModel: InputPageViewModel,
  docsViewModel: DocsViewModel,
) {
  // created once for entire editor route
  var page by remember { mutableStateOf<ScriptEditorPage>(ScriptEditorPage.InputPage) }

  BackHandler(page != ScriptEditorPage.InputPage) { page = ScriptEditorPage.InputPage }

  AnimatedContent(targetState = page, modifier = modifier) { currentPage ->
    val pageModifier = Modifier.fillMaxSize()
    when (currentPage) {
      ScriptEditorPage.InputPage ->
        InputPage(
          modifier = pageModifier,
          openDocs = { page = ScriptEditorPage.DocsPage },
          uiState = inputPageViewModel.uiState.collectAsStateWithLifecycleKMP().value,
          observeInput = inputPageViewModel::observeInput,
        )
      ScriptEditorPage.DocsPage ->
        DocsPage(
          modifier = pageModifier,
          viewModel = docsViewModel,
          backToInput = { page = ScriptEditorPage.InputPage },
          onInsert = { script ->
            inputPageViewModel.insertInInput(script)
            page = ScriptEditorPage.InputPage
          },
        )
    }
  }
}
