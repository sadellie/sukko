package io.github.sadellie.sukko.core.data

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.LayerContext
import io.github.sadellie.sukko.core.model.basic.BrushSource

class BrushSourceEvaluator(
  private val brushSource: BrushSource,
  private val layerContext: LayerContext,
  private val globals: Globals,
) {
  suspend fun evaluate() =
    when (brushSource) {
      is BrushSource.LinearGradient -> {
        require(brushSource.colors.size >= 2) {
          "LinearGradient: expected at least 2 colors. Got: ${brushSource.colors}"
        }
        val colorWithStops =
          brushSource.colors
            .map { (stop, color) -> stop to color.getValue(layerContext, globals) }
            .toTypedArray()
        if (brushSource.horizontal) {
          Brush.horizontalGradient(*colorWithStops)
        } else {
          Brush.verticalGradient(*colorWithStops)
        }
      }
      is BrushSource.RadialGradient ->
        Brush.radialGradient(
          colors = brushSource.colors.map { it.getValue(layerContext, globals) },
          radius = brushSource.radius.getValue(layerContext, globals).toFloat(),
        )
      is BrushSource.SolidColor -> SolidColor(brushSource.color.getValue(layerContext, globals))
    }
}
