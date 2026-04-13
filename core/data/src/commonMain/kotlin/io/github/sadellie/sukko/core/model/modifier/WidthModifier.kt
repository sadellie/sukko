package io.github.sadellie.sukko.core.model.modifier

import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import io.github.sadellie.sukko.core.model.basic.ScriptableDouble
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.core_model_modifier_width
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ColdWidthModifier(
  override val id: Int,
  val width: ScriptableDouble = ScriptableDouble.Fixed(72.0),
) : WidgetModifier.Cold {
  @Transient override val displayName = Res.string.core_model_modifier_width

  companion object {
    val widthRange by lazy { 0.0..Double.MAX_VALUE }
  }

  override fun updateId(newId: Int): WidgetModifier.Cold = this.copy(id = newId)
}

data class EvaluatedWidthModifier(override val id: Int, val width: Dp) : WidgetModifier.Evaluated {
  override fun addToModifier(modifier: Modifier, scope: Any) = modifier.width(width)
}
