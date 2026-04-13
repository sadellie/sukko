package io.github.sadellie.sukko.core.data.script

import io.github.sadellie.sukko.core.data.script.simplify.SimplificationRule
import io.github.sadellie.sukko.core.data.script.simplify.SimplificationStep
import io.github.sadellie.sukko.core.data.script.token.tokenize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Evaluates a script and allows it to override globals */
suspend fun evaluateScriptToFormattedString(
  input: String,
  context: ScriptContext,
  enableGlobalOverridesAPI: Boolean,
): String? {
  val evaluationResul =
    evaluateScript(
      input = input,
      context = context,
      enableGlobalOverridesAPI = enableGlobalOverridesAPI,
    )
  return evaluationResul?.toFinalFormattedString()
}

suspend fun evaluateScriptString(input: String, context: ScriptContext): String {
  val evaluation = evaluateScript(input, context)
  return when (evaluation) {
    FalseNode,
    TrueNode,
    is ConstantNode,
    is NumberNode,
    is VariableNode,
    is TextNode -> evaluation.token.symbol
    is BracketsNode,
    is FunctionNode,
    is AssignNode,
    is DivideNode,
    is EqualNode,
    is GreaterNode,
    is GreaterOrEqualNode,
    is LessNode,
    is LessOrEqualNode,
    is MultiplyNode,
    is NotEqualNode,
    is PlusNode,
    is UnaryOperatorNode,
    is OrNode,
    is AndNode,
    null -> throw ScriptException.WrongReturnType(evaluation, "string")
  }
}

suspend fun evaluateScriptBoolean(input: String, context: ScriptContext): Boolean {
  val evaluation = evaluateScript(input, context)
  if (evaluation !is BoolNode) throw ScriptException.WrongReturnType(evaluation, "boolean")
  return evaluation.value
}

suspend fun evaluateScriptDouble(input: String, context: ScriptContext): Double {
  val evaluation = evaluateScript(input, context)
  if (evaluation !is NumberNode) throw ScriptException.WrongReturnType(evaluation, "number")
  return evaluation.value
}

/** Null if empty input or result */
private suspend fun evaluateScript(
  input: String,
  context: ScriptContext,
  enableGlobalOverridesAPI: Boolean = false,
): ASTNode? =
  withContext(Dispatchers.Default) {
    // evaluate each line in input
    val inputLines = input.lines()
    var result: ASTNode? = null

    for (line in inputLines) {
      if (line.isBlank()) continue
      val tokens = tokenize(line)
      // initial collapse to prepare for first simplification and replace variable with actual
      // values
      val trees = ASTNode.buildTrees(tokens, enableGlobalOverridesAPI).map { it.collapse(context) }
      val simplifiedTrees =
        trees.map { tree ->
          simplifyRecursively(context, tree).lastOrNull()?.simplifiedASTNode ?: tree
        }

      when (simplifiedTrees.size) {
        0 -> continue
        1 -> {
          val simplifiedTree = simplifiedTrees.first()
          if (simplifiedTree is AssignNode) {
            // save variable value in memory only if it's Atomic
            val variable = simplifiedTree.children[0]
            if (variable !is VariableNode)
              throw ScriptException.VariableNameClash(variable.token.symbol)
            val variableValue = simplifiedTree.children[1]
            context.variableValueMemory[variable] = variableValue
          } else {
            // update sub-result
            result = simplifiedTree
          }
        }
        else -> result = TextNode(joinTrees(simplifiedTrees))
      }
    }

    return@withContext result
  }

private fun joinTrees(simplifiedTrees: List<ASTNode>) =
  simplifiedTrees.joinToString("") { simplifiedTree -> simplifiedTree.toFinalFormattedString() }

private suspend fun simplifyRecursively(
  context: ScriptContext,
  input: ASTNode,
): List<SimplificationStep> {
  val simplificationSteps = mutableListOf<SimplificationStep>()

  // restart loop if simplification was not performed to avoid missed matches
  // this ensures better control over order of simplifications
  while (true) {
    // null at the start
    val lastTreeState = simplificationSteps.lastOrNull()?.simplifiedASTNode ?: input
    // null when failed to match a pattern
    simplificationSteps.add(matchRule(context, lastTreeState) ?: break)
  }

  return simplificationSteps
}

private suspend fun matchRule(context: ScriptContext, lastTreeState: ASTNode): SimplificationStep? {
  val simplificationRules = SimplificationRule.allRules()
  for (simplifier in simplificationRules) {
    val simplificationStep = simplifier.simplify(context, lastTreeState)
    if (simplificationStep != null) {
      // matched pattern
      return simplificationStep
    }
  }

  return null
}

internal const val DOT = '.'
