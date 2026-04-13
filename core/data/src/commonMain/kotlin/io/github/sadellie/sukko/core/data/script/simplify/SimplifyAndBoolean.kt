package io.github.sadellie.sukko.core.data.script.simplify

import io.github.sadellie.sukko.core.data.script.ASTNode
import io.github.sadellie.sukko.core.data.script.AndNode
import io.github.sadellie.sukko.core.data.script.BoolNode
import io.github.sadellie.sukko.core.data.script.FalseNode
import io.github.sadellie.sukko.core.data.script.ScriptContext
import io.github.sadellie.sukko.core.data.script.TrueNode

internal val SimplifyAndBoolean =
  object : SimplificationRule {
    override suspend fun simplify(context: ScriptContext, tree: ASTNode): SimplificationStep? =
      simplifyBottomToTop(SimplificationType.LOGICAL_AND, context, tree) { currentNode ->
        // only work with logical and node. skip to child and move to neighbour
        if (currentNode !is AndNode) return@simplifyBottomToTop null

        // find booleans in this node
        val left = currentNode.getParameter<BoolNode>(0)?.value ?: return@simplifyBottomToTop null
        val right = currentNode.getParameter<BoolNode>(1)?.value ?: return@simplifyBottomToTop null
        // kill parent node and return result as boolean node
        return@simplifyBottomToTop if (left && right) TrueNode else FalseNode
      }
  }
