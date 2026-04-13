package io.github.sadellie.sukko.core.data.script

import io.github.sadellie.sukko.core.data.script.token.Token3

/** Nodes without children, can not be simplified. Usually some number or text. */
internal sealed interface AtomicNode : ASTNode {
  override val children: List<ASTNode>
    get() = emptyList()

  // no children
  override fun withNewChildren(children: List<ASTNode>) = this

  override fun toFormattedString(): String = token.symbol
}

data class TextNode(override val token: Token3.Text) : AtomicNode {
  val value: String
    get() = token.symbol

  constructor(value: String) : this(Token3.Text(value))

  override fun toFormattedString(): String = "\"${token.symbol}\""

  override fun toFinalFormattedString(): String = token.symbol
}

data class NumberNode(
  override val token: Token3.Number,
  val value: Double = token.symbol.toDouble(),
) : AtomicNode {
  constructor(value: Double) : this(Token3.Number(value.toString()), value)

  constructor(value: Int) : this(Token3.Number(value.toString()), value.toDouble())

  override fun toFinalFormattedString(): String = toNumber().toString()

  fun negate() = NumberNode(value.unaryMinus())

  /** Returns double or integer based on decimal points */
  fun toNumber(): Number {
    val fractionalPart = token.symbol.substringAfter(DOT, "")
    // true if no decimal points or all zeros
    val isFractionalPartUseless = fractionalPart.all { it == '0' }
    val isNegative = value < 0
    return if (isFractionalPartUseless) {
      val wholePart = token.symbol.substringBefore(DOT)
      if (isNegative) wholePart.toInt().unaryMinus() else wholePart.toInt()
    } else {
      if (isNegative) token.symbol.toDouble().unaryMinus() else token.symbol.toDouble()
    }
  }
}

internal sealed interface BoolNode : AtomicNode {
  val value: Boolean
}

internal data object TrueNode : BoolNode {
  override val token = Token3.True

  override val value: Boolean
    get() = true
}

internal data object FalseNode : BoolNode {
  override val token = Token3.False

  override val value: Boolean
    get() = false
}

internal data class ConstantNode(override val token: Token3.Const) : AtomicNode

internal data class VariableNode(override val token: Token3.Variable) : AtomicNode {
  val value: String
    get() = token.symbol

  /**
   * Collapsing variable tries to replace itself with actual value that might be in memory. Variable
   * is collapsed only when it is on the right side to make variable values reassignable. This is
   * implemented in [AssignNode].
   *
   * @see [AssignNode.collapse]
   */
  override fun collapse(scriptContext: ScriptContext): ASTNode {
    // try to replace itself with value from memory
    return scriptContext.variableValueMemory[this]
      ?: throw ScriptException.VariableValueNotFound(this)
  }
}
