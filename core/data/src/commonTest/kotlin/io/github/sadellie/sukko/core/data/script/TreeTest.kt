package io.github.sadellie.sukko.core.data.script

import io.github.sadellie.sukko.core.data.script.token.Token3
import io.github.sadellie.sukko.core.data.script.token.tokenize
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TreeTest {
  @Test
  fun buildTree_empty() {
    assertBuildTree(input = """""", expected = emptyList())
  }

  @Test
  fun buildTree_number() {
    assertBuildTree(input = """123""", expected = listOf(NumberNode(123)))
  }

  @Test
  fun buildTree_pluses() {
    assertBuildTree(
      input = """123+456+789""",
      expected = listOf(PlusNode(PlusNode(NumberNode(123), NumberNode(456)), NumberNode(789))),
    )
  }

  @Test
  fun buildTree_currentDate1() {
    assertBuildTree(
      input = """currentDate("dd")""",
      expected = listOf(FunctionNode(token = Token3.Function.CurrentDate, TextNode("dd"))),
    )
  }

  @Test
  fun buildTree_currentDateWithTimeZone() {
    assertBuildTree(
      input = """currentDateWithTimeZone("HH:mm", "UTC")""",
      expected =
        listOf(
          FunctionNode(
            token = Token3.Function.CurrentDateWithTimeZone,
            TextNode("HH:mm"),
            TextNode("UTC"),
          )
        ),
    )
  }

  @Test
  fun buildTree_batteryWithText() {
    assertBuildTree(
      input = "batteryLevel\"%\"",
      expected = listOf(ConstantNode(Token3.Const.BatteryLevel), TextNode("%")),
    )
  }

  @Test
  fun buildTree_equal1() {
    assertBuildTree(
      input = "1 + 2 == 4",
      expected = listOf(EqualNode(PlusNode(NumberNode(1), NumberNode(2)), NumberNode(4))),
    )
  }

  @Test
  fun buildTree_equal2() {
    assertBuildTree(
      input = "(1 + 2) == 4",
      expected =
        listOf(EqualNode(BracketsNode(PlusNode(NumberNode(1), NumberNode(2))), NumberNode(4))),
    )
  }

  @Test
  fun buildTree_equal3() {
    assertBuildTree(
      input = "1 + 2 == 4 * 7",
      expected =
        listOf(
          EqualNode(
            PlusNode(NumberNode(1), NumberNode(2)),
            MultiplyNode(NumberNode(4), NumberNode(7)),
          )
        ),
    )
  }

  @Test
  fun buildTree_equal4() {
    assertBuildTree(
      input = "1 + 2 + 3 == 4 + 5",
      expected =
        listOf(
          EqualNode(
            PlusNode(PlusNode(NumberNode(1), NumberNode(2)), NumberNode(3)),
            PlusNode(NumberNode(4), NumberNode(5)),
          )
        ),
    )
  }

  @Test
  fun buildTree_equal5() {
    assertBuildTree(
      input = "(1 == 2) == (true == false)",
      expected =
        listOf(
          EqualNode(
            BracketsNode(EqualNode(NumberNode(1), NumberNode(2))),
            BracketsNode(EqualNode(TrueNode, FalseNode)),
          )
        ),
    )
  }

  @Test
  fun buildTree_if1() {
    assertBuildTree(
      input = """if(batteryLevel <= 30, "low battery", "battery ok") """,
      expected =
        listOf(
          FunctionNode(
            Token3.Function.If,
            LessOrEqualNode(ConstantNode(Token3.Const.BatteryLevel), NumberNode(30)),
            TextNode("low battery"),
            TextNode("battery ok"),
          )
        ),
    )
  }

  @Test
  fun buildTree_if2() {
    assertBuildTree(
      input = """if(batteryLevel <= 30, 72 + 34, 123 * 213) """,
      expected =
        listOf(
          FunctionNode(
            Token3.Function.If,
            LessOrEqualNode(ConstantNode(Token3.Const.BatteryLevel), NumberNode(30)),
            PlusNode(NumberNode(72), NumberNode(34)),
            MultiplyNode(NumberNode(123), NumberNode(213)),
          )
        ),
    )
  }

  @Test
  fun buildTree_if3() {
    assertBuildTree(
      input = """if(batteryLevel <= 30, (72 + 34), 123 * 213) """,
      expected =
        listOf(
          FunctionNode(
            Token3.Function.If,
            LessOrEqualNode(ConstantNode(Token3.Const.BatteryLevel), NumberNode(30)),
            BracketsNode(PlusNode(NumberNode(72), NumberNode(34))),
            MultiplyNode(NumberNode(123), NumberNode(213)),
          )
        ),
    )
  }

  @Test
  fun buildTree_assign() {
    assertBuildTree(
      input =
        """
        x = 4
        """
          .trimIndent(),
      expected = listOf(AssignNode(VariableNode(Token3.Variable("x")), NumberNode(4))),
    )
  }

  @Test
  fun buildTree_or1() {
    assertBuildTree(
      input =
        """
        (2 + 2) == 3 || 6 * 2 == 12
        """
          .trimIndent(),
      expected =
        listOf(
          OrNode(
            EqualNode(BracketsNode(PlusNode(NumberNode(2), NumberNode(2))), NumberNode(3)),
            EqualNode(MultiplyNode(NumberNode(6), NumberNode(2)), NumberNode(12)),
          )
        ),
    )
  }

  @Test
  fun buildTree_and1() {
    assertBuildTree(
      input =
        """
        (2 + 2) == 3 || 6 * 2 == 12 && false
        """
          .trimIndent(),
      expected =
        listOf(
          OrNode(
            EqualNode(BracketsNode(PlusNode(NumberNode(2), NumberNode(2))), NumberNode(3)),
            AndNode(
              EqualNode(MultiplyNode(NumberNode(6), NumberNode(2)), NumberNode(12)),
              FalseNode,
            ),
          )
        ),
    )
  }

  @Test
  fun buildTree_not1() {
    assertBuildTree(
      input =
        """
        !false
        """
          .trimIndent(),
      expected = listOf(UnaryOperatorNode(Token3.Operator.Not, FalseNode)),
    )
  }

  @Test
  fun buildTree_unaryMinus1() {
    assertBuildTree(
      input =
        """
        -(123+456)
        """
          .trimIndent(),
      expected =
        listOf(
          UnaryOperatorNode(
            Token3.Operator.UnaryMinus,
            BracketsNode(PlusNode(NumberNode(123), NumberNode(456))),
          )
        ),
    )
  }

  @Test
  fun buildTree_namedParameter() {
    assertBuildTree(
      input =
        """
        colorScheme("colorName", source = "source", scheme = "scheme")
        """
          .trimIndent(),
      expected =
        listOf(
          FunctionNode(
            Token3.Function.ColorScheme,
            TextNode("colorName"),
            TextNode("source"),
            TextNode("scheme"),
          )
        ),
    )
  }

  @Test
  fun buildTree_namedParameterOutOfOrder() {
    assertBuildTree(
      input =
        """
        colorScheme(source = "source", colorName = "colorName", scheme = "scheme")
        """
          .trimIndent(),
      expected =
        listOf(
          FunctionNode(
            Token3.Function.ColorScheme,
            TextNode("colorName"),
            TextNode("source"),
            TextNode("scheme"),
          )
        ),
    )
  }

  @Test
  fun buildTree_namedParameterInvalid() {
    assertFailsWith<ScriptException.WrongParameterName> {
      assertBuildTree(
        input =
          """
          colorScheme(source = "source", colorName = "colorName", invalid = 123)
          """
            .trimIndent(),
        expected = listOf(),
      )
    }
  }

  @Test
  fun buildTree_noOptionalParameter() {
    assertBuildTree(
      input =
        """
        colorScheme("colorName", globalString(1))
        """
          .trimIndent(),
      expected =
        listOf(
          FunctionNode(
            Token3.Function.ColorScheme,
            TextNode("colorName"),
            FunctionNode(Token3.Function.GetGlobalString, NumberNode(1)),
            TextNode("EXPRESSIVE"),
          )
        ),
    )
  }

  @Test
  fun buildTree_nestedFunctions() {
    assertBuildTree(
      input =
        """
        "colors: " colorScheme("colorName", globalString(1), "scheme")
        """
          .trimIndent(),
      expected =
        listOf(
          TextNode("colors: "),
          FunctionNode(
            Token3.Function.ColorScheme,
            TextNode("colorName"),
            FunctionNode(Token3.Function.GetGlobalString, NumberNode(1)),
            TextNode("scheme"),
          ),
        ),
    )
  }

  private fun assertBuildTree(input: String, expected: List<ASTNode>) {
    val tokens = tokenize(input)
    val actual = ASTNode.buildTrees(tokens = tokens, enableGlobalOverridesAPI = false)
    assertEquals(expected, actual)
  }
}
