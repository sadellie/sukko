package io.github.sadellie.sukko.core.model.modifier

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.LayerContext
import io.github.sadellie.sukko.core.model.basic.ScriptableDouble
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.core_model_modifier_fill_max_size
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ColdFillMaxSizeModifier(
  override val id: Int,
  val fraction: ScriptableDouble = ScriptableDouble.Fixed(1.0),
) : WidgetModifier.Cold {
  @Transient override val displayName = Res.string.core_model_modifier_fill_max_size

  companion object {
    val fractionRange by lazy { 0.0..1.0 }
  }

  override suspend fun evaluate(layerContext: LayerContext, globals: Globals) =
    EvaluatedFillMaxSizeModifier(
      id = id,
      fraction = fraction.getValue(layerContext, globals).coerceIn(fractionRange).toFloat(),
    )

  override fun updateId(newId: Int) = this.copy(id = newId)
}

data class EvaluatedFillMaxSizeModifier(override val id: Int, val fraction: Float) :
  WidgetModifier.Evaluated {
  override fun addToModifier(modifier: Modifier, scope: Any) =
    modifier.fillMaxSize(fraction = fraction)
}
