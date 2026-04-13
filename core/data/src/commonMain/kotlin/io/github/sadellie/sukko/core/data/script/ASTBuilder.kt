package io.github.sadellie.sukko.core.data.script

import io.github.sadellie.sukko.core.data.script.token.Token3

internal class ASTBuilder(
  private val tokens: List<Token3>,
  private val enableGlobalOverridesAPI: Boolean,
) {
  private val operatorStack by lazy { mutableListOf<Token3>() }
  private val outputTree by lazy { mutableListOf<ASTNode>() }
  private val argCounterStack = mutableListOf<Int>()

  fun build(): List<ASTNode> {
    if (tokens.isEmpty()) return emptyList()
    for (token in tokens) {
      when (token) {
        Token3.Comma -> {
          // comma after closing bracket "method((a+b), c)"
          // expression (a+b) is a first parameter and needs to be sent to output
          // if not popped it will lead to "b+c" in tree since they are 2 last nodes in output stack
          if (operatorStack.last() !is Token3.Parentheses.Left) {
            handlePopAndAddToOutput()
          }
          // update counter if initialized
          val topCounter = argCounterStack.lastOrNull()
          if (topCounter != null) {
            argCounterStack[argCounterStack.lastIndex] = topCounter + 1
          }
        }
        is Token3.Number -> addToOutputTree(NumberNode(token))
        is Token3.Text -> addToOutputTree(TextNode(token))
        Token3.True -> addToOutputTree(TrueNode)
        Token3.False -> addToOutputTree(FalseNode)
        is Token3.Const -> addToOutputTree(ConstantNode(token))
        is Token3.Function -> {
          if (token is Token3.GlobalOverrideAPI && !enableGlobalOverridesAPI)
            throw ScriptException.GlobalOverrideAPINotAllowed(token)
          // start new counter
          argCounterStack.add(0)
          operatorStack.add(token)
        }
        is Token3.Operator -> handleOperator(token)
        is Token3.Variable -> addToOutputTree(VariableNode(token))
        Token3.Parentheses.Left -> operatorStack.add(token)
        Token3.Parentheses.Right -> handleRightParen()
      }
    }

    while (operatorStack.isNotEmpty()) {
      require(operatorStack.lastOrNull() !is Token3.Parentheses.Left) { "Missing right bracket" }
      handlePopAndAddToOutput()
    }

    return outputTree
  }

  private fun popLastFromOperatorStack(): Token3 = operatorStack.removeAt(operatorStack.lastIndex)

  private fun popLastFromOutputTree(): ASTNode = outputTree.removeAt(outputTree.lastIndex)

  private fun addToOutputTree(node: ASTNode) {
    outputTree.add(node)
  }

  private fun handleRightParen() {
    // end of expression in bracket, build tree from whatever is in stack
    var topOfStack = operatorStack.lastOrNull()
    while (topOfStack != Token3.Parentheses.Left) {
      // null only when left parent, but should not be possible in this loop
      handlePopAndAddToOutput()
      topOfStack = operatorStack.lastOrNull()
    }
    // used all nodes in stack in reached start of the expression in brackets (walked backwards)
    require(operatorStack.last() == Token3.Parentheses.Left)
    handlePopAndAddToOutput()
    // pushed everything in brackets, now inline bracket's children in function if needed
    topOfStack = operatorStack.lastOrNull()
    if (topOfStack is Token3.Function) {
      require(outputTree.last() is BracketsNode) { "Function is missing brackets" }
      handlePopAndAddToOutput()
    }
  }

  private fun handleOperator(operator1: Token3.Operator) {
    var operator2 = operatorStack.lastOrNull()
    while (shouldPopOperator(operator1, operator2)) {
      // pop operators from stack to output
      handlePopAndAddToOutput()
      operator2 = operatorStack.lastOrNull()
    }
    // other operators were popped, now add this operator on top of stack
    operatorStack.add(operator1)
  }

  private fun shouldPopOperator(operator1: Token3.Operator, operator2: Token3?): Boolean {
    if (operator2 !is Token3.Operator) return false
    return (operator2.precedence > operator1.precedence) ||
      ((operator2.precedence == operator1.precedence) &&
        (operator1.associativity == Token3.Operator.Associativity.LEFT))
  }

  private fun handlePopAndAddToOutput() {
    val parentOperator = popLastFromOperatorStack()
    val popped =
      when (parentOperator) {
        is Token3.Function -> handlePopFunction(parentOperator)
        is Token3.Operator.UnaryMinus -> {
          val child = popLastFromOutputTree()
          UnaryOperatorNode(parentOperator, child)
        }
        is Token3.Operator.Minus -> {
          val right = popLastFromOutputTree()
          val left = popLastFromOutputTree()
          // 1 - 2 = 1 + -2
          PlusNode(left, UnaryOperatorNode(Token3.Operator.UnaryMinus, right))
        }
        is Token3.Operator.Assign -> {
          val right = popLastFromOutputTree()
          val left = popLastFromOutputTree()
          AssignNode(left, right)
        }
        is Token3.Operator.Plus -> {
          val right = popLastFromOutputTree()
          val left = popLastFromOutputTree()
          PlusNode(listOf(left, right))
        }
        is Token3.Operator.Multiply -> {
          val right = popLastFromOutputTree()
          val left = popLastFromOutputTree()
          MultiplyNode(left, right)
        }
        is Token3.Operator.Divide -> {
          val right = popLastFromOutputTree()
          val left = popLastFromOutputTree()
          DivideNode(left, right)
        }
        Token3.Operator.Equal -> {
          val right = popLastFromOutputTree()
          val left = popLastFromOutputTree()
          EqualNode(left, right)
        }
        Token3.Operator.NotEqual -> {
          val right = popLastFromOutputTree()
          val left = popLastFromOutputTree()
          NotEqualNode(left, right)
        }
        Token3.Operator.Greater -> {
          val right = popLastFromOutputTree()
          val left = popLastFromOutputTree()
          GreaterNode(left, right)
        }
        Token3.Operator.GreaterOrEqual -> {
          val right = popLastFromOutputTree()
          val left = popLastFromOutputTree()
          GreaterOrEqualNode(left, right)
        }
        Token3.Operator.Less -> {
          val right = popLastFromOutputTree()
          val left = popLastFromOutputTree()
          LessNode(left, right)
        }
        Token3.Operator.LessOrEqual -> {
          val right = popLastFromOutputTree()
          val left = popLastFromOutputTree()
          LessOrEqualNode(left, right)
        }
        is Token3.Operator.Or -> {
          val right = popLastFromOutputTree()
          val left = popLastFromOutputTree()
          OrNode(left, right)
        }
        is Token3.Operator.And -> {
          val right = popLastFromOutputTree()
          val left = popLastFromOutputTree()
          AndNode(left, right)
        }
        is Token3.Operator.Not -> {
          val child = popLastFromOutputTree()
          UnaryOperatorNode(parentOperator, child)
        }
        is Token3.Parentheses.Left -> {
          val child = popLastFromOutputTree()
          BracketsNode(child)
        }
        Token3.Parentheses.Right,
        is Token3.Text,
        is Token3.Const,
        is Token3.Number,
        is Token3.Variable,
        Token3.True,
        Token3.False,
        Token3.Comma -> error("Not allowed to pop for: $parentOperator")
      }

    addToOutputTree(popped)
  }

  private fun handlePopFunction(token: Token3.Function): FunctionNode {
    // +1 because argCounterStack increases on comma tokens:
    // function(param1, param2) <- 1 comma for method with 2 parameters
    val paramCount = argCounterStack.last() + 1
    // reversed since we pop from end
    val allParams = List(paramCount) { popLastFromOutputTree() }.reversed()
    val namedParams = mutableMapOf<String, ASTNode?>()

    if (allParams.size > token.parametersCount)
      throw ScriptException.WrongParameterCount(token, token.parametersCount, allParams.size)

    val validParamNames = token.parameters.map { it.name }

    // process parameters from input
    allParams.forEachIndexed { index, node ->
      // last parameter is always a child in brackets
      val param = if (index == allParams.lastIndex) node.children.first() else node

      val paramName =
        if (param is AssignNode) {
          (param.left as? VariableNode)?.value
            ?: throw ScriptException.WrongParameterNameType(param.left)
        } else {
          // same order as in function definition
          token.parameters[index].name
        }
      if (paramName !in validParamNames) throw ScriptException.WrongParameterName(paramName)

      val paramValue = if (param is AssignNode) param.right else param
      namedParams[paramName] = paramValue
    }

    // validate parameters from function definition
    token.parameters.forEach { parameter ->
      if (parameter.name in namedParams) return@forEach
      // missing parameter
      when (parameter) {
        is Token3.Function.Parameter.Required ->
          throw ScriptException.MissingRequiredParameter(parameter)

        is Token3.Function.Parameter.Optional ->
          namedParams[parameter.name] = optionalParameterDefaultValueToNode(parameter)
      }
    }

    argCounterStack.removeLast()

    return FunctionNode(token, namedParams)
  }

  private fun optionalParameterDefaultValueToNode(
    parameter: Token3.Function.Parameter.Optional
  ): ASTNode? =
    when (parameter.defaultValue) {
      is Token3.Number -> NumberNode(parameter.defaultValue)
      is Token3.Text -> TextNode(parameter.defaultValue)
      is Token3.True -> TrueNode
      is Token3.False -> FalseNode
      null -> null
      else -> throw ScriptException.WrongOptionalParameterDefaultValueType(parameter)
    }
}
