package io.github.sadellie.sukko.core.script

import io.github.sadellie.sukko.core.script.token.Token3
import kotlin.reflect.KClass

sealed class ScriptException : Exception() {
  class WrongParameterCount(parentNodeToken: Token3, requiredCount: Int, currentCount: Int) :
    ScriptException() {
    internal constructor(
      parentNode: ASTNode,
      requiredCount: Int,
    ) : this(parentNode.token, requiredCount, parentNode.children.size)

    override val message: String =
      "Not enough parameters in $parentNodeToken. Need $requiredCount, got $currentCount"
  }

  class WrongParameterType(
    parentNodeToken: Token3,
    requiredType: KClass<*>,
    currentTypeToken: Token3,
  ) : ScriptException() {
    internal constructor(
      parentNode: ASTNode,
      requiredType: KClass<*>,
      currentType: ASTNode,
    ) : this(parentNode.token, requiredType, currentType.token)

    override val message: String =
      "Wrong parameter type in $parentNodeToken. Need $requiredType, got $currentTypeToken"
  }

  class VariableNameClash(variableName: String) : ScriptException() {
    override val message = "Variable name $variableName is reserved for internal usage"
  }

  class VariableValueNotFound(nodeToken: Token3) : ScriptException() {
    internal constructor(node: VariableNode) : this(node.token)

    override val message = "Value for $nodeToken is not found"
  }

  internal class WrongReturnType(value: ASTNode?, expectedType: String) : ScriptException() {
    override val message = "Wrong return type. Expected $expectedType. Got: $value"
  }

  internal class WrongUnary(value: ASTNode?, operator: Token3.Operator) : ScriptException() {
    override val message = "Can not apply unary ($operator) collapse to $value"
  }
}
