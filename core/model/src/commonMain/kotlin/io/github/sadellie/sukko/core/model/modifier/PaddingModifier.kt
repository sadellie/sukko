package io.github.sadellie.sukko.core.model.modifier

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.LayerContext
import io.github.sadellie.sukko.core.model.basic.ScriptableDp
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.core_model_modifier_padding_all_sides
import io.github.sadellie.sukko.resources.core_model_modifier_padding_axis
import io.github.sadellie.sukko.resources.core_model_modifier_padding_each_side
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ColdPaddingAllSidesModifier(
  override val id: Int,
  val all: ScriptableDp = ScriptableDp.Fixed(0.dp),
) : WidgetModifier.Cold {
  @Transient override val displayName = Res.string.core_model_modifier_padding_all_sides

  companion object {
    val valueRange by lazy { 0.dp..Dp.Infinity }
  }

  override suspend fun evaluate(
    layerContext: LayerContext,
    globals: Globals,
  ): WidgetModifier.Evaluated {
    val allEvaluated = all.getValue(layerContext, globals).coerceIn(valueRange)
    return EvaluatedPaddingModifier(
      id = id,
      start = allEvaluated,
      end = allEvaluated,
      top = allEvaluated,
      bottom = allEvaluated,
    )
  }

  override fun updateId(newId: Int) = this.copy(id = newId)
}

@Serializable
data class ColdPaddingEachSideModifier(
  override val id: Int,
  val start: ScriptableDp = ScriptableDp.Fixed(0.dp),
  val end: ScriptableDp = ScriptableDp.Fixed(0.dp),
  val top: ScriptableDp = ScriptableDp.Fixed(0.dp),
  val bottom: ScriptableDp = ScriptableDp.Fixed(0.dp),
) : WidgetModifier.Cold {
  @Transient override val displayName = Res.string.core_model_modifier_padding_each_side

  companion object {
    val valueRange by lazy { 0.dp..Dp.Infinity }
  }

  override suspend fun evaluate(
    layerContext: LayerContext,
    globals: Globals,
  ): WidgetModifier.Evaluated =
    EvaluatedPaddingModifier(
      id = id,
      start = start.getValue(layerContext, globals).coerceIn(valueRange),
      end = end.getValue(layerContext, globals).coerceIn(valueRange),
      top = top.getValue(layerContext, globals).coerceIn(valueRange),
      bottom = bottom.getValue(layerContext, globals).coerceIn(valueRange),
    )

  override fun updateId(newId: Int) = this.copy(id = newId)
}

@Serializable
data class ColdPaddingAxisModifier(
  override val id: Int,
  val horizontal: ScriptableDp = ScriptableDp.Fixed(0.dp),
  val vertical: ScriptableDp = ScriptableDp.Fixed(0.dp),
) : WidgetModifier.Cold {
  @Transient override val displayName = Res.string.core_model_modifier_padding_axis

  companion object {
    val valueRange by lazy { 0.dp..Dp.Infinity }
  }

  override suspend fun evaluate(
    layerContext: LayerContext,
    globals: Globals,
  ): WidgetModifier.Evaluated {
    val horizontalEvaluated = horizontal.getValue(layerContext, globals).coerceIn(valueRange)
    val verticalEvaluated = vertical.getValue(layerContext, globals).coerceIn(valueRange)
    return EvaluatedPaddingModifier(
      id = id,
      start = horizontalEvaluated,
      end = horizontalEvaluated,
      top = verticalEvaluated,
      bottom = verticalEvaluated,
    )
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
