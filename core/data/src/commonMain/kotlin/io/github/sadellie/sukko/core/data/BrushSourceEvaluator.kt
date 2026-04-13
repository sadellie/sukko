package io.github.sadellie.sukko.core.data

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import io.github.sadellie.sukko.core.model.basic.BrushSource

class BrushSourceEvaluator(private val scriptableEvaluator: ScriptableEvaluator) {
  suspend fun evaluate(brushSource: BrushSource) =
    when (brushSource) {
      is BrushSource.LinearGradient -> {
        require(brushSource.colors.size >= 2) {
          "LinearGradient: expected at least 2 colors. Got: ${brushSource.colors}"
        }
        val colorWithStops =
          brushSource.colors
            .map { (stop, color) -> stop to scriptableEvaluator.evaluateColor(color) }
            .toTypedArray()
        if (brushSource.horizontal) {
          Brush.horizontalGradient(colorStops = colorWithStops)
        } else {
          Brush.verticalGradient(colorStops = colorWithStops)
        }
      }
      is BrushSource.RadialGradient -> {
        val colorsWithStops =
          brushSource.colors
            .map { (stop, color) -> stop to scriptableEvaluator.evaluateColor(color) }
            .toTypedArray()
        Brush.radialGradient(
          colorStops = colorsWithStops,
          radius = scriptableEvaluator.evaluateDouble(brushSource.radius).toFloat(),
        )
      }
      is BrushSource.SolidColor -> SolidColor(scriptableEvaluator.evaluateColor(brushSource.color))
    }
}
