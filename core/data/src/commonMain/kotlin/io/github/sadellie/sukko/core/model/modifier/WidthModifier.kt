package io.github.sadellie.sukko.core.model.modifier

import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.sadellie.sukko.core.model.basic.ScriptableDp
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.core_model_modifier_width
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ColdWidthModifier(
  override val id: Int,
  val width: ScriptableDp = ScriptableDp.Fixed(72.dp),
) : WidgetModifier.Cold {
  @Transient override val displayName = Res.string.core_model_modifier_width

  companion object {
    val widthRange by lazy { 0.dp..Dp.Infinity }
  }

  override fun updateId(newId: Int): WidgetModifier.Cold = this.copy(id = newId)
}

data class EvaluatedWidthModifier(override val id: Int, val width: Dp) : WidgetModifier.Evaluated {
  override fun addToModifier(modifier: Modifier, scope: Any) = modifier.width(width)
}
