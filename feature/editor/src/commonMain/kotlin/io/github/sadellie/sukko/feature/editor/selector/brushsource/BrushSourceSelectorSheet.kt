package io.github.sadellie.sukko.feature.editor.selector.brushsource

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.composables.core.ModalBottomSheetState
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.basic.BrushSource
import io.github.sadellie.sukko.core.model.basic.ScriptableColor
import io.github.sadellie.sukko.core.ui.ModalBottomSheet2
import io.github.sadellie.sukko.core.ui.hide
import io.github.sadellie.sukko.feature.editor.selector.ColorSelectorSheetContent
import io.github.sadellie.sukko.feature.editor.selector.SelectorSheetTemplateContent
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.core_model_brush_linear_gradient
import io.github.sadellie.sukko.resources.core_model_brush_radial_gradient
import io.github.sadellie.sukko.resources.core_model_brush_solid
import org.jetbrains.compose.resources.StringResource

@Composable
internal fun BrushSourceSelectorSheet(
  state: ModalBottomSheetState,
  onValueSelected: (newValue: BrushSource) -> Unit,
  value: BrushSource,
  globals: Globals,
) {
  ModalBottomSheet2(state) {
    BrushSourcesSelectorSheetContent(
      onDismiss = state::hide,
      onValueSelected = onValueSelected,
      value = value,
      globals = globals,
    )
  }
}

@Composable
private fun BrushSourcesSelectorSheetContent(
  onDismiss: () -> Unit,
  onValueSelected: (newValue: BrushSource) -> Unit,
  value: BrushSource,
  globals: Globals,
) {
  var currentInputMode by rememberSaveable { mutableStateOf(BrushInputMode.initialMode(value)) }
  SelectorSheetTemplateContent(
    currentInputMode = currentInputMode,
    inputModes = remember { BrushInputMode.entries },
    onInputModeUpdate = { currentInputMode = it },
  ) { inputMode ->
    when (inputMode) {
      BrushInputMode.SOLID ->
        SolidBrush(
          onDismiss = onDismiss,
          onConfirm = onValueSelected,
          initialValue = value,
          globals = globals,
        )

      BrushInputMode.LINEAR ->
        LinearBrushEditor(
          onDismiss = onDismiss,
          onConfirm = onValueSelected,
          initialValue = value,
          globals = globals,
        )

      BrushInputMode.RADIAL ->
        RadialBrushEditor(
          onDismiss = onDismiss,
          onConfirm = onValueSelected,
          initialValue = value,
          globals = globals,
        )
    }
  }
}

@Composable
private fun SolidBrush(
  onDismiss: () -> Unit,
  onConfirm: (BrushSource.SolidColor) -> Unit,
  initialValue: BrushSource,
  globals: Globals,
) {
  ColorSelectorSheetContent(
    onDismissRequest = onDismiss,
    onValueSelected = { onConfirm(BrushSource.SolidColor(it)) },
    value =
      remember {
        if (initialValue is BrushSource.SolidColor) initialValue.color
        else ScriptableColor.FixedCustom(Color.Unspecified)
      },
    globals = globals,
  )
}

private enum class BrushInputMode(override val displayName: StringResource) :
  io.github.sadellie.sukko.feature.editor.selector.InputMode {
  SOLID(displayName = Res.string.core_model_brush_solid),
  LINEAR(displayName = Res.string.core_model_brush_linear_gradient),
  RADIAL(displayName = Res.string.core_model_brush_radial_gradient);

  companion object {
    fun initialMode(brushSource: BrushSource) =
      when (brushSource) {
        is BrushSource.SolidColor -> SOLID
        is BrushSource.RadialGradient -> RADIAL
        is BrushSource.LinearGradient -> LINEAR
      }
  }
}
