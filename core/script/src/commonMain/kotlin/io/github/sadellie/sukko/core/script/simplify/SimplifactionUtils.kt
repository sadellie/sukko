package io.github.sadellie.sukko.core.script.simplify

import io.github.sadellie.sukko.core.script.ASTNode
import io.github.sadellie.sukko.core.script.AtomicNode
import io.github.sadellie.sukko.core.script.BracketsNode
import io.github.sadellie.sukko.core.script.ScriptContext
import io.github.sadellie.sukko.core.script.ScriptException
import io.github.sadellie.sukko.core.script.token.Token3

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

// TODO use in other simplifications
internal inline fun <reified T : AtomicNode> ASTNode.getParameter(childIndex: Int): T? {
  val child = this.children[childIndex]
  // not simplified yet
  if (child !is AtomicNode) return null
  // simplified but wrong type
  if (child !is T) throw ScriptException.WrongParameterType(this, T::class, child)
  return child
}

/**
 * Walks from parent to children. First performs [onVisitParent] on [parentNode]. Calls
 * [onVisitParent] on [parentNode]'s children only if parent didn't match the pattern.
 *
 * Returns modified node on first match or null if failed to match pattern for entire tree.
 *
 * @param parentNode Entry point, top of the tree
 * @param onVisitParent Perform any modification in this block. Result will be passed recursively
 *   all the way up. Return null to indicate mismatch and continue visiting other children.
 * @see walkBottomToTop
 */
private fun walkTopToBottom(
  parentNode: ASTNode,
  scriptContext: ScriptContext,
  onVisitParent: (node: ASTNode) -> ASTNode?,
): ASTNode? {
  val parentMatch = onVisitParent(parentNode)
  if (parentMatch != null) return parentMatch

  var childrenWithIndex = parentNode.children.withIndex()

  // control flow
  if (parentNode.token == Token3.Function.If) {
    // for if statements only allow visiting their condition
    childrenWithIndex = childrenWithIndex.take(1)
  }

  for ((index, child) in childrenWithIndex) {
    val simplified = walkTopToBottom(child, scriptContext, onVisitParent)
    if (simplified != null) {
      val updatedChildren = parentNode.children.toMutableList()
      updatedChildren[index] = simplified
      return parentNode.withNewChildren(updatedChildren).collapse(scriptContext)
    }
  }

  return null
}

/**
 * Start visiting nodes from the deepest bracket in expression. Always collapses before returning
 * result.
 *
 * @see walkTopToBottom
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
