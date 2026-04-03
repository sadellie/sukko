package io.github.sadellie.sukko.core.model.basic

import androidx.compose.ui.unit.sp
import io.github.sadellie.sukko.core.fontfiles.FontFile
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.core_model_text_style_global
import io.github.sadellie.sukko.resources.core_model_text_style_local
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.jetbrains.compose.resources.StringResource

@Serializable
sealed interface TextStyleSource {
  companion object {
    val fontSizeRange by lazy { 0f..1_000f }
    val fontWeightRange by lazy { 1.0..1000.0 }
  }

  val displayName: StringResource

  @Serializable
  data class Local(
    val fontSize: ScriptableSp = ScriptableSp.Fixed(16.sp),
    val fontFile: FontFile = FontFile.System,
    val fontStyle: FontStyleSource = FontStyleSource.Normal,
    val fontWeight: ScriptableDouble = ScriptableDouble.Fixed(NORMAL_FONT_WEIGHT),
    val textAlignSource: TextAlignSource = TextAlignSource.Start,
  ) : TextStyleSource {
    @Transient override val displayName = Res.string.core_model_text_style_local
  }

  @Serializable
  data class Global(val id: Long) : TextStyleSource {
    @Transient override val displayName = Res.string.core_model_text_style_global
  }
}

private const val NORMAL_FONT_WEIGHT = 400.0
