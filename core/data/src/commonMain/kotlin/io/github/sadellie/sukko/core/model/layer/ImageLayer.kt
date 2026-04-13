package io.github.sadellie.sukko.core.model.layer

import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import co.touchlab.kermit.Logger
import coil3.Bitmap
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import google.material.design.symbols.Image
import google.material.design.symbols.Symbols
import io.github.sadellie.sukko.core.designsystem.LocalImageLoader
import io.github.sadellie.sukko.core.model.basic.ClickAction
import io.github.sadellie.sukko.core.model.basic.ContentScaleSource
import io.github.sadellie.sukko.core.model.basic.ImageUriSource
import io.github.sadellie.sukko.core.model.basic.ScriptableBoolean
import io.github.sadellie.sukko.core.model.basic.ScriptableColor
import io.github.sadellie.sukko.core.model.basic.ScriptableDouble
import io.github.sadellie.sukko.core.model.modifier.ColdSizeModifier
import io.github.sadellie.sukko.core.model.modifier.WidgetModifier
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.core_model_layer_image
import io.github.sadellie.sukko.resources.core_model_layer_image_description
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ColdImageLayer(
  override val id: Int,
  override val parentId: Int? = null,
  override val name: String? = null,
  override val widgetModifiers: List<WidgetModifier.Cold> =
    listOf(ColdSizeModifier(0, ScriptableDouble.Fixed(146.0))),
  override val clickActions: List<ClickAction.Cold> = emptyList(),
  override val isEnabled: ScriptableBoolean = ScriptableBoolean.Fixed(true),
  val imageUriSource: ImageUriSource = ImageUriSource.Gallery(null),
  val contentScale: ContentScaleSource = ContentScaleSource.Fit,
  val tint: ScriptableColor = ScriptableColor.FixedCustom(Color.Unspecified),
) : Layer.Cold {
  @Transient override val displayName = Res.string.core_model_layer_image
  @Transient override val displayDescription = Res.string.core_model_layer_image_description
  @Transient override val icon = Symbols.Image

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
  override val parentId: Int? = null,
  override val name: String? = null,
  override val widgetModifiers: List<WidgetModifier.Evaluated> = emptyList(),
  override val clickActions: List<ClickAction.Evaluated> = emptyList(),
  val image: Bitmap? = null,
  val contentScale: ContentScale = ContentScale.Fit,
  val tint: Color? = null,
) : Layer.Evaluated {
  @Composable
  override fun BaseRender(
    modifier: Modifier,
    renderOption: RenderOption,
    childrenLayers: List<Layer.Evaluated>,
    onGloballyPositioned: (Int, LayoutCoordinates) -> Unit,
  ) {
    if (image == null) {
      Spacer(modifier)
      return
    }
    val platformContext = LocalPlatformContext.current
    val imageRequest = remember(image) { ImageRequest.Builder(platformContext).data(image).build() }

    AsyncImage(
      modifier = modifier,
      model = imageRequest,
      imageLoader = LocalImageLoader.current,
      contentScale = contentScale,
      contentDescription = null,
      colorFilter = tint?.let { ColorFilter.tint(tint) },
      onError = { Logger.e(it.result.throwable) { "Failed to load image: $it" } },
    )
  }
}
