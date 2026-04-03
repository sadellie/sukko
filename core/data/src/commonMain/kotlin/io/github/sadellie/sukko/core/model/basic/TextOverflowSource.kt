package io.github.sadellie.sukko.core.model.basic

import androidx.compose.ui.text.style.TextOverflow
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.core_model_text_overflow_source_clip
import io.github.sadellie.sukko.resources.core_model_text_overflow_source_ellipsis
import io.github.sadellie.sukko.resources.core_model_text_overflow_source_middle_ellipsis
import io.github.sadellie.sukko.resources.core_model_text_overflow_source_start_ellipsis
import io.github.sadellie.sukko.resources.core_model_text_overflow_source_visible
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.jetbrains.compose.resources.StringResource

@Serializable
sealed interface TextOverflowSource {
  companion object {
    fun values(): List<TextOverflowSource> =
      listOf(Clip, Ellipsis, MiddleEllipsis, Visible, StartEllipsis)
  }

  val displayName: StringResource

  fun getTextOverflow(): TextOverflow

  data object Clip : TextOverflowSource {
    @Transient override val displayName = Res.string.core_model_text_overflow_source_clip

    override fun getTextOverflow() = TextOverflow.Clip
  }

  data object Ellipsis : TextOverflowSource {
    override val displayName = Res.string.core_model_text_overflow_source_ellipsis

    override fun getTextOverflow() = TextOverflow.Ellipsis
  }

  data object MiddleEllipsis : TextOverflowSource {
    override val displayName = Res.string.core_model_text_overflow_source_middle_ellipsis

    override fun getTextOverflow() = TextOverflow.MiddleEllipsis
  }

  data object Visible : TextOverflowSource {
    override val displayName = Res.string.core_model_text_overflow_source_visible

    override fun getTextOverflow() = TextOverflow.Visible
  }

  data object StartEllipsis : TextOverflowSource {
    override val displayName = Res.string.core_model_text_overflow_source_start_ellipsis

    override fun getTextOverflow() = TextOverflow.StartEllipsis
  }
}
