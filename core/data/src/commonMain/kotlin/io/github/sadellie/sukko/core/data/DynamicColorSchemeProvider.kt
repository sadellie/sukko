package io.github.sadellie.sukko.core.data

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import io.github.sadellie.sukko.core.common.toHex
import io.github.sadellie.sukko.core.model.basic.M3Color

interface DynamicColorSchemeProvider {
  fun extractHexFromSystemColorScheme(m3ColorName: String): String

  suspend fun extractHexFromImageColorScheme(m3ColorName: String, imageUri: String): String

  fun getColorFromSystemColorScheme(m3Color: M3Color): Color

  /**
   * Returns hex color from color scheme. Pass [M3Color.name].
   *
   * Empty string is ONLY to simplify test setups. Actual implementation never returns empty
   * strings.
   */
  fun extractHexFromColorScheme(m3ColorName: String, colorScheme: ColorScheme): String {
    val m3Color = M3Color.valueOf(m3ColorName)
    val color = m3Color.extractFromScheme(colorScheme)
    val colorHex = color.toHex()
    return colorHex
  }
}
