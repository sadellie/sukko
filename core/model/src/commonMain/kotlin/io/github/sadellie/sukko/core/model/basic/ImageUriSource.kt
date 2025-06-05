package io.github.sadellie.sukko.core.model.basic

import androidx.compose.runtime.Composable
import io.github.sadellie.sukko.core.iconfiles.IconFile
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.LayerContext
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.common_not_selected
import io.github.sadellie.sukko.resources.core_model_image_uri_source_album_cover
import io.github.sadellie.sukko.resources.core_model_image_uri_source_gallery
import io.github.sadellie.sukko.resources.core_model_image_uri_source_gallery_selected
import io.github.sadellie.sukko.resources.core_model_image_uri_source_icon_pack
import io.github.sadellie.sukko.resources.core_model_image_uri_source_link
import io.github.sadellie.sukko.resources.core_model_image_uri_source_player_icon
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Serializable
sealed interface ImageUriSource {
  /** Return actual uri to file. Can be http://, file:// or something else */
  suspend fun getUri(layerContext: LayerContext, globals: Globals): String?

  /**
   * Return uri to locally cached image that was downloaded from [getUri] (in case it was web link)
   */
  suspend fun getLocalUri(layerContext: LayerContext, globals: Globals): String? {
    val uri = getUri(layerContext, globals) ?: return null
    return layerContext.loadAndCacheImage(uri)
  }

  val displayName: StringResource

  @Composable fun displayValue(): String

  companion object {
    fun values(): List<ImageUriSource> =
      listOf(Gallery(), IconPack(), Link(), AlbumCover, PlayerIcon)
  }

  @Serializable
  data class Gallery(val imageUri: String? = null) : ImageUriSource {
    @Transient override val displayName = Res.string.core_model_image_uri_source_gallery

    override suspend fun getUri(layerContext: LayerContext, globals: Globals) = imageUri

    @Composable
    override fun displayValue() =
      stringResource(
        if (imageUri == null) Res.string.common_not_selected
        else Res.string.core_model_image_uri_source_gallery_selected
      )
  }

  @Serializable
  data object AlbumCover : ImageUriSource {
    @Transient override val displayName = Res.string.core_model_image_uri_source_album_cover

    override suspend fun getUri(layerContext: LayerContext, globals: Globals) =
      layerContext.mediaInfoProvider.coverUri

    @Composable override fun displayValue() = stringResource(displayName)
  }

  @Serializable
  data object PlayerIcon : ImageUriSource {
    @Transient override val displayName = Res.string.core_model_image_uri_source_player_icon

    override suspend fun getUri(layerContext: LayerContext, globals: Globals) =
      layerContext.mediaInfoProvider.playerIcon

    @Composable override fun displayValue() = stringResource(displayName)
  }

  @Serializable
  data class Link(val value: ScriptableString = ScriptableString.Fixed("")) : ImageUriSource {
    @Transient override val displayName = Res.string.core_model_image_uri_source_link

    override suspend fun getUri(layerContext: LayerContext, globals: Globals) =
      value.getValue(layerContext, globals)

    @Composable override fun displayValue() = LocalScriptableDisplay.current.displayString(value)
  }

  @Serializable
  data class IconPack(val iconFile: IconFile? = null) : ImageUriSource {
    @Transient override val displayName = Res.string.core_model_image_uri_source_icon_pack

    override suspend fun getUri(layerContext: LayerContext, globals: Globals) =
      iconFile?.getFullPath(layerContext.filesDirPath).toString()

    @Composable
    override fun displayValue() = iconFile?.name ?: stringResource(Res.string.common_not_selected)
  }
}
