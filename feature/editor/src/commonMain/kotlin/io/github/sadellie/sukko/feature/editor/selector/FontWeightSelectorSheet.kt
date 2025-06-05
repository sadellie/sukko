package io.github.sadellie.sukko.feature.editor.selector

import androidx.compose.foundation.layout.fillMaxSize
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
import io.github.sadellie.sukko.core.designsystem.theme.Sizes
import io.github.sadellie.sukko.core.model.basic.ScriptableDouble
import io.github.sadellie.sukko.core.ui.InputTransformationDouble
import io.github.sadellie.sukko.core.ui.SheetContentWithButtons
import io.github.sadellie.sukko.core.ui.SukkoOutlinedTextField
import io.github.sadellie.sukko.feature.editor.selector.scripteditor.OutOfRangeErrorMessage
import io.github.sadellie.sukko.feature.editor.selector.scripteditor.ScriptEditor
import io.github.sadellie.sukko.feature.editor.selector.scripteditor.SuccessMessage
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.common_cancel
import io.github.sadellie.sukko.resources.common_confirm
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun FontWeightSelectorSheetContent(
  onDismissRequest: () -> Unit,
  onValueSelected: (newValue: ScriptableDouble) -> Unit,
  value: ScriptableDouble,
  range: ClosedRange<Double>,
  dismissLabel: String = stringResource(Res.string.common_cancel),
  confirmLabel: String = stringResource(Res.string.common_confirm),
) {
  var inputMode by rememberSaveable { mutableStateOf(DefaultInputMode.initialMode(value)) }
  SelectorSheetTemplateContent(
    currentInputMode = inputMode,
    inputModes = remember { DefaultInputMode.entries },
    onInputModeUpdate = { inputMode = it },
  ) { currentMode ->
    when (currentMode) {
      DefaultInputMode.FIXED ->
        FixedWeight(
          onDismiss = onDismissRequest,
          onConfirm = onValueSelected,
          initialValue = value,
          range = range,
          dismissLabel = dismissLabel,
          confirmLabel = confirmLabel,
        )

      DefaultInputMode.SCRIPT ->
        ScriptWeight(
          onDismiss = onDismissRequest,
          onConfirm = onValueSelected,
          initialValue = value,
          range = range,
          dismissLabel = dismissLabel,
          confirmLabel = confirmLabel,
        )
    }
  }
}

@Composable
private fun FixedWeight(
  onDismiss: () -> Unit,
  onConfirm: (value: ScriptableDouble.Fixed) -> Unit,
  initialValue: ScriptableDouble,
  range: ClosedRange<Double>,
  dismissLabel: String,
  confirmLabel: String,
) {
  val textFieldState =
    rememberTextFieldState(
      initialText =
        if (initialValue is ScriptableDouble.Fixed) initialValue.value.toString() else ""
    )

  val inputTransformation = InputTransformationDouble(valueRange = range, allowFraction = false)
  val currentValue =
    remember(textFieldState.text) { inputTransformation.toValue(textFieldState.text) }
  SheetContentWithButtons(
    onDismiss = onDismiss,
    onConfirm = { if (currentValue != null) onConfirm(ScriptableDouble.Fixed(currentValue)) },
    isConfirmButtonEnabled = currentValue != null,
    dismissLabel = dismissLabel,
    confirmLabel = confirmLabel,
  ) {
    SukkoOutlinedTextField(
      state = textFieldState,
      modifier = Modifier.fillMaxWidth().padding(horizontal = Sizes.large),
      inputTransformation = inputTransformation,
      lineLimits = TextFieldLineLimits.SingleLine,
    )
  }
}

@Composable
private fun ScriptWeight(
  onDismiss: () -> Unit,
  onConfirm: (value: ScriptableDouble.Script) -> Unit,
  initialValue: ScriptableDouble,
  range: ClosedRange<Double>,
  dismissLabel: String,
  confirmLabel: String,
) {
  val textFieldState =
    rememberTextFieldState(if (initialValue is ScriptableDouble.Script) initialValue.script else "")
  SheetContentWithButtons(
    onDismiss = onDismiss,
    onConfirm = {
      val newValue = ScriptableDouble.Script(textFieldState.text.toString())
      onConfirm(newValue)
    },
    isConfirmButtonEnabled = true,
    dismissLabel = dismissLabel,
    confirmLabel = confirmLabel,
  ) {
    ScriptEditor(
      modifier = Modifier.fillMaxSize().padding(horizontal = Sizes.large),
      textFieldState = textFieldState,
      produceScriptable = { ScriptableDouble.Script(it) },
    ) { value ->
      if (value in range) SuccessMessage(value.toString()) else OutOfRangeErrorMessage(range)
    }
  }
}
