package io.github.sadellie.sukko.core.script

import io.github.sadellie.sukko.core.script.token.Token3

internal class ASTBuilder(private val tokens: List<Token3>) {
  private val operatorStack by lazy { mutableListOf<Token3>() }
  private val outputTree by lazy { mutableListOf<ASTNode>() }

  fun build(): List<ASTNode> {
    if (tokens.isEmpty()) return emptyList()
    for (token in tokens) {
      when (token) {
        Token3.Comma ->
          // pop if the parameter in front is an expression
          // last operator can be a first bracket of a function, do not pop
          // method((a+b),c) <- second left bracket is last in operator stack, but does not belong
          // to method
          if (operatorStack.last() !is Token3.Parentheses.Left) handlePopAndAddToOutput()
        is Token3.Number -> outputTree.add(NumberNode(token))
        is Token3.Text -> outputTree.add(TextNode(token))
        Token3.True -> outputTree.add(TrueNode)
        Token3.False -> outputTree.add(FalseNode)
        is Token3.Const -> outputTree.add(ConstantNode(token))
        is Token3.Function -> operatorStack.add(token)
        is Token3.Operator -> handleOperator(token)
        is Token3.Variable -> outputTree.add(VariableNode(token))
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
    while (
      operator2 is Token3.Operator &&
        (operator2.precedence > operator1.precedence ||
          operator2.precedence == operator1.precedence &&
            operator1.associativity == Token3.Operator.Associativity.LEFT)
    ) {
      // pop operators from stack to output
      handlePopAndAddToOutput()
      operator2 = operatorStack.lastOrNull()
    }
    // other operators were popped, now add this operator on top of stack
    operatorStack.add(operator1)
  }

  private fun handlePopAndAddToOutput() {
    val parentOperator = popLastFromOperatorStack()
    val popped =
      when (parentOperator) {
        is Token3.Function -> {
          // first parameter is always a child in brackets
          val param1 = popLastFromOutputTree().children.first()
          // other parameters are not in brackets
          // -1 because we already have 1st parameter. reversed since we pop from end
          val remainingParams =
            List(parentOperator.parameters - 1) { popLastFromOutputTree() }.reversed()
          FunctionNode(parentOperator, remainingParams + param1)
        }
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

    outputTree.add(popped)
  }
}
