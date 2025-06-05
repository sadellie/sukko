package io.github.sadellie.sukko.core.model.modifier

import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.LayerContext
import io.github.sadellie.sukko.core.model.basic.ScriptableDp
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.core_model_modifier_height
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ColdHeightModifier(
  override val id: Int,
  val height: ScriptableDp = ScriptableDp.Fixed(72.dp),
) : WidgetModifier.Cold {
  @Transient override val displayName = Res.string.core_model_modifier_height

  companion object {
    val heightRange by lazy { 0.dp..Dp.Infinity }
  }

  override suspend fun evaluate(layerContext: LayerContext, globals: Globals) =
    EvaluatedHeightModifier(
      id = id,
      height = height.getValue(layerContext, globals).coerceIn(heightRange),
    )

  override fun updateId(newId: Int): WidgetModifier.Cold = this.copy(id = newId)
}

data class EvaluatedHeightModifier(override val id: Int, val height: Dp) :
  WidgetModifier.Evaluated {
  override fun addToModifier(modifier: Modifier, scope: Any) = modifier.height(height)
}
