package io.github.sadellie.sukko.core.fontfiles

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import okio.Path

expect class FontFamilyLoader() {
  suspend fun loadFromFontFile(fontFile: FontFile, fileDirPath: Path): FontFamily

  @Composable fun rememberFontFamily(fontFile: FontFile): FontFamily
}
