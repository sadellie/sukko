package io.github.sadellie.sukko.core.model.modifier

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import co.touchlab.kermit.Logger
import io.github.sadellie.sukko.core.model.basic.AlignmentSource
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.core_model_modifier_box_alignment
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ColdBoxAlignmentModifier(
  override val id: Int,
  val alignmentSource: AlignmentSource.Both = AlignmentSource.TopStart,
) : WidgetModifier.Cold {
  @Transient override val displayName = Res.string.core_model_modifier_box_alignment

  override fun updateId(newId: Int) = this.copy(id = newId)
}

data class EvaluatedBoxAlignmentModifier(override val id: Int, val alignment: Alignment) :
  WidgetModifier.Evaluated {
  companion object {
    private const val TAG = "EvaluatedBoxAlignmentModifier"
  }

  override fun addToModifier(modifier: Modifier, scope: Any): Modifier {
    if (scope !is BoxScope) {
      Logger.w(tag = TAG) { "Wrong scope: $scope" }
      return modifier
    }
    return with(scope) { modifier.align(alignment) }
  }
}
