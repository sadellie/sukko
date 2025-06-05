package io.github.sadellie.sukko.core.script.simplify.equality

import io.github.sadellie.sukko.core.script.ASTNode
import io.github.sadellie.sukko.core.script.FalseNode
import io.github.sadellie.sukko.core.script.TrueNode
import io.github.sadellie.sukko.core.script.assertSimplifySingleRule
import io.github.sadellie.sukko.core.script.simplify.SimplificationType
import kotlin.test.Test

class SimplifyNotEqualCheckTest {
  @Test fun simplify_notEqual1() = assertSimplify("2 != 2", FalseNode)

  @Test fun simplify_notEqual2() = assertSimplify("1 != 2", TrueNode)

  @Test fun simplify_notEqual3() = assertSimplify("\"2\" != 2", null)

  @Test fun simplify_notEqual4() = assertSimplify("(2 * 3) != 6", null)

  @Test fun simplify_notEqual5() = assertSimplify("(2 * 3) != (2 * 3)", null)

  @Test fun simplify_notEqual6() = assertSimplify("true != true", FalseNode)

  @Test fun simplify_notEqual7() = assertSimplify("false != false", FalseNode)

  @Test fun simplify_notEqual8() = assertSimplify("true != false", TrueNode)

  private fun assertSimplify(input: String, expected: ASTNode?) {
    assertSimplifySingleRule(
      rule = SimplifyNotEqualCheck,
      simplificationType = SimplificationType.NOT_EQUAL,
      input = input,
      expected = expected,
    )
  }
}
