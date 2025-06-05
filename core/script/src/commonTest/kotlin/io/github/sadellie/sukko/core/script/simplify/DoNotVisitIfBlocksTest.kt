package io.github.sadellie.sukko.core.script.simplify

import io.github.sadellie.sukko.core.script.ASTNode
import io.github.sadellie.sukko.core.script.FunctionNode
import io.github.sadellie.sukko.core.script.NumberNode
import io.github.sadellie.sukko.core.script.PlusNode
import io.github.sadellie.sukko.core.script.assertSimplifySingleRule
import io.github.sadellie.sukko.core.script.token.Token3
import kotlin.test.Test

class DoNotVisitIfBlocksTest {

  @Test
  fun doNotVisitIf_test1() {
    // visit condition
    assertSimplify(
      input = "if(1 + 2, 4, 5 + 6)",
      expected =
        FunctionNode(
          Token3.Function.If,
          NumberNode(3.0),
          NumberNode(4),
          PlusNode(NumberNode(5), NumberNode(6)),
        ),
    )
  }

  @Test
  fun doNotVisitIf_test2() {
    // do not visit ifTrue and ifFalse parameters
    assertSimplify(input = "if(1, 2 + 3, 4 + 5)", expected = null)
  }

  @Test
  fun doNotVisitIf_test3() {
    // skip if condition is not simplified yet
    assertSimplify(input = "if(1 > 3, 4, 5 + 6)", expected = null)
  }

  private fun assertSimplify(input: String, expected: ASTNode?) {
    assertSimplifySingleRule(
      rule = SimplifyPlusNumbers,
      simplificationType = SimplificationType.SUM_OF_NUMBERS,
      input = input,
      expected = expected,
    )
  }
}
