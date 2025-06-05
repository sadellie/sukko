package io.github.sadellie.sukko.core.script.simplify.equality

import io.github.sadellie.sukko.core.script.ASTNode
import io.github.sadellie.sukko.core.script.FalseNode
import io.github.sadellie.sukko.core.script.TrueNode
import io.github.sadellie.sukko.core.script.assertSimplifySingleRule
import io.github.sadellie.sukko.core.script.simplify.SimplificationType
import kotlin.test.Test

class SimplifyEqualCheckTest {
  @Test fun simplify_equal1() = assertSimplify("2 == 2", TrueNode)

  @Test fun simplify_equal2() = assertSimplify("1 == 2", FalseNode)

  @Test fun simplify_equal3() = assertSimplify("\"2\" == 2", null)

  @Test fun simplify_equal4() = assertSimplify("(2 * 3) == 6", null)

  @Test fun simplify_equal5() = assertSimplify("(2 * 3) == (2 * 3)", null)

  @Test fun simplify_equal6() = assertSimplify("true == true", TrueNode)

  @Test fun simplify_equal7() = assertSimplify("false == false", TrueNode)

  @Test fun simplify_equal8() = assertSimplify("true == false", FalseNode)

  private fun assertSimplify(input: String, expected: ASTNode?) {
    assertSimplifySingleRule(
      rule = SimplifyEqualCheck,
      simplificationType = SimplificationType.EQUAL,
      input = input,
      expected = expected,
    )
  }
}
