package io.github.sadellie.sukko.core.model.modifier

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import co.touchlab.kermit.Logger
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.LayerContext
import io.github.sadellie.sukko.core.model.basic.AlignmentSource
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.core_model_modifier_column_alignment
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ColdColumnAlignmentModifier(
  override val id: Int,
  val alignmentSource: AlignmentSource.Horizontal = AlignmentSource.Start,
) : WidgetModifier.Cold {
  @Transient override val displayName = Res.string.core_model_modifier_column_alignment

  override suspend fun evaluate(layerContext: LayerContext, globals: Globals) =
    EvaluatedColumnAlignmentModifier(id = id, alignment = alignmentSource.getAlignment())

  override fun updateId(newId: Int) = this.copy(id = newId)
}

data class EvaluatedColumnAlignmentModifier(
  override val id: Int,
  val alignment: Alignment.Horizontal,
) : WidgetModifier.Evaluated {
  companion object {
    private const val TAG = "EvaluatedColumnAlignmentModifier"
  }

  override fun addToModifier(modifier: Modifier, scope: Any): Modifier {
    if (scope !is ColumnScope) {
      Logger.w(TAG) { "Wrong scope: $scope" }
      return modifier
    }
    return with(scope) { modifier.align(alignment) }
  }
}
