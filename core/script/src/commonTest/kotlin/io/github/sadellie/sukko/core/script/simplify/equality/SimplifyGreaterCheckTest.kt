package io.github.sadellie.sukko.core.script.simplify.equality

import io.github.sadellie.sukko.core.script.ASTNode
import io.github.sadellie.sukko.core.script.FalseNode
import io.github.sadellie.sukko.core.script.TrueNode
import io.github.sadellie.sukko.core.script.assertSimplifySingleRule
import io.github.sadellie.sukko.core.script.simplify.SimplificationType
import kotlin.test.Test

class SimplifyGreaterCheckTest {
  @Test fun simplify_greater1() = assertSimplify("2 > 2", FalseNode)

  @Test fun simplify_greater2() = assertSimplify("1 > 2", FalseNode)

  @Test fun simplify_greater3() = assertSimplify("\"2\" > 2", null)

  @Test fun simplify_greater4() = assertSimplify("(2 * 3) > 6", null)

  @Test fun simplify_greater5() = assertSimplify("(2 * 3) > (2 * 3)", null)

  @Test fun simplify_greater6() = assertSimplify("true > true", FalseNode)

  @Test fun simplify_greater7() = assertSimplify("false > false", FalseNode)

  @Test fun simplify_greater8() = assertSimplify("true > false", TrueNode)

  @Test fun simplify_greater9() = assertSimplify("false > true", FalseNode)

  private fun assertSimplify(input: String, expected: ASTNode?) {
    assertSimplifySingleRule(
      rule = SimplifyGreaterCheck,
      simplificationType = SimplificationType.GREATER,
      input = input,
      expected = expected,
    )
  }
}
