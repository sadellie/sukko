package io.github.sadellie.sukko.core.model.basic

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.LayerContext
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.core_model_brush_linear_gradient
import io.github.sadellie.sukko.resources.core_model_brush_radial_gradient
import io.github.sadellie.sukko.resources.core_model_brush_solid
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Serializable
sealed interface BrushSource {
  suspend fun getBrush(layerContext: LayerContext, globals: Globals): Brush

  val displayName: StringResource

  @Composable fun displayValue(): String = stringResource(displayName)

  companion object {
    fun values(): List<BrushSource> =
      listOf(
        SolidColor(ScriptableColor.FixedM3(M3Color.PRIMARY)),
        LinearGradient(
          colors =
            listOf(
              0f to ScriptableColor.FixedCustom(Color.Gray),
              1f to ScriptableColor.FixedCustom(Color.DarkGray),
            ),
          horizontal = true,
        ),
        RadialGradient(
          colors = emptyList(),
          radius = ScriptableDouble.Fixed(Double.POSITIVE_INFINITY),
        ),
      )
  }

  @Serializable
  data class SolidColor(val color: ScriptableColor) : BrushSource {
    @Transient override val displayName = Res.string.core_model_brush_solid

    @Composable override fun displayValue() = LocalScriptableDisplay.current.displayString(color)

    override suspend fun getBrush(layerContext: LayerContext, globals: Globals): Brush =
      SolidColor(color.getValue(layerContext, globals))
  }

  @Serializable
  data class LinearGradient(
    val colors: List<Pair<Float, ScriptableColor>>,
    val horizontal: Boolean,
  ) : BrushSource {
    @Transient override val displayName = Res.string.core_model_brush_linear_gradient

    @Suppress("SpreadOperator")
    override suspend fun getBrush(layerContext: LayerContext, globals: Globals): Brush {
      require(colors.size >= 2) { "LinearGradient: expected at least 2 colors. Got: $colors" }
      val colorWithStops =
        colors.map { (stop, color) -> stop to color.getValue(layerContext, globals) }.toTypedArray()
      return if (horizontal) {
        Brush.horizontalGradient(*colorWithStops)
      } else {
        Brush.verticalGradient(*colorWithStops)
      }
    }
  }

  @Serializable
  data class RadialGradient(val colors: List<ScriptableColor>, val radius: ScriptableDouble) :
    BrushSource {
    @Transient override val displayName = Res.string.core_model_brush_radial_gradient

    override suspend fun getBrush(layerContext: LayerContext, globals: Globals): Brush =
      Brush.radialGradient(
        colors = colors.map { it.getValue(layerContext, globals) },
        radius = radius.getValue(layerContext, globals).toFloat(),
      )
  }
}
