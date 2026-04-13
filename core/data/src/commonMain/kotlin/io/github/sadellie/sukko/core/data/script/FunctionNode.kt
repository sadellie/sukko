package io.github.sadellie.sukko.core.data.script

import io.github.sadellie.sukko.core.data.script.token.Token3

internal data class FunctionNode(
  override val token: Token3.Function,
  val namedParameters: Map<String, ASTNode?>,
) : ASTNode {
  constructor(
    token: Token3.Function,
    vararg node: ASTNode,
  ) : this(token, token.parameters.zip(node) { parameter, node -> parameter.name to node }.toMap())

  constructor(
    token: Token3.Function,
    nodes: List<ASTNode>,
  ) : this(token, token.parameters.zip(nodes) { parameter, node -> parameter.name to node }.toMap())

  override val children: List<ASTNode> = namedParameters.values.toList().filterNotNull()

  override fun withNewChildren(children: List<ASTNode>) = FunctionNode(token, children)

  override fun toFormattedString(): String {
    val formattedChildrenString =
      children.joinToString("${Token3.Comma.symbol} ", transform = ASTNode::toFormattedString)
    return "${token.symbol}($formattedChildrenString)"
  }
}
