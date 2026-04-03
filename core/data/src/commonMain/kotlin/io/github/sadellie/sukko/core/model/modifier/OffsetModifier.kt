package io.github.sadellie.sukko.core.model.modifier

import androidx.compose.foundation.layout.offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.sadellie.sukko.core.model.basic.ScriptableDp
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.core_model_modifier_offset
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ColdOffsetModifier(
  override val id: Int,
  val x: ScriptableDp = ScriptableDp.Fixed(0.dp),
  val y: ScriptableDp = ScriptableDp.Fixed(0.dp),
) : WidgetModifier.Cold {
  @Transient override val displayName = Res.string.core_model_modifier_offset

  override fun updateId(newId: Int) = this.copy(id = newId)
}

data class EvaluatedOffsetModifier(override val id: Int, val x: Dp, val y: Dp) :
  WidgetModifier.Evaluated {
  override fun addToModifier(modifier: Modifier, scope: Any) = modifier.offset(x, y)
}
