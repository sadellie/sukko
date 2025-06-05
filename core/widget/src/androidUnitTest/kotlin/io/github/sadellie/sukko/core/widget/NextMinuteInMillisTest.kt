package io.github.sadellie.sukko.core.widget

import kotlin.test.Test
import kotlin.test.assertEquals

class NextMinuteInMillisTest {

  @Test
  fun text_currentMinuteStartOfMinute() {
    // Fri Jun 20 2025 05:26:00
    val currentTimeMillis = 1750397160000L
    // Fri Jun 20 2025 05:27:00
    val expected = 1750397220000L
    val actual = nextMinuteStartMillis(currentTimeMillis, 1)
    assertEquals(expected, actual)
  }

  @Test
  fun text_currentMinuteStartOfMinutePastMillis() {
    // Fri Jun 20 2025 05:26:00.555
    val currentTimeMillis = 1750397160555L
    // Fri Jun 20 2025 05:27:00
    val expected = 1750397220000L
    val actual = nextMinuteStartMillis(currentTimeMillis, 1)
    assertEquals(expected, actual)
  }

  @Test
  fun text_currentMinuteNearEndOfMinute() {
    // Fri Jun 20 2025 05:26:00.999
    val currentTimeMillis = 1750397219999L
    // Fri Jun 20 2025 05:27:00
    val expected = 1750397220000L
    val actual = nextMinuteStartMillis(currentTimeMillis, 1)
    assertEquals(expected, actual)
  }

  @Test
  fun text_currentMinuteStartOfMinute15() {
    // Fri Jun 20 2025 05:26:00
    // Fri Jun 20 2025 05:27:00
    val currentTimeMillis = 1750397160000L
    val expected = 1750398060000L
    val actual = nextMinuteStartMillis(currentTimeMillis, 15)
    assertEquals(expected, actual)
  }

  @Test
  fun text_currentMinuteStartOfMinutePastMillis15() {
    val currentTimeMillis = 1750397160555L
    val expected = 1750398060000L
    val actual = nextMinuteStartMillis(currentTimeMillis, 15)
    assertEquals(expected, actual)
  }

  @Test
  fun text_currentMinuteNearEndOfMinute15() {
    val currentTimeMillis = 1750397219999L
    val expected = 1750398060000L
    val actual = nextMinuteStartMillis(currentTimeMillis, 15)
    assertEquals(expected, actual)
  }
}
