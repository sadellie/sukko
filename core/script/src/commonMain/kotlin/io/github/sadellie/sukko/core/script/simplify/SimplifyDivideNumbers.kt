package io.github.sadellie.sukko.core.script.simplify

import io.github.sadellie.sukko.core.script.ASTNode
import io.github.sadellie.sukko.core.script.DivideNode
import io.github.sadellie.sukko.core.script.NumberNode
import io.github.sadellie.sukko.core.script.ScriptContext

/**
 * - 6 / 2 = 3
 * - 5 / 2 = 2.5
 * - 5 / 2 * 3 = 2.5 * 3
 */
internal val SimplifyDivideNumbers =
  object : SimplificationRule {
    override suspend fun simplify(context: ScriptContext, tree: ASTNode): SimplificationStep? =
      simplifyBottomToTop(SimplificationType.DIVISION_OF_NUMBERS, context, tree) { currentNode ->
        // only work with divide node. skip to child and move to neighbour
        if (currentNode !is DivideNode) return@simplifyBottomToTop null

        // find numbers in this divide node
        val numberNodes = currentNode.children.filterIsInstance<NumberNode>()
        if (numberNodes.size != 2) return@simplifyBottomToTop null
        // divide found number nodes
        val divisionOfNumberNodes = numberNodes[0].value / numberNodes[1].value
        val divisionAsNumberNode = NumberNode(divisionOfNumberNodes)

        // kill parent division node and return result as number node
        return@simplifyBottomToTop divisionAsNumberNode
      }
  }
