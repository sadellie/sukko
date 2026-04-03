package io.github.sadellie.sukko.core.model.modifier

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.ui.Modifier
import io.github.sadellie.sukko.core.model.basic.ScriptableBoolean
import io.github.sadellie.sukko.core.model.basic.ScriptableDouble
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.core_model_modifier_aspect_ratio
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ColdAspectRatioModifier(
  override val id: Int,
  val ratio: ScriptableDouble = ScriptableDouble.Fixed(1.0),
  val matchHeightConstraintsFirst: ScriptableBoolean = ScriptableBoolean.Fixed(false),
) : WidgetModifier.Cold {
  @Transient override val displayName = Res.string.core_model_modifier_aspect_ratio

  companion object {
    val ratioRange by lazy { 0.01..Double.POSITIVE_INFINITY }
  }

  override fun updateId(newId: Int): WidgetModifier.Cold = this.copy(id = newId)
}

data class EvaluatedAspectRatioModifier(
  override val id: Int,
  val ratio: Float,
  val matchHeightConstraintsFirst: Boolean,
) : WidgetModifier.Evaluated {
  override fun addToModifier(modifier: Modifier, scope: Any) =
    modifier.aspectRatio(ratio = ratio, matchHeightConstraintsFirst = matchHeightConstraintsFirst)
}
