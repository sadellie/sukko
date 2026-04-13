package io.github.sadellie.sukko.core.data.script

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/** From documentation examples. */
class MainEvaluationTest {
  @Test fun evaluate_number1() = assertEvaluation("123", 123.0)

  @Test fun evaluate_number2() = assertEvaluation("456.789", 456.789)

  @Test fun evaluate_number3() = assertEvaluation("2+3.0", 5.0)

  @Test fun evaluate_text1() = assertEvaluation("\"text\"", "text")

  @Test fun evaluate_text2() = assertEvaluation("\"text with spaces\"", "text with spaces")

  @Test fun evaluate_text3() = assertEvaluation("\"123\"", "123")

  @Test fun evaluate_parentheses1() = assertEvaluation("1 + (2 * 3)", 7.0)

  @Test fun evaluate_plus1() = assertEvaluation("1 + 2", 3.0)

  @Test fun evaluate_plus2() = assertEvaluation("\"1\" \"2\"", "12")

  @Test fun evaluate_plus3() = assertEvaluation("(2 * 2) + 4", 8.0)

  @Test fun evaluate_minus1() = assertEvaluation("5 - 2", 3.0)

  @Test fun evaluate_unaryMinus1() = assertEvaluation("- 2 + 5", 3.0)

  @Test fun evaluate_multiply1() = assertEvaluation("2 * 5", 10.0)

  @Test fun evaluate_multiply2() = assertEvaluation("(2 * 10) * 5", 100.0)

  @Test fun evaluate_divide1() = assertEvaluation("5 / 2", 2.5)

  @Test fun evaluate_divide2() = assertEvaluation("(5 * 10) / 2", 25.0)

  @Test fun evaluate_equality1() = assertEvaluation("3 < 5", true)

  @Test fun evaluate_equality2() = assertEvaluation("1 == 3", false)

  @Test fun evaluate_equality3() = assertEvaluation("\"text1\" > \"text\"", true)

  @Test fun evaluate_equality4() = assertEvaluation("\"a\" == \"b\"", false)

  @Test fun evaluate_equality5() = assertEvaluation("(2 + 2) == 4", true)

  @Test fun evaluate_batteryLevel1() = assertEvaluation("batteryLevel / 100", 0.5)

  @Test fun evaluate_batteryLevel2() = assertEvaluation("batteryLevel\"%\"", "50%")

  @Test fun evaluate_batteryStatus1() = assertEvaluation("batteryStatus", "BATTERY_STATUS_CHARGING")

  @Test
  fun evaluate_batteryStatus2() =
    assertEvaluation(
      "if(batteryStatus == \"BATTERY_STATUS_CHARGING\", \"Phone is charging\", \"Phone is not charging\")",
      "Phone is charging",
    )

  @Test fun evaluate_currentDate1() = assertEvaluation("currentDate(\"HH:mm\")", "11:42")

  @Test
  fun evaluate_currentDateWithTimeZone1() =
    assertEvaluation("""currentDateWithTimeZone("HH:mm", "UTC")""", "08:42")

  @Test
  fun evaluate_multipleLinesReturnLastLineResult() =
    assertEvaluation(
      """
      1+1
      "this is a test"
      2+4
      """
        .trimIndent(),
      6.0,
    )

  @Test
  fun evaluate_multipleLinesWithLocalVariable() =
    assertEvaluation(
      """
      x = 1+1
      y = 4
      c = x + y
      c
      """
        .trimIndent(),
      6.0,
    )

  @Test
  fun evaluate_variableNameClash() {
    assertFailsWith<ScriptException.VariableNameClash> {
      assertEvaluation(
        """
        x = 1+1
        batteryLevel = 4
        x + batteryLevel
        """
          .trimIndent(),
        6.0,
      )
    }
  }

  @Test
  fun evaluate_timeManipulation() {
    assertEvaluation("""formatTimestamp(123+456, "HH:mm")""", "03:09")
  }

  @Test
  fun evaluate_andOrOperators1() {
    assertEvaluation("(2 + 2) == 3 || 6 * 2 == 12 && false", false)
  }

  @Test
  fun evaluate_andOrOperators2() {
    assertEvaluation("(2 + 2) == 3 || 6 * 2 == 12 && true", true)
  }

  @Test
  fun evaluate_notBoolean1() {
    assertEvaluation("!false", true)
  }

  @Test
  fun evaluate_notBoolean2() {
    assertEvaluation("!true", false)
  }

  @Test
  internal fun evaluate_unsolvable() {
    assertFailsWith<ScriptException.WrongReturnType> {
      assertEvaluation("""2 + "test" + 4""", null)
    }
  }

  private fun assertEvaluation(input: String, expected: Any?) = runTest {
    val context = fakeScriptContext()
    val actual =
      when (expected) {
        null,
        is String -> evaluateScriptString(input, context)
        is Boolean -> evaluateScriptBoolean(input, context)
        is Double -> evaluateScriptDouble(input, context)
        else -> error("unsupported expected type: $expected")
      }
    assertEquals(expected, actual)
  }
}
