package io.github.sadellie.sukko.core.model.modifier

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import io.github.sadellie.sukko.core.model.basic.ScriptableDouble
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.core_model_modifier_alpha
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ColdAlphaModifier(
  override val id: Int,
  val alpha: ScriptableDouble = ScriptableDouble.Fixed(1.0),
) : WidgetModifier.Cold {
  @Transient override val displayName = Res.string.core_model_modifier_alpha

  companion object {
    val alphaRange by lazy { 0.0..1.0 }
  }

  override fun updateId(newId: Int): WidgetModifier.Cold = this.copy(id = newId)
}

data class EvaluatedAlphaModifier(override val id: Int, val alpha: Float) :
  WidgetModifier.Evaluated {
  override fun addToModifier(modifier: Modifier, scope: Any) = modifier.alpha(alpha)
}
