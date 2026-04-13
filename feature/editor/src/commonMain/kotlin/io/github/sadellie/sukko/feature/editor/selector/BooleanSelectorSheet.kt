package io.github.sadellie.sukko.feature.editor.selector

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.composables.core.ModalBottomSheetState
import io.github.sadellie.sukko.core.designsystem.theme.ListArrangement
import io.github.sadellie.sukko.core.designsystem.theme.Sizes
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.basic.GlobalValue
import io.github.sadellie.sukko.core.model.basic.ScriptableBoolean
import io.github.sadellie.sukko.core.ui.ListItem2
import io.github.sadellie.sukko.core.ui.SheetContentWithButtons
import io.github.sadellie.sukko.core.ui.firstShapes
import io.github.sadellie.sukko.core.ui.hide
import io.github.sadellie.sukko.core.ui.lastShapes
import io.github.sadellie.sukko.feature.editor.selector.scripteditor.ScriptEditorSheetContent
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.core_model_scriptable_boolean_false
import io.github.sadellie.sukko.resources.core_model_scriptable_boolean_true
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BooleanSelectorSheet(
  state: ModalBottomSheetState,
  onValueSelected: (newValue: ScriptableBoolean) -> Unit,
  value: ScriptableBoolean,
  globals: Globals,
) {
  var currentInputMode by rememberSaveable { mutableStateOf(DefaultInputMode.initialMode(value)) }

  SelectorSheetTemplate(
    state = state,
    currentInputMode = currentInputMode,
    onInputModeUpdate = { currentInputMode = it },
    inputModes = remember { DefaultInputMode.entries },
  ) { currentMode ->
    when (currentMode) {
      DefaultInputMode.FIXED ->
        FixedBoolean(onDismiss = state::hide, onConfirm = onValueSelected, initialValue = value)
      DefaultInputMode.SCRIPT ->
        ScriptBoolean(
          onDismiss = state::hide,
          onConfirm = onValueSelected,
          initialValue = value,
          globals = globals,
        )
      DefaultInputMode.GLOBAL ->
        GlobalBoolean(
          onDismiss = state::hide,
          onConfirm = onValueSelected,
          initialValue = value,
          globals = globals.booleans,
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
        content = { Text(stringResource(Res.string.core_model_scriptable_boolean_true)) },
        leadingContent = {
          RadioButton(selected = currentValue, onClick = { currentValue = true })
        },
        onClick = { currentValue = true },
        shapes = ListItemDefaults.firstShapes,
      )
      ListItem2(
        content = { Text(stringResource(Res.string.core_model_scriptable_boolean_false)) },
        leadingContent = {
          RadioButton(selected = !currentValue, onClick = { currentValue = false })
        },
        onClick = { currentValue = false },
        shapes = ListItemDefaults.lastShapes,
      )
    }
  }
}

@Composable
private fun ScriptBoolean(
  onDismiss: () -> Unit,
  onConfirm: (value: ScriptableBoolean.Script) -> Unit,
  initialValue: ScriptableBoolean,
  globals: Globals,
) {
  ScriptEditorSheetContent(
    initialInput =
      remember { if (initialValue is ScriptableBoolean.Script) initialValue.script else "" },
    globals = globals,
    onDismiss = onDismiss,
    onConfirm = { onConfirm(ScriptableBoolean.Script(it)) },
  )
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
