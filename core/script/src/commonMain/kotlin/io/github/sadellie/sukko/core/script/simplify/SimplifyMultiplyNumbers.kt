package io.github.sadellie.sukko.core.script.simplify

import io.github.sadellie.sukko.core.script.ASTNode
import io.github.sadellie.sukko.core.script.MultiplyNode
import io.github.sadellie.sukko.core.script.NumberNode
import io.github.sadellie.sukko.core.script.ScriptContext

/**
 * - 1 * 2 = 2
 * - 1 * 2 * 3 = 6
 * - 1 * (2 + 3) = null
 */
internal val SimplifyMultiplyNumbers =
  object : SimplificationRule {
    override suspend fun simplify(context: ScriptContext, tree: ASTNode): SimplificationStep? =
      simplifyBottomToTop(SimplificationType.PRODUCT_OF_NUMBERS, context, tree) { currentNode ->
        // only work with multiply node. skip to child and move to neighbour
        if (currentNode !is MultiplyNode) return@simplifyBottomToTop null

        // find numbers in this multiply node
        val numberNodes = currentNode.children.filterIsInstance<NumberNode>()
        if (numberNodes.size < 2) return@simplifyBottomToTop null
        // multiply up all found number nodes
        val productOfNumberNodes = numberNodes.fold(1.0) { left, right -> left * right.toDouble() }
        val productAsNumberNode = NumberNode(productOfNumberNodes)

        val updatedChildren =
          currentNode.children
            // remove number nodes
            .filter { child -> child !is NumberNode }
            // place their product
            .plus(productAsNumberNode)

        return@simplifyBottomToTop currentNode.withNewChildren(updatedChildren)
      }
  }
