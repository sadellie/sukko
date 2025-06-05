package io.github.sadellie.sukko.core.model.modifier

import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.LayerContext
import io.github.sadellie.sukko.core.model.basic.BrushSource
import io.github.sadellie.sukko.core.model.basic.M3Color
import io.github.sadellie.sukko.core.model.basic.ScriptableColor
import io.github.sadellie.sukko.core.model.basic.ScriptableDp
import io.github.sadellie.sukko.core.model.basic.ShapeSource
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.core_model_modifier_border
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ColdBorderModifier(
  override val id: Int,
  val width: ScriptableDp = ScriptableDp.Fixed(1.dp),
  val color: BrushSource = BrushSource.SolidColor(ScriptableColor.FixedM3(M3Color.OUTLINE_VARIANT)),
  val shapeSource: ShapeSource = ShapeSource.CutCornersDp(size = 0.dp),
) : WidgetModifier.Cold {
  @Transient override val displayName = Res.string.core_model_modifier_border

  override suspend fun evaluate(layerContext: LayerContext, globals: Globals) =
    EvaluatedBorderModifier(
      id = id,
      width = width.getValue(layerContext, globals),
      color = color.getBrush(layerContext, globals),
      shape = shapeSource.getShape(),
    )

  override fun updateId(newId: Int) = this.copy(id = newId)
}

data class EvaluatedBorderModifier(
  override val id: Int,
  val width: Dp,
  val color: Brush,
  val shape: Shape,
) : WidgetModifier.Evaluated {
  override fun addToModifier(modifier: Modifier, scope: Any) =
    modifier.border(width = width, brush = color, shape = shape)
}
