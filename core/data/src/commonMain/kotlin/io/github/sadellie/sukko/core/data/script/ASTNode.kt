package io.github.sadellie.sukko.core.data.script

import io.github.sadellie.sukko.core.data.script.token.Token3

internal sealed interface ASTNode {
  val token: Token3
  val children: List<ASTNode>

  /**
   * Collapse a tree to prepare it for simplifications. While collapsing and simplification do same
   * thing, collapsing is considered to be a separate step to ensure all simplifications receive
   * stable and expected tree.
   */
  fun collapse(scriptContext: ScriptContext): ASTNode {
    val collapsedChildren = children.map { it.collapse(scriptContext) }
    val updatedNode = this.withNewChildren(collapsedChildren)
    return updatedNode
  }

  /** Creates a copy with provided [children]. */
  fun withNewChildren(children: List<ASTNode>): ASTNode

  /** Format string for displaying in expression */
  fun toFormattedString(): String

  /** Format string for displaying final result. No quotes and similar symbols */
  fun toFinalFormattedString(): String = toFormattedString()

  companion object {
    internal fun buildTrees(tokens: List<Token3>, enableGlobalOverridesAPI: Boolean) =
      ASTBuilder(tokens, enableGlobalOverridesAPI).build()

    internal fun buildTreesAndCollapse(
      tokens: List<Token3>,
      scriptContext: ScriptContext,
      enableGlobalOverridesAPI: Boolean,
    ) = buildTrees(tokens, enableGlobalOverridesAPI).map { it.collapse(scriptContext) }
  }
}
