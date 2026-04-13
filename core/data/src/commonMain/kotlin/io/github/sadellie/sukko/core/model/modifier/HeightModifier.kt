package io.github.sadellie.sukko.core.model.modifier

import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import io.github.sadellie.sukko.core.model.basic.ScriptableDouble
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.core_model_modifier_height
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ColdHeightModifier(
  override val id: Int,
  val height: ScriptableDouble = ScriptableDouble.Fixed(72.0),
) : WidgetModifier.Cold {
  @Transient override val displayName = Res.string.core_model_modifier_height

  companion object {
    val heightRange by lazy { 0.0..Double.MAX_VALUE }
  }

  override fun updateId(newId: Int): WidgetModifier.Cold = this.copy(id = newId)
}

data class EvaluatedHeightModifier(override val id: Int, val height: Dp) :
  WidgetModifier.Evaluated {
  override fun addToModifier(modifier: Modifier, scope: Any) = modifier.height(height)
}
