package io.github.sadellie.sukko.core.data.script.token

import kotlin.test.Test
import kotlin.test.assertEquals

class TokenizerTest {
  @Test
  fun tokenize_empty() {
    assertTokenizer(input = "", expected = emptyList())
  }

  @Test
  fun tokenize_integer() {
    assertTokenizer(input = "123", expected = listOf(Token3.Number("123")))
  }

  @Test
  fun tokenize_unaryMinus() {
    assertTokenizer(
      input = "-123",
      expected = listOf(Token3.Operator.UnaryMinus, Token3.Number("123")),
    )
  }

  @Test
  fun tokenize_currentDate() {
    assertTokenizer(
      input = """currentDate("dd")""",
      expected =
        listOf(
          Token3.Function.CurrentDate,
          Token3.Parentheses.Left,
          Token3.Text("dd"),
          Token3.Parentheses.Right,
        ),
    )
  }

  @Test
  fun tokenize_currentDateWithTimeZone() {
    assertTokenizer(
      input = """currentDateWithTimeZone("HH:mm", "UTC")""",
      expected =
        listOf(
          Token3.Function.CurrentDateWithTimeZone,
          Token3.Parentheses.Left,
          Token3.Text("HH:mm"),
          Token3.Comma,
          Token3.Text("UTC"),
          Token3.Parentheses.Right,
        ),
    )
  }

  @Test
  fun tokenize_batteryLevel() {
    assertTokenizer(
      input = """batteryLevel"%"""",
      expected = listOf(Token3.Const.BatteryLevel, Token3.Text("%")),
    )
  }

  @Test
  fun tokenize_batteryLevelEqual() {
    assertTokenizer(
      input = """batteryLevel"%"==50""",
      expected =
        listOf(
          Token3.Const.BatteryLevel,
          Token3.Text("%"),
          Token3.Operator.Equal,
          Token3.Number("50"),
        ),
    )
  }

  @Test
  fun tokenize_batteryLevelLess() {
    assertTokenizer(
      input = """batteryLevel"%"<50""",
      expected =
        listOf(
          Token3.Const.BatteryLevel,
          Token3.Text("%"),
          Token3.Operator.Less,
          Token3.Number("50"),
        ),
    )
  }

  @Test
  fun tokenize_batteryLevelLessOrEqual() {
    assertTokenizer(
      input = """batteryLevel"%"<=50""",
      expected =
        listOf(
          Token3.Const.BatteryLevel,
          Token3.Text("%"),
          Token3.Operator.LessOrEqual,
          Token3.Number("50"),
        ),
    )
  }

  @Test
  fun tokenize_true1() {
    assertTokenizer(input = """true""", expected = listOf(Token3.True))
  }

  @Test
  fun tokenize_true2() {
    assertTokenizer(
      input = """true == false""",
      expected = listOf(Token3.True, Token3.Operator.Equal, Token3.False),
    )
  }

  @Test
  fun tokenize_true3() {
    assertTokenizer(
      input = """true == "true" + 4""",
      expected =
        listOf(
          Token3.True,
          Token3.Operator.Equal,
          Token3.Text("true"),
          Token3.Operator.Plus,
          Token3.Number("4"),
        ),
    )
  }

  @Test
  fun tokenize_if1() {
    assertTokenizer(
      input = """if(batteryLevel<=30,"low battery", "battery ok") """,
      expected =
        listOf(
          Token3.Function.If,
          Token3.Parentheses.Left,
          Token3.Const.BatteryLevel,
          Token3.Operator.LessOrEqual,
          Token3.Number("30"),
          Token3.Comma,
          Token3.Text("low battery"),
          Token3.Comma,
          Token3.Text("battery ok"),
          Token3.Parentheses.Right,
        ),
    )
  }

  @Test
  fun tokenize_assign() {
    assertTokenizer(
      input = """xtra = 4""",
      expected = listOf(Token3.Variable("xtra"), Token3.Operator.Assign, Token3.Number("4")),
    )
  }

  @Test
  fun tokenize_quotedString() {
    assertTokenizer(input = "\"4\"", expected = listOf(Token3.Text("4")))
  }

  @Test
  fun tokenize_quotedString2() {
    assertTokenizer(
      input =
        """
        "test \"text\""
        """
          .trimIndent(),
      expected = listOf(Token3.Text("test \"text\"")),
    )
  }

  @Test
  fun tokenize_or1() =
    assertTokenizer(
      input = "true || false",
      expected = listOf(Token3.True, Token3.Operator.Or, Token3.False),
    )

  @Test
  fun tokenize_or2() =
    assertTokenizer(
      input = "true || false || true",
      expected =
        listOf(Token3.True, Token3.Operator.Or, Token3.False, Token3.Operator.Or, Token3.True),
    )

  @Test
  fun tokenize_and1() =
    assertTokenizer(
      input = "true && false",
      expected = listOf(Token3.True, Token3.Operator.And, Token3.False),
    )

  @Test
  fun tokenize_and2() =
    assertTokenizer(
      input = "true && false || true",
      expected =
        listOf(Token3.True, Token3.Operator.And, Token3.False, Token3.Operator.Or, Token3.True),
    )

  @Test
  fun tokenize_not1() =
    assertTokenizer(
      input = "true || !false",
      expected = listOf(Token3.True, Token3.Operator.Or, Token3.Operator.Not, Token3.False),
    )

  @Test
  fun tokenize_not2() =
    assertTokenizer(
      input = "true || !false || true",
      expected =
        listOf(
          Token3.True,
          Token3.Operator.Or,
          Token3.Operator.Not,
          Token3.False,
          Token3.Operator.Or,
          Token3.True,
        ),
    )

  private fun assertTokenizer(input: String, expected: List<Token3>) {
    val tokens = tokenize(input)
    assertEquals(expected, tokens)
  }
}
