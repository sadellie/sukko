package io.github.sadellie.sukko.core.fontfiles

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.toFontFamily
import co.touchlab.kermit.Logger
import io.github.sadellie.sukko.core.designsystem.LocalFilesDirPath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okio.Path

actual class FontFamilyLoader {
  private val cache = mutableMapOf<FontFile, FontFamily>()
  private val mutex = Mutex()

  actual suspend fun loadFromFontFile(fontFile: FontFile, fileDirPath: Path): FontFamily =
    withContext(Dispatchers.IO) {
      // lock for cache
      val fromCache = mutex.withLock { cache[fontFile] }
      if (fromCache != null) return@withContext fromCache

      val fontFamily =
        try {
          when (fontFile) {
            is FontFile.Custom -> Font(fontFile.getFullPath(fileDirPath).toFile()).toFontFamily()
            is FontFile.BuiltIn -> fontFile.getFontFamily()
          }
        } catch (e: Exception) {
          Logger.d(throwable = e, tag = "FontFamilyLoaderImpl") { "Failed to load font from file" }
          return@withContext FontFamily.Default
        }

      mutex.withLock { cache[fontFile] = fontFamily }
      return@withContext fontFamily
    }

  @Composable
  actual fun rememberFontFamily(fontFile: FontFile): FontFamily {
    val filesDirPath = LocalFilesDirPath.current
    val fontFamily = remember(fontFile) { runBlocking { loadFromFontFile(fontFile, filesDirPath) } }
    return fontFamily
  }
}
