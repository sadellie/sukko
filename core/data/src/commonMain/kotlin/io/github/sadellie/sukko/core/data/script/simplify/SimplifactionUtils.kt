package io.github.sadellie.sukko.core.data.script.simplify

import io.github.sadellie.sukko.core.data.script.ASTNode
import io.github.sadellie.sukko.core.data.script.AtomicNode
import io.github.sadellie.sukko.core.data.script.BracketsNode
import io.github.sadellie.sukko.core.data.script.ScriptContext
import io.github.sadellie.sukko.core.data.script.ScriptException
import io.github.sadellie.sukko.core.data.script.token.Token3

internal suspend fun simplifyBottomToTop(
  simplificationType: SimplificationType,
  context: ScriptContext,
  parentNode: ASTNode,
  onVisitParent: suspend (currentNode: ASTNode) -> ASTNode?,
): SimplificationStep? {
  // null if no pattern match report to caller
  val simplifiedTree = walkBottomToTop(context, parentNode, onVisitParent) ?: return null
  // found a pattern and simplified
  return SimplificationStep(simplifiedASTNode = simplifiedTree, type = simplificationType)
}

internal inline fun <reified T : AtomicNode> ASTNode.getParameter(childIndex: Int): T? {
  val child = this.children[childIndex]
  // not simplified yet
  if (child !is AtomicNode) return null
  // simplified but wrong type
  if (child !is T) throw ScriptException.WrongParameterType(this, T::class, child)
  return child
}

/**
 * Start visiting nodes from the deepest bracket in expression. Always collapses before returning
 * result.
 */
private suspend fun walkBottomToTop(
  context: ScriptContext,
  parentNode: ASTNode,
  onVisitParent: suspend (node: ASTNode) -> ASTNode?,
): ASTNode? {
  var sortedChildrenWithIndex =
    parentNode.children
      // index before sorting to keep indexes to modify list correctly
      .withIndex()
      // visit brackets first
      .sortedByDescending { it.value is BracketsNode }

  // control flow
  if (parentNode.token == Token3.Function.If) {
    // for if statements only allow visiting their condition
    sortedChildrenWithIndex = sortedChildrenWithIndex.take(1)
  }

  for ((index, child) in sortedChildrenWithIndex) {
    val simplified = walkBottomToTop(context, child, onVisitParent)
    if (simplified != null) {
      val updatedChildren = parentNode.children.toMutableList()
      updatedChildren[index] = simplified
      return parentNode.withNewChildren(updatedChildren).collapse(context)
    }
  }

  // visited all children and made no modifications, now visit parent
  val visitedParentNode = onVisitParent(parentNode)?.collapse(context)
  if (visitedParentNode != null) return visitedParentNode

  return null
}
