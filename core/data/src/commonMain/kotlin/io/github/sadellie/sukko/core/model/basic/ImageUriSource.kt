package io.github.sadellie.sukko.core.model.basic

import androidx.compose.runtime.Composable
import io.github.sadellie.sukko.core.iconfiles.IconFile
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
  val displayName: StringResource

  @Composable fun displayValue(): String

  companion object {
    fun values(): List<ImageUriSource> =
      listOf(Gallery(), IconPack(), Link(), AlbumCover, PlayerIcon)
  }

  @Serializable
  data class Gallery(val imageUri: String? = null) : ImageUriSource {
    @Transient override val displayName = Res.string.core_model_image_uri_source_gallery

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

    @Composable override fun displayValue() = stringResource(displayName)
  }

  @Serializable
  data object PlayerIcon : ImageUriSource {
    @Transient override val displayName = Res.string.core_model_image_uri_source_player_icon

    @Composable override fun displayValue() = stringResource(displayName)
  }

  /** Any link: http, https or file */
  @Serializable
  data class Link(val value: ScriptableString = ScriptableString.Fixed("")) : ImageUriSource {
    @Transient override val displayName = Res.string.core_model_image_uri_source_link

    @Composable override fun displayValue() = LocalScriptableDisplay.current.displayString(value)
  }

  @Serializable
  data class IconPack(val iconFile: IconFile? = null) : ImageUriSource {
    @Transient override val displayName = Res.string.core_model_image_uri_source_icon_pack

    @Composable
    override fun displayValue() = iconFile?.name ?: stringResource(Res.string.common_not_selected)
  }
}
