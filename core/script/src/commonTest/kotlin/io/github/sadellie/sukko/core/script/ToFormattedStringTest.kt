package io.github.sadellie.sukko.core.script

import kotlin.test.Test
import kotlin.test.assertEquals

class ToFormattedStringTest {
  @Test
  fun toFormattedString_atomicNode() {
    assertFormat("\"text\"", "\"text\"")
    assertFormat("123", "123")
    assertFormat("123.0", "123.0")
    assertFormat("123.456", "123.456")
  }

  @Test
  fun toFormattedString_operatorNode() {
    assertFormat("123 + 456", "123 + 456")
    assertFormat("123 + 456 + 789", "123 + 456 + 789")
    // minus doesn't exist
    assertFormat("123 * 456 - 789", "123 * 456 + -789")
    assertFormat("123 * 456 <= 789", "123 * 456 <= 789")
  }

  @Test
  fun toFormattedString_functionNode() {
    assertFormat("""currentDate("HH")""", """currentDate("HH")""")
    assertFormat(
      """if((1 + 2) * 4, currentDate("HH"), currentDate("mm"))""",
      """if((1 + 2) * 4, currentDate("HH"), currentDate("mm"))""",
    )
  }

  @Test
  fun toFormattedString_unaryOperatorNode() {
    assertFormat("-456", "-456")
    assertFormat("-(123+456)", "-(123 + 456)")
  }

  @Test
  fun toFormattedString_bracketsNode() {
    assertFormat("(123)", "123")
    assertFormat("((123+456) + 789)", "((123 + 456) + 789)")
  }

  // most of the time input is same after processing
  private fun assertFormat(input: String, expected: String) {
    val tree = buildTreeAndCollapse(input)
    val actual = tree.toFormattedString()
    assertEquals(expected, actual)
  }
}
