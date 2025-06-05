package io.github.sadellie.sukko.core.model.layer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import google.material.design.symbols.Symbols
import google.material.design.symbols.TableRows
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.LayerContext
import io.github.sadellie.sukko.core.model.basic.AlignmentSource
import io.github.sadellie.sukko.core.model.basic.ArrangementSource
import io.github.sadellie.sukko.core.model.basic.ClickAction
import io.github.sadellie.sukko.core.model.basic.ScriptableBoolean
import io.github.sadellie.sukko.core.model.basic.evaluate
import io.github.sadellie.sukko.core.model.modifier.WidgetModifier
import io.github.sadellie.sukko.core.model.modifier.evaluate
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.core_model_layer_column
import io.github.sadellie.sukko.resources.core_model_layer_column_description
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ColdColumnLayer(
  override val id: Int,
  override val parentId: Int? = null,
  override val name: String? = null,
  override val widgetModifiers: List<WidgetModifier.Cold> = emptyList(),
  override val clickActions: List<ClickAction.Cold> = emptyList(),
  override val isEnabled: ScriptableBoolean = ScriptableBoolean.Fixed(true),
  val arrangementSource: ArrangementSource.Vertical = ArrangementSource.Top,
  val alignmentSource: AlignmentSource.Horizontal = AlignmentSource.Start,
) : Layer.Cold {
  @Transient override val displayName = Res.string.core_model_layer_column
  @Transient override val displayDescription = Res.string.core_model_layer_column_description
  @Transient override val icon = Symbols.TableRows

  override fun evaluateAsFlow(layerContext: LayerContext, globals: Globals) = flow {
    emit(null)
    if (!isEnabled.getValue(layerContext, globals)) return@flow
    val evaluated =
      EvaluatedColumnLayer(
        id = id,
        parentId = parentId,
        name = name,
        widgetModifiers = widgetModifiers.evaluate(layerContext, globals),
        clickActions = clickActions.evaluate(layerContext, globals),
        arrangement = arrangementSource.getArrangement(),
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

data class EvaluatedColumnLayer(
  override val id: Int,
  override val parentId: Int?,
  override val name: String?,
  override val widgetModifiers: List<WidgetModifier.Evaluated>,
  override val clickActions: List<ClickAction.Evaluated>,
  val arrangement: Arrangement.Vertical,
  val alignment: Alignment.Horizontal,
) : Layer.Evaluated {
  @Composable
  override fun Render(
    modifier: Modifier,
    renderOption: RenderOption?,
    childrenLayers: List<Layer.Evaluated>,
    onGloballyPositioned: (Int, Rect) -> Unit,
    scope: Any,
  ) {
    Column(
      modifier = createModifier(modifier, renderOption, onGloballyPositioned, scope),
      verticalArrangement = arrangement,
      horizontalAlignment = alignment,
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
