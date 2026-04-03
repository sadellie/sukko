package io.github.sadellie.sukko.core.model.basic

import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.core_model_content_scale_source_crop
import io.github.sadellie.sukko.resources.core_model_content_scale_source_fill_bounds
import io.github.sadellie.sukko.resources.core_model_content_scale_source_fill_height
import io.github.sadellie.sukko.resources.core_model_content_scale_source_fill_width
import io.github.sadellie.sukko.resources.core_model_content_scale_source_fit
import io.github.sadellie.sukko.resources.core_model_content_scale_source_fixed_scale
import io.github.sadellie.sukko.resources.core_model_content_scale_source_inside
import io.github.sadellie.sukko.resources.core_model_content_scale_source_none
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.jetbrains.compose.resources.StringResource

@Serializable
sealed interface ContentScaleSource {
  companion object {
    fun values(): List<ContentScaleSource> =
      listOf(None, Fit, Crop, FillHeight, FillWidth, FillBounds, Inside, FixedScale())
  }

  val displayName: StringResource

  @Serializable
  data object Crop : ContentScaleSource {
    @Transient
    override val displayName: StringResource = Res.string.core_model_content_scale_source_crop
  }

  @Serializable
  data object Fit : ContentScaleSource {
    @Transient
    override val displayName: StringResource = Res.string.core_model_content_scale_source_fit
  }

  @Serializable
  data object FillHeight : ContentScaleSource {
    @Transient
    override val displayName: StringResource =
      Res.string.core_model_content_scale_source_fill_height
  }

  @Serializable
  data object FillWidth : ContentScaleSource {
    @Transient
    override val displayName: StringResource = Res.string.core_model_content_scale_source_fill_width
  }

  @Serializable
  data object Inside : ContentScaleSource {
    @Transient
    override val displayName: StringResource = Res.string.core_model_content_scale_source_inside
  }

  @Serializable
  data object None : ContentScaleSource {
    @Transient
    override val displayName: StringResource = Res.string.core_model_content_scale_source_none
  }

  @Serializable
  data object FillBounds : ContentScaleSource {
    @Transient
    override val displayName: StringResource =
      Res.string.core_model_content_scale_source_fill_bounds
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
  }
}
