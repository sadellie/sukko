package io.github.sadellie.sukko.feature.editor.selector

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.composables.core.ModalBottomSheetState
import io.github.sadellie.sukko.core.designsystem.theme.ListArrangement
import io.github.sadellie.sukko.core.designsystem.theme.Sizes
import io.github.sadellie.sukko.core.model.basic.GlobalValue
import io.github.sadellie.sukko.core.model.basic.ScriptableBoolean
import io.github.sadellie.sukko.core.ui.ListItem2
import io.github.sadellie.sukko.core.ui.SheetContentWithButtons
import io.github.sadellie.sukko.core.ui.firstShape
import io.github.sadellie.sukko.core.ui.hide
import io.github.sadellie.sukko.core.ui.lastShape
import io.github.sadellie.sukko.feature.editor.selector.scripteditor.ScriptEditor
import io.github.sadellie.sukko.feature.editor.selector.scripteditor.SuccessMessage
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.core_model_scriptable_boolean_false
import io.github.sadellie.sukko.resources.core_model_scriptable_boolean_true
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BooleanSelectorSheet(
  state: ModalBottomSheetState,
  onValueSelected: (newValue: ScriptableBoolean) -> Unit,
  value: ScriptableBoolean,
  globals: List<GlobalValue.GlobalBoolean>,
) {
  var currentInputMode by rememberSaveable { mutableStateOf(DefaultInputMode2.initialMode(value)) }

  SelectorSheetTemplate(
    state = state,
    currentInputMode = currentInputMode,
    onInputModeUpdate = { currentInputMode = it },
    inputModes = remember { DefaultInputMode2.entries },
  ) { currentMode ->
    when (currentMode) {
      DefaultInputMode2.FIXED ->
        FixedBoolean(onDismiss = state::hide, onConfirm = onValueSelected, initialValue = value)
      DefaultInputMode2.SCRIPT ->
        ScriptBoolean(onDismiss = state::hide, onConfirm = onValueSelected, initialValue = value)
      DefaultInputMode2.GLOBAL ->
        GlobalBoolean(
          onDismiss = state::hide,
          onConfirm = onValueSelected,
          initialValue = value,
          globals = globals,
        )
    }
  }
}

@Composable
private fun FixedBoolean(
  onDismiss: () -> Unit,
  onConfirm: (value: ScriptableBoolean.Fixed) -> Unit,
  initialValue: ScriptableBoolean,
) {
  var currentValue by
    rememberSaveable(initialValue) {
      mutableStateOf(if (initialValue is ScriptableBoolean.Fixed) initialValue.value else false)
    }
  SheetContentWithButtons(
    onDismiss = onDismiss,
    onConfirm = { onConfirm(ScriptableBoolean.Fixed(currentValue)) },
    isConfirmButtonEnabled = true,
  ) {
    Column(
      modifier = Modifier.padding(horizontal = Sizes.large),
      verticalArrangement = ListArrangement,
    ) {
      ListItem2(
        headlineContent = { Text(stringResource(Res.string.core_model_scriptable_boolean_true)) },
        leadingContent = {
          RadioButton(selected = currentValue, onClick = { currentValue = true })
        },
        modifier = Modifier.clickable { currentValue = true },
        shape = ListItemDefaults.firstShape,
      )
      ListItem2(
        headlineContent = { Text(stringResource(Res.string.core_model_scriptable_boolean_false)) },
        leadingContent = {
          RadioButton(selected = !currentValue, onClick = { currentValue = false })
        },
        modifier = Modifier.clickable { currentValue = false },
        shape = ListItemDefaults.lastShape,
      )
    }
  }
}

@Composable
private fun ScriptBoolean(
  onDismiss: () -> Unit,
  onConfirm: (value: ScriptableBoolean.Script) -> Unit,
  initialValue: ScriptableBoolean,
) {
  val textFieldState =
    rememberTextFieldState(
      if (initialValue is ScriptableBoolean.Script) initialValue.script else ""
    )
  SheetContentWithButtons(
    onDismiss = onDismiss,
    onConfirm = {
      val newValue = ScriptableBoolean.Script(textFieldState.text.toString())
      onConfirm(newValue)
    },
    isConfirmButtonEnabled = true,
  ) {
    ScriptEditor(
      modifier = Modifier.fillMaxSize().padding(horizontal = Sizes.large),
      textFieldState = textFieldState,
      produceScriptable = { ScriptableBoolean.Script(it) },
    ) { value ->
      SuccessMessage(
        stringResource(
          if (value) Res.string.core_model_scriptable_boolean_true
          else Res.string.core_model_scriptable_boolean_false
        )
      )
    }
  }
}

@Composable
private fun GlobalBoolean(
  onDismiss: () -> Unit,
  onConfirm: (value: ScriptableBoolean.Global) -> Unit,
  initialValue: ScriptableBoolean,
  globals: List<GlobalValue.GlobalBoolean>,
) {
  GlobalSelectorSheetContent(
    onDismiss = onDismiss,
    onConfirm = { onConfirm(ScriptableBoolean.Global(it)) },
    initialGlobalId = remember { (initialValue as? ScriptableBoolean.Global)?.id },
    globals = globals,
  )
}

@Preview
@Composable
private fun PreviewFixedBoolean() {
  FixedBoolean(onDismiss = {}, onConfirm = {}, initialValue = ScriptableBoolean.Fixed(false))
}

@Preview
@Composable
private fun PreviewScriptBoolean() {
  val script =
    """
    x = 123
    y = 456
    z = "this is a long text to see how does wrapping work in script editor on narrow screens"
    a = 789
  """
      .trimIndent()
  Surface(color = MaterialTheme.colorScheme.surfaceContainer) {
    ScriptBoolean(onDismiss = {}, onConfirm = {}, initialValue = ScriptableBoolean.Script(script))
  }
}
