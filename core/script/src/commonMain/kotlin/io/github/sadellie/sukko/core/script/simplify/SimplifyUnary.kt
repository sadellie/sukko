package io.github.sadellie.sukko.core.script.simplify

import io.github.sadellie.sukko.core.script.ASTNode
import io.github.sadellie.sukko.core.script.BoolNode
import io.github.sadellie.sukko.core.script.FalseNode
import io.github.sadellie.sukko.core.script.NumberNode
import io.github.sadellie.sukko.core.script.ScriptContext
import io.github.sadellie.sukko.core.script.ScriptException
import io.github.sadellie.sukko.core.script.TrueNode
import io.github.sadellie.sukko.core.script.UnaryOperatorNode
import io.github.sadellie.sukko.core.script.token.Token3

/**
 * - 1 = -1
 * - true = false
 */
internal val SimplifyUnary =
  object : SimplificationRule {
    override suspend fun simplify(context: ScriptContext, tree: ASTNode): SimplificationStep? =
      simplifyBottomToTop(SimplificationType.INVISIBLE, context, tree) { currentNode ->
        // only work with plus node. skip to child and move to neighbor
        if (currentNode !is UnaryOperatorNode) return@simplifyBottomToTop null

        when (val child = currentNode.children.firstOrNull()) {
          is NumberNode -> {
            if (currentNode.token !is Token3.Operator.UnaryMinus)
              throw ScriptException.WrongUnary(child, currentNode.token)
            return@simplifyBottomToTop child.negate()
          }
          is BoolNode -> {
            if (currentNode.token !is Token3.Operator.Not)
              throw ScriptException.WrongUnary(child, currentNode.token)
            return@simplifyBottomToTop if (child.toBoolean()) FalseNode else TrueNode
          }
          else -> return@simplifyBottomToTop null
        }
      }
  }
