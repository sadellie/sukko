package io.github.sadellie.sukko.core.model.basic

import androidx.compose.ui.text.style.TextAlign
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.core_model_text_align_center
import io.github.sadellie.sukko.resources.core_model_text_align_end
import io.github.sadellie.sukko.resources.core_model_text_align_justify
import io.github.sadellie.sukko.resources.core_model_text_align_left
import io.github.sadellie.sukko.resources.core_model_text_align_right
import io.github.sadellie.sukko.resources.core_model_text_align_start
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.jetbrains.compose.resources.StringResource

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
