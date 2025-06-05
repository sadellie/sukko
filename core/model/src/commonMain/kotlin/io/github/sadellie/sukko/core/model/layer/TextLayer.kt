package io.github.sadellie.sukko.core.model.layer

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import co.touchlab.kermit.Logger
import google.material.design.symbols.Symbols
import google.material.design.symbols.TextFields
import io.github.sadellie.sukko.core.fontfiles.FontFile
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.LayerContext
import io.github.sadellie.sukko.core.model.basic.BrushSource
import io.github.sadellie.sukko.core.model.basic.ClickAction
import io.github.sadellie.sukko.core.model.basic.M3Color
import io.github.sadellie.sukko.core.model.basic.ScriptableBoolean
import io.github.sadellie.sukko.core.model.basic.ScriptableColor
import io.github.sadellie.sukko.core.model.basic.ScriptableDouble
import io.github.sadellie.sukko.core.model.basic.ScriptableSp
import io.github.sadellie.sukko.core.model.basic.ScriptableString
import io.github.sadellie.sukko.core.model.basic.evaluate
import io.github.sadellie.sukko.core.model.modifier.WidgetModifier
import io.github.sadellie.sukko.core.model.modifier.evaluate
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.core_model_layer_text
import io.github.sadellie.sukko.resources.core_model_layer_text_description
import io.github.sadellie.sukko.resources.core_model_text_align_center
import io.github.sadellie.sukko.resources.core_model_text_align_end
import io.github.sadellie.sukko.resources.core_model_text_align_justify
import io.github.sadellie.sukko.resources.core_model_text_align_left
import io.github.sadellie.sukko.resources.core_model_text_align_right
import io.github.sadellie.sukko.resources.core_model_text_align_start
import io.github.sadellie.sukko.resources.core_model_text_style_global
import io.github.sadellie.sukko.resources.core_model_text_style_local
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.jetbrains.compose.resources.StringResource

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
) : Layer.Cold {
  @Transient override val displayName = Res.string.core_model_layer_text
  @Transient override val displayDescription = Res.string.core_model_layer_text_description
  @Transient override val icon = Symbols.TextFields

  override fun evaluateAsFlow(layerContext: LayerContext, globals: Globals) = flow {
    emit(null)
    if (!isEnabled.getValue(layerContext, globals)) return@flow
    val evaluated =
      EvaluatedTextLayer(
        id = id,
        parentId = parentId,
        name = name,
        widgetModifiers = widgetModifiers.evaluate(layerContext, globals),
        clickActions = clickActions.evaluate(layerContext, globals),
        textStyle = textStyleSource.getTextStyle(layerContext, globals),
        textColor = textColor.getBrush(layerContext, globals),
        text = text.getValue(layerContext, globals),
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

/** Text layer ready to be rendered */
data class EvaluatedTextLayer(
  override val id: Int,
  override val parentId: Int?,
  override val name: String?,
  override val widgetModifiers: List<WidgetModifier.Evaluated>,
  override val clickActions: List<ClickAction.Evaluated>,
  val textStyle: TextStyle,
  val textColor: Brush,
  val text: String,
) : Layer.Evaluated {
  @Composable
  override fun Render(
    modifier: Modifier,
    renderOption: RenderOption?,
    childrenLayers: List<Layer.Evaluated>,
    onGloballyPositioned: (Int, Rect) -> Unit,
    scope: Any,
  ) {
    Text(
      modifier = createModifier(modifier, renderOption, onGloballyPositioned, scope),
      text = text,
      style = remember(textStyle, textColor) { textStyle.copy(textColor) },
    )
  }
}

@Serializable
sealed interface TextStyleSource {
  companion object {
    val fontSizeRange by lazy { 0f..1_000f }
    val fontWeightRange by lazy { 1.0..1000.0 }
  }

  val displayName: StringResource

  suspend fun getTextStyle(layerContext: LayerContext, globals: Globals): TextStyle

  @Serializable
  data class Local(
    val fontSize: ScriptableSp = ScriptableSp.Fixed(16.sp),
    val fontFile: FontFile = FontFile.System,
    val fontWeight: ScriptableDouble = ScriptableDouble.Fixed(NORMAL_FONT_WEIGHT),
    val textAlignSource: TextAlignSource = TextAlignSource.Start,
  ) : TextStyleSource {
    @Transient override val displayName = Res.string.core_model_text_style_local

    override suspend fun getTextStyle(layerContext: LayerContext, globals: Globals) =
      TextStyle(
        fontSize = fontSize.getValue(layerContext, globals).value.coerceIn(fontSizeRange).sp,
        textAlign = textAlignSource.getTextAlign(),
        fontWeight =
          FontWeight(fontWeight.getValue(layerContext, globals).coerceIn(fontWeightRange).toInt()),
        fontFamily = layerContext.loadFontFamily(fontFile),
      )
  }

  @Serializable
  data class Global(val id: Long) : TextStyleSource {
    @Transient override val displayName = Res.string.core_model_text_style_global

    override suspend fun getTextStyle(layerContext: LayerContext, globals: Globals): TextStyle {
      val globalTextStyle = globals.findTextStyle(id) ?: return TextStyle()
      return layerContext.globalValueCache.getOrPut(globalTextStyle) {
        Logger.d(TAG) { "evaluate global: ${globalTextStyle.id} ${globalTextStyle.label}" }
        globalTextStyle.value.getTextStyle(layerContext, globals)
      } ?: TextStyle()
    }
  }
}

@Serializable
sealed interface TextAlignSource {
  @Serializable
  data object Left : TextAlignSource {
    @Transient override val displayName = Res.string.core_model_text_align_left

    override fun getTextAlign() = TextAlign.Left
  }

  @Serializable
  data object Right : TextAlignSource {
    @Transient override val displayName = Res.string.core_model_text_align_right

    override fun getTextAlign() = TextAlign.Right
  }

  @Serializable
  data object Center : TextAlignSource {
    @Transient override val displayName = Res.string.core_model_text_align_center

    override fun getTextAlign() = TextAlign.Center
  }

  @Serializable
  data object Justify : TextAlignSource {
    @Transient override val displayName = Res.string.core_model_text_align_justify

    override fun getTextAlign() = TextAlign.Justify
  }

  @Serializable
  data object Start : TextAlignSource {
    @Transient override val displayName = Res.string.core_model_text_align_start

    override fun getTextAlign() = TextAlign.Start
  }

  @Serializable
  data object End : TextAlignSource {
    @Transient override val displayName = Res.string.core_model_text_align_end

    override fun getTextAlign() = TextAlign.End
  }

  val displayName: StringResource

  fun getTextAlign(): TextAlign

  companion object {
    fun values(): List<TextAlignSource> = listOf(Left, Right, Center, Justify, Start, End)
  }
}

private const val TAG = "TextLayer"
private const val NORMAL_FONT_WEIGHT = 400.0
