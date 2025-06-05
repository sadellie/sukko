package io.github.sadellie.sukko.core.script.simplify.equality

import io.github.sadellie.sukko.core.script.ASTNode
import io.github.sadellie.sukko.core.script.FalseNode
import io.github.sadellie.sukko.core.script.TrueNode
import io.github.sadellie.sukko.core.script.assertSimplifySingleRule
import io.github.sadellie.sukko.core.script.simplify.SimplificationType
import kotlin.test.Test

class SimplifyLessOrEqualCheckTest {
  @Test fun simplify_lessOrEqual1() = assertSimplify("2 <= 2", TrueNode)

  @Test fun simplify_lessOrEqual2() = assertSimplify("1 <= 2", TrueNode)

  @Test fun simplify_lessOrEqual3() = assertSimplify("\"2\" <= 2", null)

  @Test fun simplify_lessOrEqual4() = assertSimplify("(2 * 3) <= 6", null)

  @Test fun simplify_lessOrEqual5() = assertSimplify("(2 * 3) <= (2 * 3)", null)

  @Test fun simplify_lessOrEqual6() = assertSimplify("true <= true", TrueNode)

  @Test fun simplify_lessOrEqual7() = assertSimplify("false <= false", TrueNode)

  @Test fun simplify_lessOrEqual8() = assertSimplify("true <= false", FalseNode)

  @Test fun simplify_lessOrEqual9() = assertSimplify("false <= true", TrueNode)

  private fun assertSimplify(input: String, expected: ASTNode?) {
    assertSimplifySingleRule(
      rule = SimplifyLessOrEqualCheck,
      simplificationType = SimplificationType.LESS_OR_EQUAL,
      input = input,
      expected = expected,
    )
  }
}
