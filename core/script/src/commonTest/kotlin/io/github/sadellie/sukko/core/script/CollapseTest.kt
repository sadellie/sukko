package io.github.sadellie.sukko.core.script

import io.github.sadellie.sukko.core.script.token.Token3
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CollapseTest {
  private val simplificationContext = ScriptContext(FakeBasicScriptContext)

  @Test
  fun collapse_nothing() {
    val input = NumberNode(123)
    val expected = NumberNode(123)
    val actual = input.collapse(simplificationContext)
    assertEquals(expected, actual)
  }

  @Test
  fun collapse_unaryMinusNumber() {
    val input =
      UnaryOperatorNode(token = Token3.Operator.UnaryMinus, children = listOf(NumberNode(123)))

    val expected = NumberNode(-123)
    val actual = input.collapse(simplificationContext)
    assertEquals(expected, actual)
  }

  @Test
  fun collapse_unaryMinusBoolean() {
    val input = UnaryOperatorNode(token = Token3.Operator.UnaryMinus, children = listOf(FalseNode))
    assertFailsWith<ScriptException.WrongUnary> { input.collapse(simplificationContext) }
  }

  @Test
  fun collapse_unaryNotBoolean() {
    val input = UnaryOperatorNode(token = Token3.Operator.Not, children = listOf(FalseNode))

    val expected = TrueNode
    val actual = input.collapse(simplificationContext)
    assertEquals(expected, actual)
  }

  @Test
  fun collapse_unaryNotNumber() {
    val input = UnaryOperatorNode(token = Token3.Operator.Not, children = listOf(NumberNode(123)))
    assertFailsWith<ScriptException.WrongUnary> { input.collapse(simplificationContext) }
  }

  @Test
  fun collapse_unaryMinusVariable() {
    simplificationContext.variableValueMemory[VariableNode(Token3.Variable("test"))] =
      NumberNode(123)
    val input =
      UnaryOperatorNode(
        token = Token3.Operator.UnaryMinus,
        children = listOf(VariableNode(Token3.Variable("test"))),
      )

    val expected = NumberNode(-123)
    val actual = input.collapse(simplificationContext)
    assertEquals(expected, actual)
  }

  @Test
  fun collapse_plusOperator() {
    // 123+456+789
    val input = PlusNode(PlusNode(NumberNode(123), NumberNode(456)), NumberNode(789))

    val expected = PlusNode(NumberNode(123), NumberNode(456), NumberNode(789))
    val actual = input.collapse(simplificationContext)
    assertEquals(expected, actual)
  }

  @Test
  fun collapse_multiplyOperator() {
    // 123*456*789
    val input = MultiplyNode(MultiplyNode(NumberNode(123), NumberNode(456)), NumberNode(789))

    val expected = MultiplyNode(NumberNode(123), NumberNode(456), NumberNode(789))
    val actual = input.collapse(simplificationContext)
    assertEquals(expected, actual)
  }

  @Test
  fun collapse_replaceVariable() {
    simplificationContext.variableValueMemory[VariableNode(Token3.Variable("a"))] = NumberNode(1)
    simplificationContext.variableValueMemory[VariableNode(Token3.Variable("b"))] = NumberNode(2)
    simplificationContext.variableValueMemory[VariableNode(Token3.Variable("c"))] = NumberNode(3)
    // a = b + c + 4
    val input =
      AssignNode(
        VariableNode(Token3.Variable("a")),
        PlusNode(
          VariableNode(Token3.Variable("b")),
          PlusNode(VariableNode(Token3.Variable("c")), NumberNode(4)),
        ),
      )

    val expected =
      AssignNode(
        VariableNode(Token3.Variable("a")),
        PlusNode(NumberNode(2), NumberNode(3), NumberNode(4)),
      )
    val actual = input.collapse(simplificationContext)
    assertEquals(expected, actual)
  }

  @Test
  fun collapse_replaceVariableNoValue() {
    simplificationContext.variableValueMemory[VariableNode(Token3.Variable("a"))] = NumberNode(1)
    simplificationContext.variableValueMemory[VariableNode(Token3.Variable("b"))] = NumberNode(2)

    assertFailsWith<ScriptException.VariableValueNotFound> {
      // a = b + c + 4
      val input =
        AssignNode(
          VariableNode(Token3.Variable("a")),
          PlusNode(
            VariableNode(Token3.Variable("b")),
            PlusNode(VariableNode(Token3.Variable("c")), NumberNode(4)),
          ),
        )
      input.collapse(simplificationContext)
    }
  }
}
