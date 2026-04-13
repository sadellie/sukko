package io.github.sadellie.sukko.core.data.script.simplify

import io.github.sadellie.sukko.core.data.script.ASTNode
import io.github.sadellie.sukko.core.data.script.FalseNode
import io.github.sadellie.sukko.core.data.script.NumberNode
import io.github.sadellie.sukko.core.data.script.assertSimplifySingleRule
import kotlin.test.Test

class SimplifyUnaryTest {

  @Test
  fun simplifyNumber() {
    assertSimplify(input = "-123", expected = NumberNode(-123.0))
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
