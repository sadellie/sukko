package io.github.sadellie.sukko.core.script.simplify

import io.github.sadellie.sukko.core.script.ASTNode
import io.github.sadellie.sukko.core.script.DivideNode
import io.github.sadellie.sukko.core.script.MultiplyNode
import io.github.sadellie.sukko.core.script.NumberNode
import io.github.sadellie.sukko.core.script.assertSimplifySingleRule
import kotlin.test.Test

class SimplifyDivideNumbersTest {
  @Test fun simplify_divideNumbers1() = assertSimplify("6 / 3", NumberNode(2.0))

  @Test fun simplify_divideNumbers2() = assertSimplify("2 * 6 / 3", null)

  @Test fun simplify_divideNumbers3() = assertSimplify("5 / 2", NumberNode(2.5))

  @Test
  fun simplify_divideNumbers4() =
    assertSimplify("6 / 3 / 2", DivideNode(NumberNode(2.0), NumberNode(2)))

  @Test
  fun simplify_divideNumbers5() =
    assertSimplify("2 * (6 / 2)", MultiplyNode(NumberNode(2), NumberNode(3.0)))

  private fun assertSimplify(input: String, expected: ASTNode?) {
    assertSimplifySingleRule(
      rule = SimplifyDivideNumbers,
      simplificationType = SimplificationType.DIVISION_OF_NUMBERS,
      input = input,
      expected = expected,
    )
  }
}
