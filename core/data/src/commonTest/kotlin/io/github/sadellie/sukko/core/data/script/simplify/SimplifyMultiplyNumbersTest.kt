package io.github.sadellie.sukko.core.data.script.simplify

import io.github.sadellie.sukko.core.data.script.ASTNode
import io.github.sadellie.sukko.core.data.script.MultiplyNode
import io.github.sadellie.sukko.core.data.script.NumberNode
import io.github.sadellie.sukko.core.data.script.assertSimplifySingleRule
import kotlin.test.Test

class SimplifyMultiplyNumbersTest {
  @Test fun simplify_multiplyNumbers1() = assertSimplify("2 * 3", NumberNode(6.0))

  @Test fun simplify_multiplyNumbers2() = assertSimplify("2 * 3 * 4", NumberNode(24.0))

  @Test fun simplify_multiplyNumbers3() = assertSimplify("2 / 3 + 4", null)

  @Test
  fun simplify_multiplyNumbers4() =
    assertSimplify("2 * (3 * 4)", MultiplyNode(NumberNode(2), NumberNode(12.0)))

  private fun assertSimplify(input: String, expected: ASTNode?) {
    assertSimplifySingleRule(
      rule = SimplifyMultiplyNumbers,
      simplificationType = SimplificationType.PRODUCT_OF_NUMBERS,
      input = input,
      expected = expected,
    )
  }
}
