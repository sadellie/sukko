package io.github.sadellie.sukko.core.data.script.simplify

import io.github.sadellie.sukko.core.data.script.ASTNode
import io.github.sadellie.sukko.core.data.script.NumberNode
import io.github.sadellie.sukko.core.data.script.PlusNode
import io.github.sadellie.sukko.core.data.script.assertSimplifySingleRule
import kotlin.test.Test

class SimplifyPlusNumbersTest {

  @Test fun simplify_plusTwoNumbers1() = assertSimplify("2 + 3", NumberNode(5.0))

  @Test fun simplify_plusTwoNumbers2() = assertSimplify("2 + 3 + 4", NumberNode(9.0))

  @Test fun simplify_plusTwoNumbers3() = assertSimplify("2 + 3 * 4", null)

  @Test fun simplify_plusTwoNumbers4() = assertSimplify("2 / 3 * 4", null)

  @Test
  fun simplify_plusTwoNumbers5() =
    assertSimplify("2 + (3 + 4)", PlusNode(NumberNode(2), NumberNode(7.0)))

  private fun assertSimplify(input: String, expected: ASTNode?) {
    assertSimplifySingleRule(
      rule = SimplifyPlusNumbers,
      simplificationType = SimplificationType.SUM_OF_NUMBERS,
      input = input,
      expected = expected,
    )
  }
}
