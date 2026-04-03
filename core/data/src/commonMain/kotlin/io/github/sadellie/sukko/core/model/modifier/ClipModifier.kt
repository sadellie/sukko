package io.github.sadellie.sukko.core.model.modifier

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import io.github.sadellie.sukko.core.model.basic.ShapeSource
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.core_model_modifier_clip
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ColdClipModifier(
  override val id: Int,
  val shapeSource: ShapeSource = ShapeSource.CutCornersDp(),
) : WidgetModifier.Cold {
  @Transient override val displayName = Res.string.core_model_modifier_clip

  override fun updateId(newId: Int) = this.copy(id = newId)
}

data class EvaluatedClipModifier(override val id: Int, val shape: Shape) :
  WidgetModifier.Evaluated {
  override fun addToModifier(modifier: Modifier, scope: Any) = modifier.clip(shape = shape)
}
