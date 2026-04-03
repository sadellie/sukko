package io.github.sadellie.sukko.core.model.layer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import google.material.design.symbols.Steppers
import google.material.design.symbols.Symbols
import io.github.sadellie.sukko.core.model.basic.ClickAction
import io.github.sadellie.sukko.core.model.basic.M3Color
import io.github.sadellie.sukko.core.model.basic.ScriptableBoolean
import io.github.sadellie.sukko.core.model.basic.ScriptableColor
import io.github.sadellie.sukko.core.model.basic.ScriptableDouble
import io.github.sadellie.sukko.core.model.basic.ScriptableDp
import io.github.sadellie.sukko.core.model.basic.ShapeSource
import io.github.sadellie.sukko.core.model.modifier.WidgetModifier
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.core_model_layer_step_indicator
import io.github.sadellie.sukko.resources.core_model_layer_step_indicator_description
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ColdStepIndicatorLayer(
  override val id: Int,
  override val parentId: Int? = null,
  override val name: String? = null,
  override val widgetModifiers: List<WidgetModifier.Cold> = emptyList(),
  override val clickActions: List<ClickAction.Cold> = emptyList(),
  override val isEnabled: ScriptableBoolean = ScriptableBoolean.Fixed(true),
  val fill: ScriptableBoolean = ScriptableBoolean.Fixed(true),
  val totalSteps: ScriptableDouble = ScriptableDouble.Fixed(DEFAULT_TOTAL_STEPS),
  val currentStep: ScriptableDouble = ScriptableDouble.Fixed(DEFAULT_CURRENT_STEP),
  val indicatorSize: ScriptableDp = ScriptableDp.Fixed(8.dp),
  val activeColor: ScriptableColor = ScriptableColor.FixedM3(M3Color.PRIMARY),
  val inactiveColor: ScriptableColor = ScriptableColor.FixedM3(M3Color.SECONDARY_CONTAINER),
  val shape: ShapeSource = ShapeSource.Circle,
) : Layer.Cold {
  @Transient override val displayName = Res.string.core_model_layer_step_indicator

  @Transient
  override val displayDescription = Res.string.core_model_layer_step_indicator_description

  @Transient override val icon = Symbols.Steppers

  override fun updateId(id: Int) = this.copy(id = id)

  override fun updateName(name: String) = this.copy(name = name)

  override fun updateClickActions(clickActions: List<ClickAction.Cold>) =
    this.copy(clickActions = clickActions)

  override fun updateWidgetModifiers(widgetModifiers: List<WidgetModifier.Cold>) =
    this.copy(widgetModifiers = widgetModifiers)

  override fun updateIsEnabled(isEnabled: ScriptableBoolean) = this.copy(isEnabled = isEnabled)
}

data class EvaluatedStepIndicatorLayer(
  override val id: Int,
  override val parentId: Int? = null,
  override val name: String? = null,
  override val widgetModifiers: List<WidgetModifier.Evaluated> = emptyList(),
  override val clickActions: List<ClickAction.Evaluated> = emptyList(),
  val fill: Boolean = true,
  val totalSteps: Int = 10,
  val currentStep: Int = 3,
  val indicatorSize: Dp = 8.dp,
  val activeColor: Color = Color.Black,
  val inactiveColor: Color = Color.White,
  val shape: Shape = CircleShape,
) : Layer.Evaluated {
  @Composable
  override fun Render(
    modifier: Modifier,
    renderOption: RenderOption,
    childrenLayers: List<Layer.Evaluated>,
    onGloballyPositioned: (Int, Rect) -> Unit,
    scope: Any,
  ) {
    Row(
      modifier = createModifier(modifier, renderOption, onGloballyPositioned, scope),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      repeat(totalSteps) { index ->
        val step = index + 1
        val isActive = if (fill) step <= currentStep else step == currentStep
        Box(
          modifier =
            Modifier.size(indicatorSize)
              .background(color = if (isActive) activeColor else inactiveColor, shape = shape)
        )
      }
    }
  }
}

private const val DEFAULT_TOTAL_STEPS = 10.0
private const val DEFAULT_CURRENT_STEP = 5.0
