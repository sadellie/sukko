package io.github.sadellie.sukko.core.script.simplify

import io.github.sadellie.sukko.core.script.ASTNode
import io.github.sadellie.sukko.core.script.ScriptContext
import io.github.sadellie.sukko.core.script.simplify.equality.SimplifyEqualCheck
import io.github.sadellie.sukko.core.script.simplify.equality.SimplifyGreaterCheck
import io.github.sadellie.sukko.core.script.simplify.equality.SimplifyGreaterOrEqualCheck
import io.github.sadellie.sukko.core.script.simplify.equality.SimplifyLessCheck
import io.github.sadellie.sukko.core.script.simplify.equality.SimplifyLessOrEqualCheck
import io.github.sadellie.sukko.core.script.simplify.equality.SimplifyNotEqualCheck

internal interface SimplificationRule {
  suspend fun simplify(context: ScriptContext, tree: ASTNode): SimplificationStep?

  companion object {
    fun allRules(): List<SimplificationRule> =
      listOf(
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
