package io.github.sadellie.sukko.core.model.modifier

import androidx.compose.foundation.layout.RowScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import co.touchlab.kermit.Logger
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.LayerContext
import io.github.sadellie.sukko.core.model.basic.AlignmentSource
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.core_model_modifier_row_alignment
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ColdRowAlignmentModifier(
  override val id: Int,
  val alignmentSource: AlignmentSource.Vertical = AlignmentSource.Top,
) : WidgetModifier.Cold {
  @Transient override val displayName = Res.string.core_model_modifier_row_alignment

  override suspend fun evaluate(layerContext: LayerContext, globals: Globals) =
    EvaluatedRowAlignmentModifier(id = id, alignment = alignmentSource.getAlignment())

  override fun updateId(newId: Int) = this.copy(id = newId)
}

data class EvaluatedRowAlignmentModifier(override val id: Int, val alignment: Alignment.Vertical) :
  WidgetModifier.Evaluated {
  companion object {
    private const val TAG = "EvaluatedRowAlignmentModifier"
  }

  override fun addToModifier(modifier: Modifier, scope: Any): Modifier {
    if (scope !is RowScope) {
      Logger.w(TAG) { "Wrong scope: $scope" }
      return modifier
    }
    return with(scope) { modifier.align(alignment) }
  }
}
