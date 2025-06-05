package io.github.sadellie.sukko.core.fontfiles

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import io.github.sadellie.sukko.core.common.notReady
import okio.Path

actual class FontFamilyLoader actual constructor() {
  actual suspend fun loadFromFontFile(fontFile: FontFile, fileDirPath: Path): FontFamily = notReady

  @Composable actual fun rememberFontFamily(fontFile: FontFile): FontFamily = notReady
}
