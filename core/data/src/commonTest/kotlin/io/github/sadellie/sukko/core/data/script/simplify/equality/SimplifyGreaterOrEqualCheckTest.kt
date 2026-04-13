package io.github.sadellie.sukko.core.data.script.simplify.equality

import io.github.sadellie.sukko.core.data.script.ASTNode
import io.github.sadellie.sukko.core.data.script.FalseNode
import io.github.sadellie.sukko.core.data.script.TrueNode
import io.github.sadellie.sukko.core.data.script.assertSimplifySingleRule
import io.github.sadellie.sukko.core.data.script.simplify.SimplificationType
import kotlin.test.Test

class SimplifyGreaterOrEqualCheckTest {
  @Test fun simplify_greaterOrEqual1() = assertSimplify("2 >= 2", TrueNode)

  @Test fun simplify_greaterOrEqual2() = assertSimplify("1 >= 2", FalseNode)

  @Test fun simplify_greaterOrEqual3() = assertSimplify("\"2\" >= 2", null)

  @Test fun simplify_greaterOrEqual4() = assertSimplify("(2 * 3) >= 6", null)

  @Test fun simplify_greaterOrEqual5() = assertSimplify("(2 * 3) >= (2 * 3)", null)

  @Test fun simplify_greaterOrEqual6() = assertSimplify("true >= true", TrueNode)

  @Test fun simplify_greaterOrEqual7() = assertSimplify("false >= false", TrueNode)

  @Test fun simplify_greaterOrEqual8() = assertSimplify("true >= false", TrueNode)

  @Test fun simplify_greaterOrEqual9() = assertSimplify("false >= true", FalseNode)

  private fun assertSimplify(input: String, expected: ASTNode?) {
    assertSimplifySingleRule(
      rule = SimplifyGreaterOrEqualCheck,
      simplificationType = SimplificationType.GREATER_OR_EQUAL,
      input = input,
      expected = expected,
    )
  }
}
