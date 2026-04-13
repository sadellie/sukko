package io.github.sadellie.sukko.core.data.script.simplify.equality

import io.github.sadellie.sukko.core.data.script.ASTNode
import io.github.sadellie.sukko.core.data.script.FalseNode
import io.github.sadellie.sukko.core.data.script.TrueNode
import io.github.sadellie.sukko.core.data.script.assertSimplifySingleRule
import io.github.sadellie.sukko.core.data.script.simplify.SimplificationType
import kotlin.test.Test

class SimplifyLessCheckTest {
  @Test fun simplify_less1() = assertSimplify("2 < 2", FalseNode)

  @Test fun simplify_less2() = assertSimplify("1 < 2", TrueNode)

  @Test fun simplify_less3() = assertSimplify("\"2\" < 2", null)

  @Test fun simplify_less4() = assertSimplify("(2 * 3) < 6", null)

  @Test fun simplify_less5() = assertSimplify("(2 * 3) < (2 * 3)", null)

  @Test fun simplify_less6() = assertSimplify("true < true", FalseNode)

  @Test fun simplify_less7() = assertSimplify("false < false", FalseNode)

  @Test fun simplify_less8() = assertSimplify("false < true", TrueNode)

  @Test fun simplify_less9() = assertSimplify("true < false", FalseNode)

  private fun assertSimplify(input: String, expected: ASTNode?) {
    assertSimplifySingleRule(
      rule = SimplifyLessCheck,
      simplificationType = SimplificationType.LESS,
      input = input,
      expected = expected,
    )
  }
}
