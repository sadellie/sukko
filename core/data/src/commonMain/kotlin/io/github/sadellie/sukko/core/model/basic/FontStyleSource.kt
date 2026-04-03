package io.github.sadellie.sukko.core.model.basic

import androidx.compose.ui.text.font.FontStyle
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.core_model_font_style_italic
import io.github.sadellie.sukko.resources.core_model_font_style_normal
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.jetbrains.compose.resources.StringResource

@Serializable
sealed interface FontStyleSource {
  val displayName: StringResource

  fun getFontStyle(): FontStyle

  companion object {
    fun values(): List<FontStyleSource> = listOf(Normal, Italic)
  }

  @Serializable
  data object Normal : FontStyleSource {
    @Transient override val displayName = Res.string.core_model_font_style_normal

    override fun getFontStyle() = FontStyle.Normal
  }

  @Serializable
  data object Italic : FontStyleSource {
    @Transient override val displayName = Res.string.core_model_font_style_italic

    override fun getFontStyle() = FontStyle.Italic
  }
}
