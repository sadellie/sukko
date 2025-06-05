package io.github.sadellie.sukko.feature.editor.selector.scripteditor

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.sadellie.sukko.core.designsystem.Preview2
import io.github.sadellie.sukko.core.designsystem.theme.Sizes
import io.github.sadellie.sukko.core.model.basic.Scriptable
import io.github.sadellie.sukko.core.model.basic.ScriptableString
import io.github.sadellie.sukko.core.ui.BackHandler
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun <T> ScriptEditor(
  modifier: Modifier,
  textFieldState: TextFieldState,
  produceScriptable: (text: String) -> Scriptable.Script<T>,
  successPreview: @Composable (T) -> Unit,
) {
  // created once for entire editor route
  val docsViewModel = koinViewModel<DocsViewModel>()
  var page by remember { mutableStateOf<ScriptEditorPage>(ScriptEditorPage.InputPage) }
  var autoReload by rememberSaveable { mutableStateOf(false) }
  val scriptEditorState =
    produceScriptEditorState(textFieldState.text, autoReload) {
      produceScriptable(textFieldState.text.toString())
    }
  BackHandler(page != ScriptEditorPage.InputPage) { page = ScriptEditorPage.InputPage }

  AnimatedContent(targetState = page, modifier = modifier) { currentPage ->
    val pageModifier = Modifier.fillMaxSize()
    when (currentPage) {
      ScriptEditorPage.InputPage ->
        InputPage(
          modifier = pageModifier,
          textFieldState = textFieldState,
          scriptEditorState = scriptEditorState.value,
          openDocs = { page = ScriptEditorPage.DocsPage },
          onAutoReloadChange = { autoReload = it },
          successPreview = successPreview,
        )
      ScriptEditorPage.DocsPage ->
        DocsPage(
          modifier = pageModifier,
          viewModel = docsViewModel,
          backToInput = { page = ScriptEditorPage.InputPage },
          onInsert = { script ->
            textFieldState.insert(script)
            page = ScriptEditorPage.InputPage
          },
        )
    }
  }
}

@Composable
@Preview
private fun PreviewScriptEditor() = Preview2 {
  Surface(color = MaterialTheme.colorScheme.surfaceContainer) {
    ScriptEditor(
      modifier = Modifier.height(400.dp).fillMaxWidth().padding(Sizes.large),
      produceScriptable = { ScriptableString.Script("Test value (OK)") },
      textFieldState = rememberTextFieldState("this is a test\nsecond line"),
    ) {
      SuccessMessage(it)
    }
  }
}
