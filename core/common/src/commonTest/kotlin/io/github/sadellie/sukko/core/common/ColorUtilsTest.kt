package io.github.sadellie.sukko.core.common

import androidx.compose.ui.graphics.Color
import kotlin.test.Test
import kotlin.test.assertEquals

class ColorUtilsTest {
  @Test
  fun toHex_testWithAlpha() {
    val input = Color(0xAB123456)
    val expected = "AB123456"
    val actual = input.toHex()
    assertEquals(expected, actual)
  }

  @Test
  fun toHex_testWithoutAlpha() {
    val input = Color(0xFF123456)
    val expected = "123456"
    val actual = input.toHex()
    assertEquals(expected, actual)
  }

  @Test
  fun toHex_testUnspecified() {
    val input = Color.Unspecified
    val expected = "00000000"
    val actual = input.toHex()
    assertEquals(expected, actual)
  }

  @Test
  fun hexToColor_testWithAlpha() {
    val input = "ab123456"
    val expected = Color(0xAB123456)
    val actual = input.hexToColor()
    assertEquals(expected, actual)
  }

  @Test
  fun hexToColor_testWithoutAlpha() {
    val input = "123456"
    val expected = Color(0xFF123456)
    val actual = input.hexToColor()
    assertEquals(expected, actual)
  }

  @Test
  fun hexToColor_testWithoutAlphaWithHashtag() {
    val input = "#123456"
    val expected = Color(0xFF123456)
    val actual = input.hexToColor()
    assertEquals(expected, actual)
  }
}
