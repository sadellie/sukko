package io.github.sadellie.sukko.core.model.layer

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import google.material.design.symbols.Square
import google.material.design.symbols.Symbols
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.LayerContext
import io.github.sadellie.sukko.core.model.basic.AlignmentSource
import io.github.sadellie.sukko.core.model.basic.ClickAction
import io.github.sadellie.sukko.core.model.basic.ScriptableBoolean
import io.github.sadellie.sukko.core.model.basic.evaluate
import io.github.sadellie.sukko.core.model.modifier.WidgetModifier
import io.github.sadellie.sukko.core.model.modifier.evaluate
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.core_model_layer_box
import io.github.sadellie.sukko.resources.core_model_layer_box_description
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ColdBoxLayer(
  override val id: Int,
  override val parentId: Int? = null,
  override val name: String? = null,
  override val widgetModifiers: List<WidgetModifier.Cold> = emptyList(),
  override val clickActions: List<ClickAction.Cold> = emptyList(),
  override val isEnabled: ScriptableBoolean = ScriptableBoolean.Fixed(true),
  val alignmentSource: AlignmentSource.Both = AlignmentSource.TopStart,
) : Layer.Cold {
  @Transient override val displayName = Res.string.core_model_layer_box
  @Transient override val displayDescription = Res.string.core_model_layer_box_description
  @Transient override val icon = Symbols.Square

  override fun evaluateAsFlow(layerContext: LayerContext, globals: Globals) = flow {
    emit(null)
    if (!isEnabled.getValue(layerContext, globals)) return@flow
    val evaluated =
      EvaluatedBoxLayer(
        id = id,
        parentId = parentId,
        name = name,
        widgetModifiers = widgetModifiers.evaluate(layerContext, globals),
        clickActions = clickActions.evaluate(layerContext, globals),
        alignment = alignmentSource.getAlignment(),
      )
    emit(evaluated)
  }

  override fun updateName(name: String) = this.copy(name = name)

  override fun updateClickActions(clickActions: List<ClickAction.Cold>) =
    this.copy(clickActions = clickActions)

  override fun updateId(id: Int) = this.copy(id = id)

  override fun updateWidgetModifiers(widgetModifiers: List<WidgetModifier.Cold>) =
    this.copy(widgetModifiers = widgetModifiers)

  override fun updateIsEnabled(isEnabled: ScriptableBoolean) = this.copy(isEnabled = isEnabled)
}

data class EvaluatedBoxLayer(
  override val id: Int,
  override val parentId: Int?,
  override val name: String?,
  override val widgetModifiers: List<WidgetModifier.Evaluated>,
  override val clickActions: List<ClickAction.Evaluated>,
  val alignment: Alignment,
) : Layer.Evaluated {
  @Composable
  override fun Render(
    modifier: Modifier,
    renderOption: RenderOption?,
    childrenLayers: List<Layer.Evaluated>,
    onGloballyPositioned: (Int, Rect) -> Unit,
    scope: Any,
  ) {
    Box(
      modifier = createModifier(modifier, renderOption, onGloballyPositioned, scope),
      contentAlignment = alignment,
    ) {
      NestedRenderer(
        layers = childrenLayers,
        renderOption = renderOption,
        parentId = id,
        onGloballyPositioned = onGloballyPositioned,
        scope = this,
      )
    }
  }
}
