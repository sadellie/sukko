package io.github.sadellie.sukko.core.fontfiles

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.font_file_cursive
import io.github.sadellie.sukko.resources.font_file_monospace
import io.github.sadellie.sukko.resources.font_file_sans_serif
import io.github.sadellie.sukko.resources.font_file_serif
import io.github.sadellie.sukko.resources.font_file_system
import kotlinx.serialization.Serializable
import okio.Path
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Serializable
sealed interface FontFile {
  val id: String

  @Composable fun toDisplayString(): String

  @Serializable
  sealed interface BuiltIn : FontFile {
    val displayName: StringResource

    fun getFontFamily(): FontFamily

    @Composable override fun toDisplayString() = stringResource(displayName)
  }

  @Serializable
  data object System : BuiltIn {
    override val id = "System"
    override val displayName = Res.string.font_file_system

    override fun getFontFamily() = FontFamily.Default
  }

  @Serializable
  data object Serif : BuiltIn {
    override val id = "Serif"
    override val displayName = Res.string.font_file_serif

    override fun getFontFamily() = FontFamily.Serif
  }

  @Serializable
  data object SansSerif : BuiltIn {
    override val id = "SansSerif"
    override val displayName = Res.string.font_file_sans_serif

    override fun getFontFamily() = FontFamily.SansSerif
  }

  @Serializable
  data object Cursive : BuiltIn {
    override val id = "Cursive"
    override val displayName = Res.string.font_file_cursive

    override fun getFontFamily() = FontFamily.Cursive
  }

  @Serializable
  data object Monospace : BuiltIn {
    override val id = "Monospace"
    override val displayName = Res.string.font_file_monospace

    override fun getFontFamily() = FontFamily.Monospace
  }

  /** @property fileName Name of the font file, not path. For example: file_1.otf */
  @Serializable
  data class Custom(val fileName: String) : FontFile {
    fun getFullPath(filesDirPath: Path) = filesDirPath / DIR_PATH / fileName

    val fileNameWithoutExtension = fileName.substringBefore('.')
    val fileExtension = fileName.substringAfter('.')
    override val id = "custom-$fileNameWithoutExtension"

    @Composable override fun toDisplayString() = fileNameWithoutExtension

    companion object {
      const val DIR_PATH = "fonts"
    }
  }

  companion object {
    fun builtIn(): List<BuiltIn> = listOf(System, Serif, SansSerif, Cursive, Monospace)
  }
}
