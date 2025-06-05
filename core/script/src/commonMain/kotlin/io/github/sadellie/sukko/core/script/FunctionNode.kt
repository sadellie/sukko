package io.github.sadellie.sukko.core.script

import io.github.sadellie.sukko.core.script.token.Token3

internal data class FunctionNode(
  override val token: Token3.Function,
  override val children: List<ASTNode>,
) : ASTNode {
  constructor(token: Token3.Function, vararg children: ASTNode) : this(token, children.asList())

  override fun withNewChildren(children: List<ASTNode>) = this.copy(children = children)

  override fun toFormattedString(): String {
    val formattedChildrenString =
      children.joinToString("${Token3.Comma.symbol} ", transform = ASTNode::toFormattedString)
    return "${token.symbol}($formattedChildrenString)"
  }
}
