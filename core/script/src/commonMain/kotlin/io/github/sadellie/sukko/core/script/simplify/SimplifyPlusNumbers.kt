package io.github.sadellie.sukko.core.script.simplify

import io.github.sadellie.sukko.core.script.ASTNode
import io.github.sadellie.sukko.core.script.NumberNode
import io.github.sadellie.sukko.core.script.PlusNode
import io.github.sadellie.sukko.core.script.ScriptContext

/**
 * - 1 + 2 = 3
 * - 1 + 2 + 3 = 6
 * - 1 + (2 + 3) = 1 + 5
 * - 1 - 2 - 3 - 4 = 1 - 9
 * - -1 - 2 - 3 = -6
 */
internal val SimplifyPlusNumbers =
  object : SimplificationRule {
    override suspend fun simplify(context: ScriptContext, tree: ASTNode): SimplificationStep? =
      simplifyBottomToTop(SimplificationType.SUM_OF_NUMBERS, context, tree) { currentNode ->
        // only work with plus node. skip to child and move to neighbour
        if (currentNode !is PlusNode) return@simplifyBottomToTop null

        // find numbers in this plus node
        val numberNodes = currentNode.children.filterIsInstance<NumberNode>()
        if (numberNodes.size < 2) return@simplifyBottomToTop null
        // sum up all found number nodes
        val sumOfNumberNodes = numberNodes.sumOf { numberNode -> numberNode.value }
        val sumAsNumberNode = NumberNode(sumOfNumberNodes)

        val updatedChildren =
          currentNode.children
            // remove number nodes
            .filter { child -> child !is NumberNode }
            // place their sum
            .plus(sumAsNumberNode)

        val updatedNode = currentNode.withNewChildren(updatedChildren)
        return@simplifyBottomToTop updatedNode
      }
  }
