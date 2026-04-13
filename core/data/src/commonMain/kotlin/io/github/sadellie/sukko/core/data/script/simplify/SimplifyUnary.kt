package io.github.sadellie.sukko.core.data.script.simplify

import io.github.sadellie.sukko.core.data.script.ASTNode
import io.github.sadellie.sukko.core.data.script.BoolNode
import io.github.sadellie.sukko.core.data.script.FalseNode
import io.github.sadellie.sukko.core.data.script.NumberNode
import io.github.sadellie.sukko.core.data.script.ScriptContext
import io.github.sadellie.sukko.core.data.script.ScriptException
import io.github.sadellie.sukko.core.data.script.TrueNode
import io.github.sadellie.sukko.core.data.script.UnaryOperatorNode
import io.github.sadellie.sukko.core.data.script.token.Token3

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
            return@simplifyBottomToTop if (child.value) FalseNode else TrueNode
          }

          else -> return@simplifyBottomToTop null
        }
      }
  }
