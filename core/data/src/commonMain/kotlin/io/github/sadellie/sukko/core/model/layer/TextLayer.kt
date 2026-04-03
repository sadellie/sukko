package io.github.sadellie.sukko.core.model.layer

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import google.material.design.symbols.Symbols
import google.material.design.symbols.TextFields
import io.github.sadellie.sukko.core.model.basic.BrushSource
import io.github.sadellie.sukko.core.model.basic.ClickAction
import io.github.sadellie.sukko.core.model.basic.M3Color
import io.github.sadellie.sukko.core.model.basic.ScriptableBoolean
import io.github.sadellie.sukko.core.model.basic.ScriptableColor
import io.github.sadellie.sukko.core.model.basic.ScriptableDouble
import io.github.sadellie.sukko.core.model.basic.ScriptableString
import io.github.sadellie.sukko.core.model.basic.TextOverflowSource
import io.github.sadellie.sukko.core.model.basic.TextStyleSource
import io.github.sadellie.sukko.core.model.modifier.WidgetModifier
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.core_model_layer_text
import io.github.sadellie.sukko.resources.core_model_layer_text_description
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ColdTextLayer(
  override val id: Int,
  override val parentId: Int? = null,
  override val name: String? = null,
  override val widgetModifiers: List<WidgetModifier.Cold> = emptyList(),
  override val clickActions: List<ClickAction.Cold> = emptyList(),
  override val isEnabled: ScriptableBoolean = ScriptableBoolean.Fixed(true),
  val textStyleSource: TextStyleSource = TextStyleSource.Local(),
  val text: ScriptableString = ScriptableString.Fixed("Fixed text"),
  val textColor: BrushSource =
    BrushSource.SolidColor(ScriptableColor.FixedM3(M3Color.ON_BACKGROUND)),
  val minLines: ScriptableDouble = ScriptableDouble.Fixed(1.0),
  val maxLines: ScriptableDouble = ScriptableDouble.Fixed(Int.MAX_VALUE.toDouble()),
  val textOverflowSource: TextOverflowSource = TextOverflowSource.Clip,
) : Layer.Cold {
  @Transient override val displayName = Res.string.core_model_layer_text
  @Transient override val displayDescription = Res.string.core_model_layer_text_description
  @Transient override val icon = Symbols.TextFields

  companion object {
    val minLinesRange by lazy { 0.00..Int.MAX_VALUE.toDouble() }
    val maxLinesRange by lazy { 0.00..Int.MAX_VALUE.toDouble() }
  }

  override fun updateName(name: String) = this.copy(name = name)

  override fun updateClickActions(clickActions: List<ClickAction.Cold>) =
    this.copy(clickActions = clickActions)

  override fun updateId(id: Int) = this.copy(id = id)

  override fun updateWidgetModifiers(widgetModifiers: List<WidgetModifier.Cold>) =
    this.copy(widgetModifiers = widgetModifiers)

  override fun updateIsEnabled(isEnabled: ScriptableBoolean) = this.copy(isEnabled = isEnabled)
}

data class EvaluatedTextLayer(
  override val id: Int,
  override val parentId: Int? = null,
  override val name: String? = null,
  override val widgetModifiers: List<WidgetModifier.Evaluated> = emptyList(),
  override val clickActions: List<ClickAction.Evaluated> = emptyList(),
  val textStyle: TextStyle = TextStyle(),
  val textColor: Brush = SolidColor(Color.Black),
  val text: String = "Text",
  val minLines: Int = 1,
  val maxLines: Int = Int.MAX_VALUE,
  val overflow: TextOverflow = TextOverflow.Clip,
) : Layer.Evaluated {
  @Composable
  override fun Render(
    modifier: Modifier,
    renderOption: RenderOption,
    childrenLayers: List<Layer.Evaluated>,
    onGloballyPositioned: (Int, Rect) -> Unit,
    scope: Any,
  ) {
    Text(
      modifier = createModifier(modifier, renderOption, onGloballyPositioned, scope),
      text = text,
      style = remember(textStyle, textColor) { textStyle.copy(textColor) },
      minLines = minLines,
      maxLines = maxLines,
      overflow = overflow,
    )
  }
}
