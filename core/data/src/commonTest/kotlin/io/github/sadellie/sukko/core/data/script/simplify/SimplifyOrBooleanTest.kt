package io.github.sadellie.sukko.core.data.script.simplify

import io.github.sadellie.sukko.core.data.script.ASTNode
import io.github.sadellie.sukko.core.data.script.OrNode
import io.github.sadellie.sukko.core.data.script.ScriptException
import io.github.sadellie.sukko.core.data.script.TrueNode
import io.github.sadellie.sukko.core.data.script.assertSimplifySingleRule
import kotlin.test.Test
import kotlin.test.assertFailsWith

class SimplifyOrBooleanTest {
  @Test fun simplify_logicalOr1() = assertSimplify("true || true", TrueNode)

  @Test fun simplify_logicalOr2() = assertSimplify("true || false", TrueNode)

  @Test
  fun simplify_logicalOr3() = assertSimplify("true || (false || true)", OrNode(TrueNode, TrueNode))

  @Test fun simplify_logicalOr4() = assertSimplify("true || (123 + 456)", null)

  @Test
  fun simplify_logicalOr5() {
    assertFailsWith<ScriptException.WrongParameterType> { assertSimplify("true || 123", null) }
  }

  private fun assertSimplify(input: String, expected: ASTNode?) {
    assertSimplifySingleRule(
      rule = SimplifyOrBoolean,
      simplificationType = SimplificationType.LOGICAL_OR,
      input = input,
      expected = expected,
    )
  }
}
