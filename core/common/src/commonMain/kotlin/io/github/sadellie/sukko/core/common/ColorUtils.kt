package io.github.sadellie.sukko.core.common

import androidx.compose.ui.graphics.Color

/** Formats [this] into a hex (like 123456 or 12345678). Will add alpha only if needed. */
fun Color.toHex(): String {
  var result = ""
  if (alpha != 1f) {
    result += this.alpha.asHexString()
  }

  result += this.red.asHexString()
  result += this.green.asHexString()
  result += this.blue.asHexString()
  return result.uppercase()
}

/**
 * Convert a hex into a [Color].
 *
 * Allowed formats:
 * - `123456` no hashtag and no alpha
 * - `#123456` with hashtag and no alpha
 * - `FF123456` no hashtag with alpha
 * - `#FF123456` with hashtag and alpha
 */
fun String.hexToColor(): Color {
  var stringToParse = this.removePrefix("#").uppercase()
  if (stringToParse.length == SHORT_HEX_LENGTH) {
    // hex without alpha 123456 -> FF123546
    stringToParse = "FF$stringToParse"
  }
  val alpha = stringToParse.take(2).toInt(16)
  val red = stringToParse.substring(2, 4).toInt(16)
  val green = stringToParse.substring(4, 6).toInt(16)
  val blue = stringToParse.substring(6, 8).toInt(16)
  return Color(red, green, blue, alpha)
}

@Suppress("MagicNumber")
private fun Float.asHexString(): String = (this * 255).toInt().toByte().toHexString()

/**
 * - short hex example: ABC123
 * - long hex example: FFABC123
 */
private const val SHORT_HEX_LENGTH = 6
