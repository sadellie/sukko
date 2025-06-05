package io.github.sadellie.sukko.core.script

import io.github.sadellie.sukko.core.script.token.Token3

internal sealed interface OperatorNode : ASTNode {
  override val token: Token3.Operator

  override fun toFormattedString(): String {
    // format children too

    return children.joinToString(" ${token.symbol} ") { child ->
      val childLatex = child.toFormattedString()

      if (child is OperatorNode && child.token.precedence < token.precedence) {
        // add brackets to preserve precedence
        "(${childLatex})"
      } else {
        childLatex
      }
    }
  }
}

internal data class AssignNode(override val children: List<ASTNode>) : OperatorNode {
  override val token = Token3.Operator.Assign

  constructor(left: ASTNode, right: ASTNode) : this(listOf(left, right))

  override fun withNewChildren(children: List<ASTNode>) = this.copy(children = children)

  override fun collapse(scriptContext: ScriptContext): ASTNode {
    val updatedChildren = children.toMutableList()
    // do not collapse left side to allow reassigning it in script
    updatedChildren[1] = updatedChildren[1].collapse(scriptContext)
    val updatedNode = this.withNewChildren(updatedChildren)
    return updatedNode
  }
}

internal data class PlusNode(override val children: List<ASTNode>) : OperatorNode {
  override val token = Token3.Operator.Plus

  constructor(vararg children: ASTNode) : this(children.asList())

  override fun withNewChildren(children: List<ASTNode>) = this.copy(children = children)

  /**
   * For example (very silly example), both trees represent same expression: "1 + 2 + 3"
   *
   *         +           +    |   +           +
   *        / \         /|\   |  / \         /|\
   *       +   3  ==>  1 2 3  | 1   +  ==>  1 2 3
   *      / \                 |    / \
   *     1   2                |   2   3
   */
  override fun collapse(scriptContext: ScriptContext): ASTNode {
    val collapsedChildren = children.map { it.collapse(scriptContext) }
    // inline child if only one
    if (collapsedChildren.size == 1) return collapsedChildren.first()

    val newChildren = mutableListOf<ASTNode>()
    collapsedChildren.forEach { collapsedChild ->
      if (collapsedChild is PlusNode) {
        // inline if child is plus node like parent
        // kill child and adopt grand children
        newChildren.addAll(collapsedChild.children)
      } else {
        newChildren.add(collapsedChild)
      }
    }

    return this.withNewChildren(newChildren)
  }
}

internal data class MultiplyNode(override val children: List<ASTNode>) : OperatorNode {
  override val token = Token3.Operator.Multiply

  constructor(vararg children: ASTNode) : this(children.asList())

  override fun withNewChildren(children: List<ASTNode>) = this.copy(children = children)

  override fun collapse(scriptContext: ScriptContext): ASTNode {
    val collapsedChildren = children.map { it.collapse(scriptContext) }
    // inline child if only one
    if (collapsedChildren.size == 1) return collapsedChildren.first()

    val newChildren = mutableListOf<ASTNode>()
    collapsedChildren.forEach { collapsedChild ->
      if (collapsedChild is MultiplyNode) {
        // inline if child is plus node like parent
        // kill child and adopt grand children
        newChildren.addAll(collapsedChild.children)
      } else {
        newChildren.add(collapsedChild)
      }
    }

    return this.withNewChildren(newChildren)
  }
}

internal data class UnaryOperatorNode(
  override val token: Token3.Operator,
  override val children: List<ASTNode>,
) : ASTNode {
  constructor(token: Token3.Operator, child: ASTNode) : this(token, listOf(child))

  override fun withNewChildren(children: List<ASTNode>) = this.copy(children = children)

  override fun toFormattedString(): String {
    val childFormatted = children.first().toFormattedString()
    return "${token.symbol}$childFormatted"
  }

  override fun collapse(scriptContext: ScriptContext): ASTNode {
    val child = children.firstOrNull()?.collapse(scriptContext)
    if (child is NumberNode) {
      if (token !is Token3.Operator.UnaryMinus) throw ScriptException.WrongUnary(child, token)
      // negate numbers and omit this unary minus
      return child.negate()
    }

    if (child is BoolNode) {
      if (token !is Token3.Operator.Not) throw ScriptException.WrongUnary(child, token)
      // negate boolean and omit this unary minus
      return if (child.toBoolean()) FalseNode else TrueNode
    }

    return super.collapse(scriptContext)
  }
}

internal data class DivideNode(override val children: List<ASTNode>) : OperatorNode {
  override val token = Token3.Operator.Divide

  constructor(left: ASTNode, right: ASTNode) : this(listOf(left, right))

  override fun withNewChildren(children: List<ASTNode>) = this.copy(children = children)
}

internal data class EqualNode(override val children: List<ASTNode>) : OperatorNode {
  override val token = Token3.Operator.Equal

  constructor(left: ASTNode, right: ASTNode) : this(listOf(left, right))

  override fun withNewChildren(children: List<ASTNode>) = this.copy(children = children)
}

internal data class NotEqualNode(override val children: List<ASTNode>) : OperatorNode {
  override val token = Token3.Operator.NotEqual

  constructor(left: ASTNode, right: ASTNode) : this(listOf(left, right))

  override fun withNewChildren(children: List<ASTNode>) = this.copy(children = children)
}

internal data class GreaterNode(override val children: List<ASTNode>) : OperatorNode {
  override val token = Token3.Operator.Greater

  constructor(left: ASTNode, right: ASTNode) : this(listOf(left, right))

  override fun withNewChildren(children: List<ASTNode>) = this.copy(children = children)
}

internal data class GreaterOrEqualNode(override val children: List<ASTNode>) : OperatorNode {
  override val token = Token3.Operator.GreaterOrEqual

  constructor(left: ASTNode, right: ASTNode) : this(listOf(left, right))

  override fun withNewChildren(children: List<ASTNode>) = this.copy(children = children)
}

internal data class LessNode(override val children: List<ASTNode>) : OperatorNode {
  override val token = Token3.Operator.Less

  constructor(left: ASTNode, right: ASTNode) : this(listOf(left, right))

  override fun withNewChildren(children: List<ASTNode>) = this.copy(children = children)
}

internal data class LessOrEqualNode(override val children: List<ASTNode>) : OperatorNode {
  override val token = Token3.Operator.LessOrEqual

  constructor(left: ASTNode, right: ASTNode) : this(listOf(left, right))

  override fun withNewChildren(children: List<ASTNode>) = this.copy(children = children)
}

internal data class OrNode(override val children: List<ASTNode>) : OperatorNode {
  override val token = Token3.Operator.Or

  constructor(left: ASTNode, right: ASTNode) : this(listOf(left, right))

  override fun withNewChildren(children: List<ASTNode>) = this.copy(children = children)
}

internal data class AndNode(override val children: List<ASTNode>) : OperatorNode {
  override val token = Token3.Operator.And

  constructor(left: ASTNode, right: ASTNode) : this(listOf(left, right))

  override fun withNewChildren(children: List<ASTNode>) = this.copy(children = children)
}
