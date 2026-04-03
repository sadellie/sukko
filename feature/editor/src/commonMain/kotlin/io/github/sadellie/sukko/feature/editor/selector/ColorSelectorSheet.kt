package io.github.sadellie.sukko.feature.editor.selector

import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.allCaps
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.isUnspecified
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import com.composables.core.ModalBottomSheetState
import io.github.sadellie.sukko.core.common.hexToColor
import io.github.sadellie.sukko.core.common.toHex
import io.github.sadellie.sukko.core.designsystem.theme.ListArrangement
import io.github.sadellie.sukko.core.designsystem.theme.Sizes
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.LayerContext
import io.github.sadellie.sukko.core.model.basic.GlobalValue
import io.github.sadellie.sukko.core.model.basic.M3Color
import io.github.sadellie.sukko.core.model.basic.ScriptableColor
import io.github.sadellie.sukko.core.ui.ModalBottomSheet2
import io.github.sadellie.sukko.core.ui.SheetContentWithButtons
import io.github.sadellie.sukko.core.ui.SukkoOutlinedTextField
import io.github.sadellie.sukko.core.ui.hide
import io.github.sadellie.sukko.core.ui.listedShape
import io.github.sadellie.sukko.feature.editor.selector.scripteditor.ScriptEditor
import io.github.sadellie.sukko.feature.editor.selector.scripteditor.SuccessMessage
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.common_cancel
import io.github.sadellie.sukko.resources.common_confirm
import io.github.sadellie.sukko.resources.common_not_selected
import io.github.sadellie.sukko.resources.editor_selector_input_mode_fixed
import io.github.sadellie.sukko.resources.editor_selector_input_mode_global
import io.github.sadellie.sukko.resources.editor_selector_input_mode_material_you
import io.github.sadellie.sukko.resources.editor_selector_input_mode_script
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ColorSelectorSheet(
  state: ModalBottomSheetState,
  onValueSelected: (newValue: ScriptableColor) -> Unit,
  value: ScriptableColor,
  globals: List<GlobalValue.GlobalColor>,
) {
  ModalBottomSheet2(state) {
    ColorSelectorSheetContent(
      onDismissRequest = state::hide,
      onValueSelected = onValueSelected,
      value = value,
      globals = globals,
    )
  }
}

@Composable
fun ColorSelectorSheetContent(
  onDismissRequest: () -> Unit,
  onValueSelected: (newValue: ScriptableColor) -> Unit,
  value: ScriptableColor,
  globals: List<GlobalValue.GlobalColor>,
  dismissLabel: String = stringResource(Res.string.common_cancel),
  confirmLabel: String = stringResource(Res.string.common_confirm),
) {
  var currentInputMode by rememberSaveable { mutableStateOf(ColorInputMode.initialMode(value)) }
  SelectorSheetTemplateContent(
    currentInputMode = currentInputMode,
    inputModes = remember { ColorInputMode.entries },
    onInputModeUpdate = { currentInputMode = it },
  ) { inputMode ->
    when (inputMode) {
      ColorInputMode.UNSPECIFIED ->
        UnspecifiedColor(
          onDismiss = onDismissRequest,
          onConfirm = { onValueSelected(ScriptableColor.FixedCustom(Color.Unspecified)) },
          dismissLabel = dismissLabel,
          confirmLabel = confirmLabel,
        )

      ColorInputMode.M3 ->
        M3Color(
          onDismiss = onDismissRequest,
          onConfirm = onValueSelected,
          initialValue = value,
          dismissLabel = dismissLabel,
          confirmLabel = confirmLabel,
        )

      ColorInputMode.FIXED ->
        CustomColor(
          onDismiss = onDismissRequest,
          onConfirm = onValueSelected,
          initialValue = value,
          dismissLabel = dismissLabel,
          confirmLabel = confirmLabel,
        )

      ColorInputMode.SCRIPT ->
        ScriptColor(
          onDismiss = onDismissRequest,
          onConfirm = onValueSelected,
          initialValue = value,
          dismissLabel = dismissLabel,
          confirmLabel = confirmLabel,
        )
      ColorInputMode.GLOBAL ->
        GlobalColor(
          onDismiss = onDismissRequest,
          onConfirm = onValueSelected,
          initialValue = value,
          dismissLabel = dismissLabel,
          confirmLabel = confirmLabel,
          globals = globals,
        )
    }
  }
}

@Composable
private fun UnspecifiedColor(
  onDismiss: () -> Unit,
  onConfirm: () -> Unit,
  dismissLabel: String,
  confirmLabel: String,
) {
  SheetContentWithButtons(
    onDismiss = onDismiss,
    onConfirm = onConfirm,
    dismissLabel = dismissLabel,
    confirmLabel = confirmLabel,
  ) {}
}

@Composable
private fun M3Color(
  onDismiss: () -> Unit,
  onConfirm: (ScriptableColor.FixedM3) -> Unit,
  initialValue: ScriptableColor,
  dismissLabel: String,
  confirmLabel: String,
) {
  var currentValue by remember {
    mutableStateOf(if (initialValue is ScriptableColor.FixedM3) initialValue.value else null)
  }
  SheetContentWithButtons(
    onDismiss = onDismiss,
    onConfirm = { currentValue?.let { onConfirm(ScriptableColor.FixedM3(it)) } },
    isConfirmButtonEnabled = currentValue != null,
    dismissLabel = dismissLabel,
    confirmLabel = confirmLabel,
  ) {
    val m3Colors = remember { M3Color.entries }
    LazyColumn(
      verticalArrangement = ListArrangement,
      modifier = Modifier.fillMaxWidth(),
      contentPadding = PaddingValues(horizontal = Sizes.large),
    ) {
      itemsIndexed(items = m3Colors, key = { _, m3Color -> m3Color }) { index, m3Color ->
        ColorsListItem(
          modifier = Modifier,
          color = m3Color.extractFromScheme(MaterialTheme.colorScheme),
          isSelected = m3Color == currentValue,
          onClick = { currentValue = m3Color },
          label = stringResource(m3Color.displayName),
          shape = ListItemDefaults.listedShape(index, m3Colors.size),
        )
      }
    }
  }
}

@Composable
private fun CustomColor(
  onDismiss: () -> Unit,
  onConfirm: (ScriptableColor.FixedCustom) -> Unit,
  initialValue: ScriptableColor,
  dismissLabel: String,
  confirmLabel: String,
) {
  val textFieldState =
    rememberTextFieldState(
      initialText =
        if (initialValue is ScriptableColor.FixedCustom) initialValue.value.toHex() else ""
    )
  val currentColor =
    remember(textFieldState.text) {
      val text = textFieldState.text.toString()
      if (text.isBlank()) return@remember null
      try {
        text.hexToColor()
      } catch (_: Exception) {
        null
      }
    }
  SheetContentWithButtons(
    onDismiss = onDismiss,
    onConfirm = { if (currentColor != null) onConfirm(ScriptableColor.FixedCustom(currentColor)) },
    isConfirmButtonEnabled = currentColor != null,
    dismissLabel = dismissLabel,
    confirmLabel = confirmLabel,
  ) {
    Column(
      modifier = Modifier.padding(horizontal = Sizes.large),
      verticalArrangement = Arrangement.spacedBy(Sizes.large),
    ) {
      val currentColorAnimated =
        animateColorAsState(
          targetValue = currentColor ?: Color.Unspecified,
          animationSpec = MaterialTheme.motionScheme.fastEffectsSpec(),
        )
      Box(
        Modifier.clip(MaterialTheme.shapes.large)
          .background(currentColorAnimated.value)
          .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.large)
          .fillMaxWidth()
          .height(Sizes.extraLarge)
      )

      SukkoOutlinedTextField(
        state = textFieldState,
        modifier = Modifier.fillMaxWidth(),
        prefix = { Text("#") },
        lineLimits = TextFieldLineLimits.SingleLine,
        inputTransformation =
          InputTransformation.allCaps(Locale.current).maxLength(MAX_HEX_COLOR_LENGTH),
      )
    }
  }
}

@Composable
private fun ScriptColor(
  onDismiss: () -> Unit,
  onConfirm: (ScriptableColor.Script) -> Unit,
  initialValue: ScriptableColor,
  dismissLabel: String,
  confirmLabel: String,
) {
  val textFieldState =
    rememberTextFieldState(if (initialValue is ScriptableColor.Script) initialValue.script else "")
  SheetContentWithButtons(
    onDismiss = onDismiss,
    onConfirm = {
      val newValue = ScriptableColor.Script(textFieldState.text.toString())
      onConfirm(newValue)
    },
    isConfirmButtonEnabled = true,
    dismissLabel = dismissLabel,
    confirmLabel = confirmLabel,
  ) {
    ScriptEditor(
      modifier = Modifier.fillMaxSize().padding(horizontal = Sizes.large),
      textFieldState = textFieldState,
      produceScriptable = { ScriptableColor.Script(it) },
    ) { value ->
      SuccessMessage("#${value.toHex()}")
    }
  }
}

@Composable
private fun GlobalColor(
  onDismiss: () -> Unit,
  onConfirm: (value: ScriptableColor.Global) -> Unit,
  initialValue: ScriptableColor,
  globals: List<GlobalValue.GlobalColor>,
  dismissLabel: String,
  confirmLabel: String,
) {
  GlobalSelectorSheetContent(
    onDismiss = onDismiss,
    onConfirm = { onConfirm(ScriptableColor.Global(it)) },
    initialGlobalId = remember { (initialValue as? ScriptableColor.Global)?.id },
    globals = globals,
    dismissLabel = dismissLabel,
    confirmLabel = confirmLabel,
  )
}

private enum class ColorInputMode(override val displayName: StringResource) : InputMode {
  UNSPECIFIED(displayName = Res.string.common_not_selected),
  M3(displayName = Res.string.editor_selector_input_mode_material_you),
  FIXED(displayName = Res.string.editor_selector_input_mode_fixed),
  SCRIPT(displayName = Res.string.editor_selector_input_mode_script),
  GLOBAL(displayName = Res.string.editor_selector_input_mode_global);

  companion object {
    fun initialMode(scriptableColor: ScriptableColor): ColorInputMode =
      when (scriptableColor) {
        is ScriptableColor.FixedCustom if scriptableColor.value.isUnspecified -> UNSPECIFIED
        is ScriptableColor.FixedCustom -> FIXED
        is ScriptableColor.FixedM3 -> M3
        is ScriptableColor.Script -> SCRIPT
        is ScriptableColor.Global -> GLOBAL
      }
  }
}

@Composable
private fun ColorsListItem(
  modifier: Modifier,
  color: Color,
  label: String,
  isSelected: Boolean,
  onClick: () -> Unit,
  shape: Shape,
) {
  val selectedTransition = updateTransition(isSelected)
  val backgroundColor =
    selectedTransition.animateColor {
      if (it) MaterialTheme.colorScheme.primaryContainer
      else MaterialTheme.colorScheme.surfaceBright
    }

  val textColor =
    selectedTransition.animateColor {
      if (it) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
    }

  Row(
    modifier =
      modifier.clip(shape).background(backgroundColor.value).clickable { onClick() }.padding(8.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(Sizes.large),
  ) {
    Box(
      modifier =
        Modifier.background(color, MaterialTheme.shapes.medium)
          .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.medium)
          .size(46.dp)
    )
    Text(modifier = Modifier.weight(1f), text = label, color = textColor.value)
  }
}

@Composable
internal fun produceColor(
  scriptableColor: ScriptableColor,
  layerContext: LayerContext,
  globals: Globals,
) =
  produceState(initialValue = Color.Unspecified, key1 = scriptableColor) {
    value =
      try {
        scriptableColor.getValue(layerContext, globals)
      } catch (e: Exception) {
        Logger.e(throwable = e, tag = TAG) { "Failed to produce color" }
        Color.Unspecified
      }
  }

private const val TAG = "ColorSelectorSheet"

/** longest hex color that can be written. For example: FFABC123 */
private const val MAX_HEX_COLOR_LENGTH = 8

@Composable
@Preview
private fun PreviewColorSelectorSheetContentUnspecified() {
  ColorSelectorSheetContent(
    onDismissRequest = {},
    onValueSelected = {},
    value = ScriptableColor.FixedCustom(Color.Unspecified),
    globals = emptyList(),
  )
}

@Composable
@Preview
private fun PreviewColorSelectorSheetContentFixedCustom() {
  ColorSelectorSheetContent(
    onDismissRequest = {},
    onValueSelected = {},
    value = ScriptableColor.FixedCustom(Color.Red),
    globals = emptyList(),
  )
}

@Composable
@Preview
private fun PreviewColorSelectorSheetContentFixedM3() {
  ColorSelectorSheetContent(
    onDismissRequest = {},
    onValueSelected = {},
    value = ScriptableColor.FixedM3(M3Color.ON_PRIMARY),
    globals = emptyList(),
  )
}

@Composable
@Preview
private fun PreviewColorSelectorSheetContentGlobal() {
  ColorSelectorSheetContent(
    onDismissRequest = {},
    onValueSelected = {},
    value = ScriptableColor.Global(1),
    globals = List(5) { GlobalValue.GlobalColor(id = it.toLong(), label = "Global $it") },
  )
}
