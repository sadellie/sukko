package io.github.sadellie.sukko.core.model.modifier

import androidx.compose.foundation.layout.RowScope
import androidx.compose.ui.Modifier
import co.touchlab.kermit.Logger
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.LayerContext
import io.github.sadellie.sukko.core.model.basic.ScriptableBoolean
import io.github.sadellie.sukko.core.model.basic.ScriptableDouble
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.core_model_modifier_row_weight
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ColdRowWeightModifier(
  override val id: Int,
  val weight: ScriptableDouble = ScriptableDouble.Fixed(1.0),
  val fill: ScriptableBoolean = ScriptableBoolean.Fixed(true),
) : WidgetModifier.Cold {
  @Transient override val displayName = Res.string.core_model_modifier_row_weight

  companion object {
    val weightRange by lazy { 0.01..Double.MAX_VALUE }
  }

  override suspend fun evaluate(layerContext: LayerContext, globals: Globals) =
    EvaluatedRowWeightModifier(
      id = id,
      weight = weight.getValue(layerContext, globals).coerceIn(weightRange).toFloat(),
      fill = fill.getValue(layerContext, globals),
    )

  override fun updateId(newId: Int) = this.copy(id = newId)
}

data class EvaluatedRowWeightModifier(override val id: Int, val weight: Float, val fill: Boolean) :
  WidgetModifier.Evaluated {
  companion object {
    private const val TAG = "EvaluatedRowWeightModifier"
  }

  override fun addToModifier(modifier: Modifier, scope: Any): Modifier {
    if (scope !is RowScope) {
      Logger.w(TAG) { "Wrong scope: $scope" }
      return modifier
    }
    return with(scope) { modifier.weight(weight, fill) }
  }
}
