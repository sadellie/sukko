package io.github.sadellie.sukko.core.model.modifier

import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.LayerContext
import io.github.sadellie.sukko.core.model.basic.ScriptableDp
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.core_model_modifier_size
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ColdSizeModifier(
  override val id: Int,
  val size: ScriptableDp = ScriptableDp.Fixed(48.dp),
) : WidgetModifier.Cold {
  @Transient override val displayName = Res.string.core_model_modifier_size

  override suspend fun evaluate(layerContext: LayerContext, globals: Globals) =
    EvaluatedSizeModifier(id = id, size = size.getValue(layerContext, globals))

  override fun updateId(newId: Int) = this.copy(id = newId)
}

data class EvaluatedSizeModifier(override val id: Int, val size: Dp) : WidgetModifier.Evaluated {
  override fun addToModifier(modifier: Modifier, scope: Any) = modifier.size(size = size)
}
