package io.github.sadellie.sukko.core.data.script.simplify

import io.github.sadellie.sukko.core.data.script.ASTNode
import io.github.sadellie.sukko.core.data.script.ScriptContext
import io.github.sadellie.sukko.core.data.script.simplify.equality.SimplifyEqualCheck
import io.github.sadellie.sukko.core.data.script.simplify.equality.SimplifyGreaterCheck
import io.github.sadellie.sukko.core.data.script.simplify.equality.SimplifyGreaterOrEqualCheck
import io.github.sadellie.sukko.core.data.script.simplify.equality.SimplifyLessCheck
import io.github.sadellie.sukko.core.data.script.simplify.equality.SimplifyLessOrEqualCheck
import io.github.sadellie.sukko.core.data.script.simplify.equality.SimplifyNotEqualCheck

internal interface SimplificationRule {
  suspend fun simplify(context: ScriptContext, tree: ASTNode): SimplificationStep?

  companion object {
    fun allRules(): List<SimplificationRule> =
      listOf(
        SimplifyUnary,
        SimplifyConstant,
        SimplifyOrBoolean,
        SimplifyAndBoolean,
        SimplifyDivideNumbers,
        SimplifyEqualCheck,
        SimplifyFunction,
        SimplifyGreaterCheck,
        SimplifyGreaterOrEqualCheck,
        SimplifyLessCheck,
        SimplifyLessOrEqualCheck,
        SimplifyMultiplyNumbers,
        SimplifyNotEqualCheck,
        SimplifyPlusNumbers,
      )
  }
}
