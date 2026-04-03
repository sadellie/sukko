package io.github.sadellie.sukko.core.model.modifier

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.ui.Modifier
import io.github.sadellie.sukko.core.model.basic.ScriptableDouble
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.core_model_modifier_fill_max_height
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ColdFillMaxHeightModifier(
  override val id: Int,
  val fraction: ScriptableDouble = ScriptableDouble.Fixed(1.0),
) : WidgetModifier.Cold {
  @Transient override val displayName = Res.string.core_model_modifier_fill_max_height

  companion object {
    val fractionRange by lazy { 0.0..1.0 }
  }

  override fun updateId(newId: Int) = this.copy(id = newId)
}

data class EvaluatedFillMaxHeightModifier(override val id: Int, val fraction: Float) :
  WidgetModifier.Evaluated {
  override fun addToModifier(modifier: Modifier, scope: Any) =
    modifier.fillMaxHeight(fraction = fraction)
}
