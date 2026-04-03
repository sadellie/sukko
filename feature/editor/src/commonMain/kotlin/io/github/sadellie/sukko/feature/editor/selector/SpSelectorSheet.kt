package io.github.sadellie.sukko.feature.editor.selector

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.composables.core.ModalBottomSheetState
import io.github.sadellie.sukko.core.designsystem.theme.Sizes
import io.github.sadellie.sukko.core.model.basic.GlobalValue
import io.github.sadellie.sukko.core.model.basic.ScriptableSp
import io.github.sadellie.sukko.core.ui.InputTransformationFloat
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
import io.github.sadellie.sukko.resources.editor_selector_sp_suffix
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SpSelectorSheet(
  state: ModalBottomSheetState,
  onValueSelected: (newValue: ScriptableSp) -> Unit,
  value: ScriptableSp,
  globals: List<GlobalValue.GlobalSp>,
  range: ClosedRange<Float> = SpSelectorSheetDefaults.valueRangeUnspecified,
) {
  ModalBottomSheet2(state) {
    SpSelectorSheetContent(
      onDismissRequest = state::hide,
      onValueSelected = onValueSelected,
      value = value,
      range = range,
      globals = globals,
    )
  }
}

@Composable
internal fun SpSelectorSheetContent(
  onDismissRequest: () -> Unit,
  onValueSelected: (ScriptableSp) -> Unit,
  value: ScriptableSp,
  range: ClosedRange<Float>,
  globals: List<GlobalValue.GlobalSp>,
  dismissLabel: String = stringResource(Res.string.common_cancel),
  confirmLabel: String = stringResource(Res.string.common_confirm),
) {
  var currentInputMode by rememberSaveable { mutableStateOf(DefaultInputMode2.initialMode(value)) }
  SelectorSheetTemplateContent(
    currentInputMode = currentInputMode,
    inputModes = remember { DefaultInputMode2.entries },
    onInputModeUpdate = { currentInputMode = it },
  ) { currentMode ->
    when (currentMode) {
      DefaultInputMode2.FIXED ->
        FixedSp(
          onDismiss = onDismissRequest,
          onConfirm = onValueSelected,
          initialValue = value,
          range = range,
          dismissLabel = dismissLabel,
          confirmLabel = confirmLabel,
        )

      DefaultInputMode2.SCRIPT ->
        ScriptSp(
          onDismiss = onDismissRequest,
          onConfirm = onValueSelected,
          initialValue = value,
          range = range,
          dismissLabel = dismissLabel,
          confirmLabel = confirmLabel,
        )
      DefaultInputMode2.GLOBAL ->
        GlobalSp(
          onDismiss = onDismissRequest,
          onConfirm = onValueSelected,
          initialValue = value,
          globals = globals,
        )
    }
  }
}

@Composable
private fun FixedSp(
  onDismiss: () -> Unit,
  onConfirm: (value: ScriptableSp.Fixed) -> Unit,
  initialValue: ScriptableSp,
  range: ClosedRange<Float>,
  dismissLabel: String,
  confirmLabel: String,
) {
  val textFieldState =
    rememberTextFieldState(
      initialText =
        if (initialValue is ScriptableSp.Fixed) initialValue.value.value.toString() else ""
    )
  val inputTransformation = InputTransformationFloat(range)
  val currentValue =
    remember(textFieldState.text) { inputTransformation.toValue(textFieldState.text)?.sp }
  SheetContentWithButtons(
    onDismiss = onDismiss,
    onConfirm = { if (currentValue != null) onConfirm(ScriptableSp.Fixed(currentValue)) },
    isConfirmButtonEnabled = currentValue != null,
    dismissLabel = dismissLabel,
    confirmLabel = confirmLabel,
  ) {
    SukkoOutlinedTextField(
      state = textFieldState,
      modifier = Modifier.fillMaxWidth().padding(horizontal = Sizes.large),
      lineLimits = TextFieldLineLimits.SingleLine,
      inputTransformation = inputTransformation,
      suffix = { Text(stringResource(Res.string.editor_selector_sp_suffix)) },
    )
  }
}

@Composable
private fun ScriptSp(
  onDismiss: () -> Unit,
  onConfirm: (value: ScriptableSp.Script) -> Unit,
  initialValue: ScriptableSp,
  range: ClosedRange<Float>,
  dismissLabel: String,
  confirmLabel: String,
) {
  val textFieldState =
    rememberTextFieldState(if (initialValue is ScriptableSp.Script) initialValue.script else "")
  SheetContentWithButtons(
    onDismiss = onDismiss,
    onConfirm = {
      val newValue = ScriptableSp.Script(textFieldState.text.toString())
      onConfirm(newValue)
    },
    isConfirmButtonEnabled = true,
    dismissLabel = dismissLabel,
    confirmLabel = confirmLabel,
  ) {
    ScriptEditor(
      modifier = Modifier.fillMaxSize().padding(horizontal = Sizes.large),
      textFieldState = textFieldState,
      produceScriptable = { ScriptableSp.Script(it) },
    ) { value ->
      if (value.value in range) SuccessMessage(value.value.toString())
      else OutOfRangeErrorMessage(range)
    }
  }
}

@Composable
private fun GlobalSp(
  onDismiss: () -> Unit,
  onConfirm: (value: ScriptableSp.Global) -> Unit,
  initialValue: ScriptableSp,
  globals: List<GlobalValue.GlobalSp>,
) {
  GlobalSelectorSheetContent(
    onDismiss = onDismiss,
    onConfirm = { onConfirm(ScriptableSp.Global(it)) },
    initialGlobalId = remember { (initialValue as? ScriptableSp.Global)?.id },
    globals = globals,
  )
}

object SpSelectorSheetDefaults {
  val valueRangeUnspecified = Float.NEGATIVE_INFINITY..Float.POSITIVE_INFINITY
}

@Preview
@Composable
private fun PreviewScriptSp() {
  Surface(color = MaterialTheme.colorScheme.surfaceContainer) {
    ScriptSp(
      onDismiss = {},
      onConfirm = {},
      initialValue = ScriptableSp.Script("50"),
      range = 0f..50f,
      dismissLabel = stringResource(Res.string.common_cancel),
      confirmLabel = stringResource(Res.string.common_confirm),
    )
  }
}
