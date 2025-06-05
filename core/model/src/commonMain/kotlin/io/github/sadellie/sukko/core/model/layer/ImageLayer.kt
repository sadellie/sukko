package io.github.sadellie.sukko.core.model.layer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.toUri
import google.material.design.symbols.Image
import google.material.design.symbols.Symbols
import io.github.sadellie.sukko.core.designsystem.LocalImageLoader
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.LayerContext
import io.github.sadellie.sukko.core.model.basic.ClickAction
import io.github.sadellie.sukko.core.model.basic.ImageUriSource
import io.github.sadellie.sukko.core.model.basic.ScriptableBoolean
import io.github.sadellie.sukko.core.model.basic.ScriptableColor
import io.github.sadellie.sukko.core.model.basic.ScriptableDouble
import io.github.sadellie.sukko.core.model.basic.ScriptableDp
import io.github.sadellie.sukko.core.model.basic.evaluate
import io.github.sadellie.sukko.core.model.modifier.ColdSizeModifier
import io.github.sadellie.sukko.core.model.modifier.WidgetModifier
import io.github.sadellie.sukko.core.model.modifier.evaluate
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.core_model_content_scale_source_crop
import io.github.sadellie.sukko.resources.core_model_content_scale_source_fill_bounds
import io.github.sadellie.sukko.resources.core_model_content_scale_source_fill_height
import io.github.sadellie.sukko.resources.core_model_content_scale_source_fill_width
import io.github.sadellie.sukko.resources.core_model_content_scale_source_fit
import io.github.sadellie.sukko.resources.core_model_content_scale_source_fixed_scale
import io.github.sadellie.sukko.resources.core_model_content_scale_source_inside
import io.github.sadellie.sukko.resources.core_model_content_scale_source_none
import io.github.sadellie.sukko.resources.core_model_layer_image
import io.github.sadellie.sukko.resources.core_model_layer_image_description
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.jetbrains.compose.resources.StringResource

@Serializable
data class ColdImageLayer(
  override val id: Int,
  override val parentId: Int? = null,
  override val name: String? = null,
  override val widgetModifiers: List<WidgetModifier.Cold> =
    listOf(ColdSizeModifier(0, ScriptableDp.Fixed(146.dp))),
  override val clickActions: List<ClickAction.Cold> = emptyList(),
  override val isEnabled: ScriptableBoolean = ScriptableBoolean.Fixed(true),
  val imageUriSource: ImageUriSource = ImageUriSource.Gallery(null),
  val contentScale: ContentScaleSource = ContentScaleSource.Fit,
  val tint: ScriptableColor = ScriptableColor.FixedCustom(Color.Unspecified),
) : Layer.Cold {
  @Transient override val displayName = Res.string.core_model_layer_image
  @Transient override val displayDescription = Res.string.core_model_layer_image_description
  @Transient override val icon = Symbols.Image

  override fun evaluateAsFlow(layerContext: LayerContext, globals: Globals) = flow {
    emit(null)
    if (!isEnabled.getValue(layerContext, globals)) return@flow
    var evaluated =
      EvaluatedImageLayer(
        id = id,
        parentId = parentId,
        name = name,
        widgetModifiers = widgetModifiers.evaluate(layerContext, globals),
        clickActions = clickActions.evaluate(layerContext, globals),
        // initially empty image to at least occupy the space in layout
        imageUri = null,
        contentScale = contentScale.getContentScale(layerContext, globals),
        tint = tint.getValue(layerContext, globals).takeIf { it.isSpecified },
      )
    emit(evaluated)
    // once image uri was generated (from cache or waited for download), emit final layer
    evaluated = evaluated.copy(imageUri = imageUriSource.getLocalUri(layerContext, globals))
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

data class EvaluatedImageLayer(
  override val id: Int,
  override val parentId: Int?,
  override val name: String?,
  override val widgetModifiers: List<WidgetModifier.Evaluated>,
  override val clickActions: List<ClickAction.Evaluated>,
  val imageUri: String?,
  val contentScale: ContentScale,
  val tint: Color?,
) : Layer.Evaluated {
  @Composable
  override fun Render(
    modifier: Modifier,
    renderOption: RenderOption?,
    childrenLayers: List<Layer.Evaluated>,
    onGloballyPositioned: (Int, Rect) -> Unit,
    scope: Any,
  ) {
    val platformContext = LocalPlatformContext.current
    val imageRequest =
      remember(imageUri) { ImageRequest.Builder(platformContext).data(imageUri?.toUri()).build() }

    AsyncImage(
      modifier = createModifier(modifier, renderOption, onGloballyPositioned, scope),
      model = imageRequest,
      imageLoader = LocalImageLoader.current,
      contentScale = contentScale,
      contentDescription = null,
      colorFilter = tint?.let { ColorFilter.tint(tint) },
    )
  }
}

@Serializable
sealed interface ContentScaleSource {
  companion object {
    fun values(): List<ContentScaleSource> =
      listOf(None, Fit, Crop, FillHeight, FillWidth, FillBounds, Inside, FixedScale())
  }

  val displayName: StringResource

  suspend fun getContentScale(layerContext: LayerContext, globals: Globals): ContentScale

  @Serializable
  data object Crop : ContentScaleSource {
    @Transient
    override val displayName: StringResource = Res.string.core_model_content_scale_source_crop

    override suspend fun getContentScale(layerContext: LayerContext, globals: Globals) =
      ContentScale.Crop
  }

  @Serializable
  data object Fit : ContentScaleSource {
    @Transient
    override val displayName: StringResource = Res.string.core_model_content_scale_source_fit

    override suspend fun getContentScale(layerContext: LayerContext, globals: Globals) =
      ContentScale.Fit
  }

  @Serializable
  data object FillHeight : ContentScaleSource {
    @Transient
    override val displayName: StringResource =
      Res.string.core_model_content_scale_source_fill_height

    override suspend fun getContentScale(layerContext: LayerContext, globals: Globals) =
      ContentScale.FillHeight
  }

  @Serializable
  data object FillWidth : ContentScaleSource {
    @Transient
    override val displayName: StringResource = Res.string.core_model_content_scale_source_fill_width

    override suspend fun getContentScale(layerContext: LayerContext, globals: Globals) =
      ContentScale.FillWidth
  }

  @Serializable
  data object Inside : ContentScaleSource {
    @Transient
    override val displayName: StringResource = Res.string.core_model_content_scale_source_inside

    override suspend fun getContentScale(layerContext: LayerContext, globals: Globals) =
      ContentScale.Inside
  }

  @Serializable
  data object None : ContentScaleSource {
    @Transient
    override val displayName: StringResource = Res.string.core_model_content_scale_source_none

    override suspend fun getContentScale(layerContext: LayerContext, globals: Globals) =
      ContentScale.None
  }

  @Serializable
  data object FillBounds : ContentScaleSource {
    @Transient
    override val displayName: StringResource =
      Res.string.core_model_content_scale_source_fill_bounds

    override suspend fun getContentScale(layerContext: LayerContext, globals: Globals) =
      ContentScale.FillBounds
  }

  @Serializable
  data class FixedScale(val scale: ScriptableDouble = ScriptableDouble.Fixed(1.0)) :
    ContentScaleSource {

    companion object {
      val scaleRange by lazy { 0.0..Double.POSITIVE_INFINITY }
    }

    @Transient
    override val displayName: StringResource =
      Res.string.core_model_content_scale_source_fixed_scale

    override suspend fun getContentScale(layerContext: LayerContext, globals: Globals) =
      androidx.compose.ui.layout.FixedScale(scale.getValue(layerContext, globals).toFloat())
  }
}
