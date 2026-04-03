package io.github.sadellie.sukko.feature.editor.selector

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.composables.core.ModalBottomSheetState
import io.github.sadellie.sukko.core.designsystem.Preview2
import io.github.sadellie.sukko.core.designsystem.theme.Sizes
import io.github.sadellie.sukko.core.model.basic.GlobalValue
import io.github.sadellie.sukko.core.model.basic.ScriptableDp
import io.github.sadellie.sukko.core.ui.InputTransformationDp
import io.github.sadellie.sukko.core.ui.ModalBottomSheet2
import io.github.sadellie.sukko.core.ui.SheetContentWithButtons
import io.github.sadellie.sukko.core.ui.SukkoOutlinedTextField
import io.github.sadellie.sukko.core.ui.hide
import io.github.sadellie.sukko.feature.editor.selector.scripteditor.OutOfRangeErrorMessage
import io.github.sadellie.sukko.feature.editor.selector.scripteditor.ScriptEditor
import io.github.sadellie.sukko.feature.editor.selector.scripteditor.SuccessMessage
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.editor_selector_dp_suffix
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FixedDpSelectorSheet(
  state: ModalBottomSheetState,
  onValueSelected: (newValue: Dp) -> Unit,
  value: Dp,
  range: ClosedRange<Dp> = DpSelectorSheetDefaults.valueRangeUnspecified,
) {
  ModalBottomSheet2(state) {
    FixedDp(
      onDismiss = state::hide,
      onConfirm = { onValueSelected(it.value) },
      initialValue = remember { ScriptableDp.Fixed(value) },
      range = range,
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DpSelectorSheet(
  state: ModalBottomSheetState,
  onValueSelected: (newValue: ScriptableDp) -> Unit,
  value: ScriptableDp,
  globals: List<GlobalValue.GlobalDp>,
  range: ClosedRange<Dp> = DpSelectorSheetDefaults.valueRangeUnspecified,
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
        FixedDp(
          onDismiss = state::hide,
          onConfirm = onValueSelected,
          initialValue = value,
          range = range,
        )
      DefaultInputMode2.SCRIPT ->
        ScriptDp(
          onDismiss = state::hide,
          onConfirm = onValueSelected,
          initialValue = value,
          range = range,
        )
      DefaultInputMode2.GLOBAL ->
        GlobalDp(
          onDismiss = state::hide,
          onConfirm = onValueSelected,
          initialValue = value,
          globals = globals,
        )
    }
  }
}

@Composable
private fun FixedDp(
  onDismiss: () -> Unit,
  onConfirm: (value: ScriptableDp.Fixed) -> Unit,
  initialValue: ScriptableDp,
  range: ClosedRange<Dp>,
) {
  val textFieldState =
    rememberTextFieldState(
      initialText =
        if (initialValue is ScriptableDp.Fixed) initialValue.value.value.toString() else ""
    )
  val inputTransformation = InputTransformationDp(range)
  val currentValue =
    remember(textFieldState.text) { inputTransformation.toValue(textFieldState.text) }
  SheetContentWithButtons(
    onDismiss = onDismiss,
    onConfirm = { if (currentValue != null) onConfirm(ScriptableDp.Fixed(currentValue)) },
    isConfirmButtonEnabled = currentValue != null,
  ) {
    SukkoOutlinedTextField(
      state = textFieldState,
      modifier = Modifier.fillMaxWidth().padding(horizontal = Sizes.large),
      inputTransformation = inputTransformation,
      lineLimits = TextFieldLineLimits.SingleLine,
      suffix = { Text(stringResource(Res.string.editor_selector_dp_suffix)) },
    )
  }
}

@Composable
private fun ScriptDp(
  onDismiss: () -> Unit,
  onConfirm: (value: ScriptableDp.Script) -> Unit,
  initialValue: ScriptableDp,
  range: ClosedRange<Dp>,
) {
  val textFieldState =
    rememberTextFieldState(if (initialValue is ScriptableDp.Script) initialValue.script else "")
  SheetContentWithButtons(
    onDismiss = onDismiss,
    onConfirm = {
      val newValue = ScriptableDp.Script(textFieldState.text.toString())
      onConfirm(newValue)
    },
    isConfirmButtonEnabled = true,
  ) {
    ScriptEditor(
      modifier = Modifier.fillMaxSize().padding(horizontal = Sizes.large),
      textFieldState = textFieldState,
      produceScriptable = { ScriptableDp.Script(it) },
    ) { value ->
      if (value in range) SuccessMessage(value.value.toString()) else OutOfRangeErrorMessage(range)
    }
  }
}

@Composable
private fun GlobalDp(
  onDismiss: () -> Unit,
  onConfirm: (value: ScriptableDp.Global) -> Unit,
  initialValue: ScriptableDp,
  globals: List<GlobalValue.GlobalDp>,
) {
  GlobalSelectorSheetContent(
    onDismiss = onDismiss,
    onConfirm = { onConfirm(ScriptableDp.Global(it)) },
    initialGlobalId = remember { (initialValue as? ScriptableDp.Global)?.id },
    globals = globals,
  )
}

object DpSelectorSheetDefaults {
  val valueRangeUnspecified = -Dp.Infinity..Dp.Infinity
}

@Preview
@Composable
private fun PreviewScriptableDpEditor() = Preview2 {
  Box(Modifier.background(MaterialTheme.colorScheme.surfaceContainerLow)) {
    ScriptDp(
      onDismiss = {},
      onConfirm = {},
      initialValue = ScriptableDp.Script("2 + 6"),
      range = 0.dp..100.dp,
    )
  }
}
