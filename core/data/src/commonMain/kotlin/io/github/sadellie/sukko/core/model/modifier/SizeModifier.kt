package io.github.sadellie.sukko.core.model.modifier

import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import io.github.sadellie.sukko.core.model.basic.ScriptableDouble
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.core_model_modifier_size
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ColdSizeModifier(
  override val id: Int,
  val size: ScriptableDouble = ScriptableDouble.Fixed(48.0),
) : WidgetModifier.Cold {
  @Transient override val displayName = Res.string.core_model_modifier_size

  override fun updateId(newId: Int) = this.copy(id = newId)
}

data class EvaluatedSizeModifier(override val id: Int, val size: Dp) : WidgetModifier.Evaluated {
  override fun addToModifier(modifier: Modifier, scope: Any) = modifier.size(size = size)
}
