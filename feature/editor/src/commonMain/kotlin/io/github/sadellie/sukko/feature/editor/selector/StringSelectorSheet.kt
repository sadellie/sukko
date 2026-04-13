package io.github.sadellie.sukko.feature.editor.selector

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.composables.core.ModalBottomSheetState
import io.github.sadellie.sukko.core.designsystem.theme.Sizes
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.basic.GlobalValue
import io.github.sadellie.sukko.core.model.basic.ScriptableString
import io.github.sadellie.sukko.core.ui.SheetContentWithButtons
import io.github.sadellie.sukko.core.ui.SukkoOutlinedTextField
import io.github.sadellie.sukko.core.ui.hide
import io.github.sadellie.sukko.feature.editor.selector.scripteditor.ScriptEditorSheetContent

@Composable
fun StringSelectorSheet(
  state: ModalBottomSheetState,
  onValueSelected: (newValue: ScriptableString) -> Unit,
  value: ScriptableString,
  globals: Globals,
) {
  var currentInputMode by rememberSaveable { mutableStateOf(DefaultInputMode.initialMode(value)) }

  SelectorSheetTemplate(
    state = state,
    inputModes = remember { DefaultInputMode.entries },
    currentInputMode = currentInputMode,
    onInputModeUpdate = { currentInputMode = it },
  ) { currentMode ->
    when (currentMode) {
      DefaultInputMode.FIXED ->
        FixedString(onDismiss = state::hide, onConfirm = onValueSelected, initialValue = value)
      DefaultInputMode.SCRIPT ->
        ScriptString(
          onDismiss = state::hide,
          onConfirm = onValueSelected,
          initialValue = value,
          globals = globals,
        )
      DefaultInputMode.GLOBAL ->
        GlobalString(
          onDismiss = state::hide,
          onConfirm = onValueSelected,
          initialValue = value,
          globals = globals.strings,
        )
    }
  }
}

@Composable
private fun FixedString(
  onDismiss: () -> Unit,
  onConfirm: (value: ScriptableString.Fixed) -> Unit,
  initialValue: ScriptableString,
) {
  val textFieldState =
    rememberTextFieldState(if (initialValue is ScriptableString.Fixed) initialValue.value else "")
  SheetContentWithButtons(
    onDismiss = onDismiss,
    onConfirm = {
      val newValue = ScriptableString.Fixed(textFieldState.text.toString())
      onConfirm(newValue)
    },
    isConfirmButtonEnabled = true,
  ) {
    SukkoOutlinedTextField(
      modifier = Modifier.fillMaxWidth().padding(horizontal = Sizes.large),
      lineLimits = TextFieldLineLimits.SingleLine,
      state = textFieldState,
    )
  }
}

@Composable
private fun ScriptString(
  onDismiss: () -> Unit,
  onConfirm: (value: ScriptableString.Script) -> Unit,
  initialValue: ScriptableString,
  globals: Globals,
) {
  ScriptEditorSheetContent(
    initialInput =
      remember { if (initialValue is ScriptableString.Script) initialValue.script else "" },
    globals = globals,
    onDismiss = onDismiss,
    onConfirm = { onConfirm(ScriptableString.Script(it)) },
  )
}

@Composable
private fun GlobalString(
  onDismiss: () -> Unit,
  onConfirm: (value: ScriptableString.Global) -> Unit,
  initialValue: ScriptableString,
  globals: List<GlobalValue.GlobalString>,
) {
  GlobalSelectorSheetContent(
    onDismiss = onDismiss,
    onConfirm = { onConfirm(ScriptableString.Global(it)) },
    initialGlobalId = remember { (initialValue as? ScriptableString.Global)?.id },
    globals = globals,
  )
}

@Preview
@Composable
private fun PreviewScriptString() {
  ScriptString(
    onDismiss = {},
    onConfirm = {},
    initialValue = ScriptableString.Script("script"),
    globals = Globals(),
  )
}

@Preview
@Composable
private fun PreviewGlobalString() {
  GlobalString(
    onDismiss = {},
    onConfirm = {},
    initialValue = ScriptableString.Fixed("fixed text"),
    globals =
      remember {
        listOf(
          GlobalValue.GlobalString(
            id = 0,
            label = "Item 1",
            initialValue = ScriptableString.Fixed("fixed text"),
          ),
          GlobalValue.GlobalString(
            id = 1,
            label = "Item 2",
            initialValue = ScriptableString.Script("some script"),
          ),
        )
      },
  )
}
