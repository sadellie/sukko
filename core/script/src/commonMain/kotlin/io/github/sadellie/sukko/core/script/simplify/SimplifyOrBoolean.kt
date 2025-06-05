package io.github.sadellie.sukko.core.script.simplify

import io.github.sadellie.sukko.core.script.ASTNode
import io.github.sadellie.sukko.core.script.BoolNode
import io.github.sadellie.sukko.core.script.FalseNode
import io.github.sadellie.sukko.core.script.OrNode
import io.github.sadellie.sukko.core.script.ScriptContext
import io.github.sadellie.sukko.core.script.TrueNode

internal val SimplifyOrBoolean =
  object : SimplificationRule {
    override suspend fun simplify(context: ScriptContext, tree: ASTNode): SimplificationStep? =
      simplifyBottomToTop(SimplificationType.LOGICAL_OR, context, tree) { currentNode ->
        // only work with logical or node. skip to child and move to neighbour
        if (currentNode !is OrNode) return@simplifyBottomToTop null

        // find booleans in this node
        val left =
          currentNode.getParameter<BoolNode>(0)?.toBoolean() ?: return@simplifyBottomToTop null
        val right =
          currentNode.getParameter<BoolNode>(1)?.toBoolean() ?: return@simplifyBottomToTop null
        // kill parent node and return result as boolean node
        return@simplifyBottomToTop if (left || right) TrueNode else FalseNode
      }
  }
