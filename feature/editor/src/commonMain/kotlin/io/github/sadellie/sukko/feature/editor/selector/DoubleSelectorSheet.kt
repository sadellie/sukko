package io.github.sadellie.sukko.feature.editor.selector

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
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
import io.github.sadellie.sukko.core.model.basic.GlobalValue
import io.github.sadellie.sukko.core.model.basic.ScriptableDouble
import io.github.sadellie.sukko.core.ui.InputTransformationDouble
import io.github.sadellie.sukko.core.ui.ModalBottomSheet2
import io.github.sadellie.sukko.core.ui.SheetContentWithButtons
import io.github.sadellie.sukko.core.ui.SukkoOutlinedTextField
import io.github.sadellie.sukko.core.ui.hide
import io.github.sadellie.sukko.feature.editor.selector.scripteditor.OutOfRangeErrorMessage
import io.github.sadellie.sukko.feature.editor.selector.scripteditor.ScriptEditor
import io.github.sadellie.sukko.feature.editor.selector.scripteditor.SuccessMessage
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.common_cancel
import io.github.sadellie.sukko.resources.common_confirm
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DoubleSelectorSheet(
  state: ModalBottomSheetState,
  onValueSelected: (newValue: ScriptableDouble) -> Unit,
  value: ScriptableDouble,
  globals: List<GlobalValue.GlobalDouble>,
  allowFraction: Boolean,
  range: ClosedRange<Double> = DoubleSelectorSheetDefaults.valueRangeUnspecified,
) {
  ModalBottomSheet2(state) {
    DoubleSelectorSheetContent(
      onDismissRequest = state::hide,
      onValueSelected = onValueSelected,
      value = value,
      range = range,
      globals = globals,
      allowFraction = allowFraction,
    )
  }
}

@Composable
internal fun DoubleSelectorSheetContent(
  onDismissRequest: () -> Unit,
  onValueSelected: (ScriptableDouble) -> Unit,
  value: ScriptableDouble,
  range: ClosedRange<Double>,
  globals: List<GlobalValue.GlobalDouble>,
  allowFraction: Boolean,
  dismissLabel: String = stringResource(Res.string.common_cancel),
  confirmLabel: String = stringResource(Res.string.common_confirm),
) {
  var currentInputMode by rememberSaveable { mutableStateOf(DefaultInputMode2.initialMode(value)) }
  SelectorSheetTemplateContent(
    currentInputMode = currentInputMode,
    onInputModeUpdate = { currentInputMode = it },
    inputModes = remember { DefaultInputMode2.entries },
  ) { currentMode ->
    when (currentMode) {
      DefaultInputMode2.FIXED ->
        FixedDouble(
          onDismiss = onDismissRequest,
          onConfirm = onValueSelected,
          initialValue = value,
          allowFraction = allowFraction,
          range = range,
          dismissLabel = dismissLabel,
          confirmLabel = confirmLabel,
        )
      DefaultInputMode2.SCRIPT ->
        ScriptDouble(
          onDismiss = onDismissRequest,
          onConfirm = onValueSelected,
          initialValue = value,
          range = range,
          dismissLabel = dismissLabel,
          confirmLabel = confirmLabel,
        )

      DefaultInputMode2.GLOBAL ->
        GlobalDouble(
          onDismiss = onDismissRequest,
          onConfirm = onValueSelected,
          initialValue = value,
          globals = globals,
          dismissLabel = dismissLabel,
          confirmLabel = confirmLabel,
        )
    }
  }
}

@Composable
internal fun FixedDouble(
  onDismiss: () -> Unit,
  onConfirm: (value: ScriptableDouble.Fixed) -> Unit,
  initialValue: ScriptableDouble,
  range: ClosedRange<Double>,
  allowFraction: Boolean,
  dismissLabel: String = stringResource(Res.string.common_cancel),
  confirmLabel: String = stringResource(Res.string.common_confirm),
) {
  val textFieldState =
    rememberTextFieldState(
      initialText =
        if (initialValue is ScriptableDouble.Fixed) {
          // input transformation kicks in after state edit, initial needs processing
          (if (allowFraction) initialValue.value else initialValue.value.toInt()).toString()
        } else {
          ""
        }
    )
  val inputTransformation = InputTransformationDouble(range, allowFraction)
  val currentValue =
    remember(textFieldState.text) { inputTransformation.toValue(textFieldState.text) }
  SheetContentWithButtons(
    onDismiss = onDismiss,
    onConfirm = { if (currentValue != null) onConfirm(ScriptableDouble.Fixed(currentValue)) },
    dismissLabel = dismissLabel,
    confirmLabel = confirmLabel,
    isConfirmButtonEnabled = currentValue != null,
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
internal fun ScriptDouble(
  onDismiss: () -> Unit,
  onConfirm: (value: ScriptableDouble.Script) -> Unit,
  initialValue: ScriptableDouble,
  range: ClosedRange<Double>,
  dismissLabel: String = stringResource(Res.string.common_cancel),
  confirmLabel: String = stringResource(Res.string.common_confirm),
) {
  val textFieldState =
    rememberTextFieldState(if (initialValue is ScriptableDouble.Script) initialValue.script else "")
  SheetContentWithButtons(
    onDismiss = onDismiss,
    onConfirm = {
      val newValue = ScriptableDouble.Script(textFieldState.text.toString())
      onConfirm(newValue)
    },
    dismissLabel = dismissLabel,
    confirmLabel = confirmLabel,
    isConfirmButtonEnabled = true,
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

@Composable
private fun GlobalDouble(
  onDismiss: () -> Unit,
  onConfirm: (value: ScriptableDouble.Global) -> Unit,
  initialValue: ScriptableDouble,
  globals: List<GlobalValue.GlobalDouble>,
  dismissLabel: String = stringResource(Res.string.common_cancel),
  confirmLabel: String = stringResource(Res.string.common_confirm),
) {
  GlobalSelectorSheetContent(
    onDismiss = onDismiss,
    onConfirm = { onConfirm(ScriptableDouble.Global(it)) },
    initialGlobalId = remember { (initialValue as? ScriptableDouble.Global)?.id },
    globals = globals,
    dismissLabel = dismissLabel,
    confirmLabel = confirmLabel,
  )
}

object DoubleSelectorSheetDefaults {
  val valueRangeUnspecified = Double.NEGATIVE_INFINITY..Double.POSITIVE_INFINITY
}

@Composable
@Preview
private fun PreviewFixedDouble() {
  DoubleSelectorSheetContent(
    onDismissRequest = {},
    onValueSelected = {},
    value = remember { ScriptableDouble.Fixed(123.456) },
    range = DoubleSelectorSheetDefaults.valueRangeUnspecified,
    globals = remember { emptyList() },
    allowFraction = true,
  )
}
