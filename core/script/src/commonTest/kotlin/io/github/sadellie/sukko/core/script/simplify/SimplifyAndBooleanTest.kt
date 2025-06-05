package io.github.sadellie.sukko.core.script.simplify

import io.github.sadellie.sukko.core.script.ASTNode
import io.github.sadellie.sukko.core.script.AndNode
import io.github.sadellie.sukko.core.script.FalseNode
import io.github.sadellie.sukko.core.script.ScriptException
import io.github.sadellie.sukko.core.script.TrueNode
import io.github.sadellie.sukko.core.script.assertSimplifySingleRule
import kotlin.test.Test
import kotlin.test.assertFailsWith

class SimplifyAndBooleanTest {
  @Test fun simplify_logicalOr1() = assertSimplify("true && true", TrueNode)

  @Test fun simplify_logicalOr2() = assertSimplify("true && false", FalseNode)

  @Test
  fun simplify_logicalOr3() =
    assertSimplify("true && (false && true)", AndNode(TrueNode, FalseNode))

  @Test fun simplify_logicalOr4() = assertSimplify("true && (123 + 456)", null)

  @Test
  fun simplify_logicalOr5() {
    assertFailsWith<ScriptException.WrongParameterType> { assertSimplify("true && 123", null) }
  }

  private fun assertSimplify(input: String, expected: ASTNode?) {
    assertSimplifySingleRule(
      rule = SimplifyAndBoolean,
      simplificationType = SimplificationType.LOGICAL_AND,
      input = input,
      expected = expected,
    )
  }
}
