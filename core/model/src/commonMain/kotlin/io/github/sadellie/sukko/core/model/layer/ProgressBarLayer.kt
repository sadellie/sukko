package io.github.sadellie.sukko.core.model.layer

import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import google.material.design.symbols.LineEnd
import google.material.design.symbols.Symbols
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.LayerContext
import io.github.sadellie.sukko.core.model.basic.ClickAction
import io.github.sadellie.sukko.core.model.basic.M3Color
import io.github.sadellie.sukko.core.model.basic.ScriptableBoolean
import io.github.sadellie.sukko.core.model.basic.ScriptableColor
import io.github.sadellie.sukko.core.model.basic.ScriptableDouble
import io.github.sadellie.sukko.core.model.basic.ScriptableDp
import io.github.sadellie.sukko.core.model.basic.evaluate
import io.github.sadellie.sukko.core.model.modifier.WidgetModifier
import io.github.sadellie.sukko.core.model.modifier.evaluate
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.core_model_layer_progress_bar
import io.github.sadellie.sukko.resources.core_model_layer_progress_bar_description
import io.github.sadellie.sukko.resources.core_model_progress_bar_type_circular
import io.github.sadellie.sukko.resources.core_model_progress_bar_type_linear
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.jetbrains.compose.resources.StringResource

@Serializable
data class ColdProgressBarLayer(
  override val id: Int,
  override val parentId: Int? = null,
  override val name: String? = null,
  override val widgetModifiers: List<WidgetModifier.Cold> = emptyList(),
  override val clickActions: List<ClickAction.Cold> = emptyList(),
  override val isEnabled: ScriptableBoolean = ScriptableBoolean.Fixed(true),
  val progress: ScriptableDouble = ScriptableDouble.Fixed(DEFAULT_PROGRESS),
  val progressBarType: ProgressBarType = ProgressBarType.LINEAR,
  val color: ScriptableColor = ScriptableColor.FixedM3(M3Color.PRIMARY),
  val trackColor: ScriptableColor = ScriptableColor.FixedM3(M3Color.SECONDARY_CONTAINER),
  val gapSize: ScriptableDp = ScriptableDp.Fixed(4.dp),
  val amplitude: ScriptableDouble = ScriptableDouble.Fixed(1.0),
  val waveLength: ScriptableDp = ScriptableDp.Fixed(15.dp),
) : Layer.Cold {
  @Transient override val displayName = Res.string.core_model_layer_progress_bar
  @Transient override val displayDescription = Res.string.core_model_layer_progress_bar_description
  @Transient override val icon = Symbols.LineEnd

  companion object {
    val progressRange by lazy { 0.0..1.0 }
    val amplitudeRange by lazy { 0.0..1.0 }
  }

  override fun evaluateAsFlow(layerContext: LayerContext, globals: Globals) = flow {
    emit(null)
    if (!isEnabled.getValue(layerContext, globals)) return@flow
    val evaluated =
      EvaluatedProgressBarLayer(
        id = id,
        parentId = parentId,
        name = name,
        widgetModifiers = widgetModifiers.evaluate(layerContext, globals),
        clickActions = clickActions.evaluate(layerContext, globals),
        progress = progress.getValue(layerContext, globals).coerceIn(progressRange).toFloat(),
        progressBarType = progressBarType,
        color = color.getValue(layerContext, globals),
        trackColor = trackColor.getValue(layerContext, globals),
        gapSize = gapSize.getValue(layerContext, globals),
        amplitude = amplitude.getValue(layerContext, globals).coerceIn(amplitudeRange).toFloat(),
        waveLength = waveLength.getValue(layerContext, globals),
      )
    emit(evaluated)
  }

  override fun updateName(name: String) = this.copy(name = name)

  override fun updateClickActions(clickActions: List<ClickAction.Cold>) =
    this.copy(clickActions = clickActions)

  override fun updateId(id: Int) = this.copy(id = id)

  override fun updateWidgetModifiers(widgetModifiers: List<WidgetModifier.Cold>) =
    this.copy(widgetModifiers = widgetModifiers)

  override fun updateIsEnabled(isEnabled: ScriptableBoolean) = this.copy(isEnabled = isEnabled)
}

data class EvaluatedProgressBarLayer(
  override val id: Int,
  override val parentId: Int?,
  override val name: String?,
  override val widgetModifiers: List<WidgetModifier.Evaluated>,
  override val clickActions: List<ClickAction.Evaluated>,
  val progress: Float,
  val progressBarType: ProgressBarType,
  val color: Color,
  val trackColor: Color,
  val gapSize: Dp,
  val amplitude: Float,
  val waveLength: Dp,
) : Layer.Evaluated {
  @Composable
  override fun Render(
    modifier: Modifier,
    renderOption: RenderOption?,
    childrenLayers: List<Layer.Evaluated>,
    onGloballyPositioned: (Int, Rect) -> Unit,
    scope: Any,
  ) {
    when (progressBarType) {
      ProgressBarType.LINEAR ->
        LinearWavyProgressIndicator(
          progress = { progress },
          modifier = createModifier(modifier, renderOption, onGloballyPositioned, scope),
          color = color,
          trackColor = trackColor,
          gapSize = gapSize,
          amplitude = { amplitude },
          wavelength = waveLength,
          waveSpeed = Dp.Unspecified, // disable animation
        )
      ProgressBarType.CIRCULAR ->
        CircularWavyProgressIndicator(
          modifier = createModifier(modifier, renderOption, onGloballyPositioned, scope),
          progress = { progress },
          color = color,
          trackColor = trackColor,
          gapSize = gapSize,
          amplitude = { amplitude },
          wavelength = waveLength,
          waveSpeed = Dp.Unspecified, // disable animation
        )
    }
  }
}

enum class ProgressBarType(val res: StringResource) {
  LINEAR(Res.string.core_model_progress_bar_type_linear),
  CIRCULAR(Res.string.core_model_progress_bar_type_circular),
}

private const val DEFAULT_PROGRESS = 0.5
