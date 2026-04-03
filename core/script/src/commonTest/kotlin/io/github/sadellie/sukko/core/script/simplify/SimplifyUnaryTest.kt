package io.github.sadellie.sukko.core.script.simplify

import io.github.sadellie.sukko.core.script.ASTNode
import io.github.sadellie.sukko.core.script.FalseNode
import io.github.sadellie.sukko.core.script.NumberNode
import io.github.sadellie.sukko.core.script.assertSimplifySingleRule
import kotlin.test.Test

class SimplifyUnaryTest {

  @Test
  fun simplifyNumber() {
    assertSimplify(input = "-123", expected = NumberNode(-123))
  }

  @Test
  fun simplifyBoolean() {
    assertSimplify(input = "!true", expected = FalseNode)
  }

  private fun assertSimplify(input: String, expected: ASTNode?) {
    assertSimplifySingleRule(
      rule = SimplifyUnary,
      simplificationType = SimplificationType.INVISIBLE,
      input = input,
      expected = expected,
    )
  }
}
