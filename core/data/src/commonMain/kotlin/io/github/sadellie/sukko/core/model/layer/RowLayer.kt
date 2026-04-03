package io.github.sadellie.sukko.core.model.layer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import google.material.design.symbols.Symbols
import google.material.design.symbols.WidthNormal
import io.github.sadellie.sukko.core.model.basic.AlignmentSource
import io.github.sadellie.sukko.core.model.basic.ArrangementSource
import io.github.sadellie.sukko.core.model.basic.ClickAction
import io.github.sadellie.sukko.core.model.basic.ScriptableBoolean
import io.github.sadellie.sukko.core.model.modifier.WidgetModifier
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.core_model_layer_row
import io.github.sadellie.sukko.resources.core_model_layer_row_description
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ColdRowLayer(
  override val id: Int,
  override val parentId: Int? = null,
  override val name: String? = null,
  override val widgetModifiers: List<WidgetModifier.Cold> = emptyList(),
  override val clickActions: List<ClickAction.Cold> = emptyList(),
  override val isEnabled: ScriptableBoolean = ScriptableBoolean.Fixed(true),
  val arrangementSource: ArrangementSource.Horizontal = ArrangementSource.Start,
  val alignmentSource: AlignmentSource.Vertical = AlignmentSource.Top,
) : Layer.Cold {
  @Transient override val displayName = Res.string.core_model_layer_row
  @Transient override val displayDescription = Res.string.core_model_layer_row_description
  @Transient override val icon = Symbols.WidthNormal

  override fun updateName(name: String) = this.copy(name = name)

  override fun updateClickActions(clickActions: List<ClickAction.Cold>) =
    this.copy(clickActions = clickActions)

  override fun updateId(id: Int) = this.copy(id = id)

  override fun updateWidgetModifiers(widgetModifiers: List<WidgetModifier.Cold>) =
    this.copy(widgetModifiers = widgetModifiers)

  override fun updateIsEnabled(isEnabled: ScriptableBoolean) = this.copy(isEnabled = isEnabled)
}

data class EvaluatedRowLayer(
  override val id: Int,
  override val parentId: Int? = null,
  override val name: String? = null,
  override val widgetModifiers: List<WidgetModifier.Evaluated> = emptyList(),
  override val clickActions: List<ClickAction.Evaluated> = emptyList(),
  val arrangement: Arrangement.Horizontal = Arrangement.Start,
  val alignment: Alignment.Vertical = Alignment.Top,
) : Layer.Evaluated {
  @Composable
  override fun Render(
    modifier: Modifier,
    renderOption: RenderOption,
    childrenLayers: List<Layer.Evaluated>,
    onGloballyPositioned: (Int, Rect) -> Unit,
    scope: Any,
  ) {
    Row(
      modifier = createModifier(modifier, renderOption, onGloballyPositioned, scope),
      horizontalArrangement = arrangement,
      verticalAlignment = alignment,
    ) {
      NestedRenderer(
        layers = childrenLayers,
        parentId = id,
        onGloballyPositioned = onGloballyPositioned,
        scope = this,
        renderOption = renderOption,
      )
    }
  }
}
