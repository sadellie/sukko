package io.github.sadellie.sukko.core.data.script

import io.github.sadellie.sukko.core.data.script.token.Token3
import kotlin.reflect.KClass

sealed class ScriptException : Exception() {
  class WrongParameterCount(parentNodeToken: Token3, requiredCount: Int, currentCount: Int) :
    ScriptException() {
    internal constructor(
      parentNode: ASTNode,
      requiredCount: Int,
    ) : this(parentNode.token, requiredCount, parentNode.children.size)

    override val message: String =
      "Wrong parameter count in $parentNodeToken. Need $requiredCount, got $currentCount"
  }

  class WrongParameterName internal constructor(name: String) : ScriptException() {
    override val message: String = "Parameter name $name is invalid"
  }

  class WrongParameterNameType internal constructor(node: ASTNode) : ScriptException() {
    override val message: String = "Parameter name must be of VariableNode type. Received $node"
  }

  class MissingRequiredParameter internal constructor(parameter: Token3.Function.Parameter) :
    ScriptException() {
    override val message: String = "Parameter ${parameter.name} is missing"
  }

  class WrongOptionalParameterDefaultValueType
  internal constructor(parameter: Token3.Function.Parameter.Optional) : ScriptException() {
    override val message: String = "Unexpected optional parameter default value type for $parameter"
  }

  class WrongParameterType
  internal constructor(parentNode: ASTNode, requiredType: KClass<*>, currentType: ASTNode) :
    ScriptException() {
    override val message: String =
      "Wrong parameter type in ${parentNode.token}. Need $requiredType, got ${currentType.token}"
  }

  class VariableNameClash(variableName: String) : ScriptException() {
    override val message = "Variable name $variableName is reserved for internal usage"
  }

  class VariableValueNotFound internal constructor(node: VariableNode) : ScriptException() {
    override val message = "Value for ${node.token} is not found"
  }

  internal class WrongReturnType(value: ASTNode?, expectedType: String) : ScriptException() {
    override val message = "Wrong return type. Expected $expectedType. Got: $value"
  }

  internal class WrongUnary(value: ASTNode?, operator: Token3.Operator) : ScriptException() {
    override val message = "Can not apply unary ($operator) collapse to $value"
  }

  internal class GlobalOverrideAPINotAllowed(method: Token3.Function) : ScriptException() {
    override val message = "$method is disabled"
  }
}
