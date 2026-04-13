package io.github.sadellie.sukko.core.data.script.simplify.equality

import io.github.sadellie.sukko.core.data.script.ASTNode
import io.github.sadellie.sukko.core.data.script.BoolNode
import io.github.sadellie.sukko.core.data.script.FalseNode
import io.github.sadellie.sukko.core.data.script.GreaterOrEqualNode
import io.github.sadellie.sukko.core.data.script.NumberNode
import io.github.sadellie.sukko.core.data.script.ScriptContext
import io.github.sadellie.sukko.core.data.script.ScriptException
import io.github.sadellie.sukko.core.data.script.TextNode
import io.github.sadellie.sukko.core.data.script.TrueNode
import io.github.sadellie.sukko.core.data.script.simplify.SimplificationRule
import io.github.sadellie.sukko.core.data.script.simplify.SimplificationStep
import io.github.sadellie.sukko.core.data.script.simplify.SimplificationType
import io.github.sadellie.sukko.core.data.script.simplify.simplifyBottomToTop

internal val SimplifyGreaterOrEqualCheck =
  object : SimplificationRule {
    override suspend fun simplify(context: ScriptContext, tree: ASTNode): SimplificationStep? =
      simplifyBottomToTop(SimplificationType.GREATER_OR_EQUAL, context, tree) walker@{ currentNode
        ->
        // only work with greater or equal node
        if (currentNode !is GreaterOrEqualNode) return@walker null

        // must have exactly 2 children
        if (currentNode.children.size != 2) {
          throw ScriptException.WrongParameterCount(currentNode, 2)
        }
        val child1 = currentNode.children[0]
        val child2 = currentNode.children[1]

        val checkResult =
          when {
            child1 is NumberNode && child2 is NumberNode -> child1.value >= child2.value
            child1 is TextNode && child2 is TextNode -> child1.value >= child2.value
            child1 is BoolNode && child2 is BoolNode -> child1.value >= child2.value
            else -> return@walker null
          }

        val resultAsBoolNode = if (checkResult) TrueNode else FalseNode
        // kill parent and return equality check result
        return@walker resultAsBoolNode
      }
  }
