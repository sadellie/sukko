package io.github.sadellie.sukko.core.model.modifier

import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.LayerContext
import io.github.sadellie.sukko.core.model.basic.BrushSource
import io.github.sadellie.sukko.core.model.basic.M3Color
import io.github.sadellie.sukko.core.model.basic.ScriptableColor
import io.github.sadellie.sukko.core.model.basic.ShapeSource
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.core_model_modifier_background_color
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ColdBackgroundColorModifier(
  override val id: Int,
  val color: BrushSource = BrushSource.SolidColor(ScriptableColor.FixedM3(M3Color.PRIMARY)),
  val shapeSource: ShapeSource = ShapeSource.CutCornersDp(size = 0.dp),
) : WidgetModifier.Cold {
  @Transient override val displayName = Res.string.core_model_modifier_background_color

  override suspend fun evaluate(layerContext: LayerContext, globals: Globals) =
    EvaluatedBackgroundColorModifier(
      id = id,
      color = color.getBrush(layerContext, globals),
      shape = shapeSource.getShape(),
    )

  override fun updateId(newId: Int) = this.copy(id = newId)
}

data class EvaluatedBackgroundColorModifier(
  override val id: Int,
  val color: Brush,
  val shape: Shape,
) : WidgetModifier.Evaluated {
  override fun addToModifier(modifier: Modifier, scope: Any) =
    modifier.background(brush = color, shape = shape)
}
