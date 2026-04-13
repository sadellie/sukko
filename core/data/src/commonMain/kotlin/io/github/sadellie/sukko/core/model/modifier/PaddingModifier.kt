package io.github.sadellie.sukko.core.model.modifier

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import io.github.sadellie.sukko.core.model.basic.ScriptableDouble
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.core_model_modifier_padding_all_sides
import io.github.sadellie.sukko.resources.core_model_modifier_padding_axis
import io.github.sadellie.sukko.resources.core_model_modifier_padding_each_side
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ColdPaddingAllSidesModifier(
  override val id: Int,
  val all: ScriptableDouble = ScriptableDouble.Fixed(0.0),
) : WidgetModifier.Cold {
  @Transient override val displayName = Res.string.core_model_modifier_padding_all_sides

  companion object {
    val valueRange by lazy { 0.0..Double.MAX_VALUE }
  }

  override fun updateId(newId: Int) = this.copy(id = newId)
}

@Serializable
data class ColdPaddingEachSideModifier(
  override val id: Int,
  val start: ScriptableDouble = ScriptableDouble.Fixed(0.0),
  val end: ScriptableDouble = ScriptableDouble.Fixed(0.0),
  val top: ScriptableDouble = ScriptableDouble.Fixed(0.0),
  val bottom: ScriptableDouble = ScriptableDouble.Fixed(0.0),
) : WidgetModifier.Cold {
  @Transient override val displayName = Res.string.core_model_modifier_padding_each_side

  companion object {
    val valueRange by lazy { 0.0..Double.MAX_VALUE }
  }

  override fun updateId(newId: Int) = this.copy(id = newId)
}

@Serializable
data class ColdPaddingAxisModifier(
  override val id: Int,
  val horizontal: ScriptableDouble = ScriptableDouble.Fixed(0.0),
  val vertical: ScriptableDouble = ScriptableDouble.Fixed(0.0),
) : WidgetModifier.Cold {
  @Transient override val displayName = Res.string.core_model_modifier_padding_axis

  companion object {
    val valueRange by lazy { 0.0..Double.MAX_VALUE }
  }

  override fun updateId(newId: Int) = this.copy(id = newId)
}

data class EvaluatedPaddingModifier(
  override val id: Int,
  val start: Dp,
  val end: Dp,
  val top: Dp,
  val bottom: Dp,
) : WidgetModifier.Evaluated {
  override fun addToModifier(modifier: Modifier, scope: Any) =
    modifier.padding(start = start, end = end, top = top, bottom = bottom)
}
