package io.github.sadellie.sukko.core.script.simplify.equality

import io.github.sadellie.sukko.core.script.ASTNode
import io.github.sadellie.sukko.core.script.BoolNode
import io.github.sadellie.sukko.core.script.EqualNode
import io.github.sadellie.sukko.core.script.FalseNode
import io.github.sadellie.sukko.core.script.NumberNode
import io.github.sadellie.sukko.core.script.ScriptContext
import io.github.sadellie.sukko.core.script.ScriptException
import io.github.sadellie.sukko.core.script.TextNode
import io.github.sadellie.sukko.core.script.TrueNode
import io.github.sadellie.sukko.core.script.simplify.SimplificationRule
import io.github.sadellie.sukko.core.script.simplify.SimplificationStep
import io.github.sadellie.sukko.core.script.simplify.SimplificationType
import io.github.sadellie.sukko.core.script.simplify.simplifyBottomToTop

internal val SimplifyEqualCheck =
  object : SimplificationRule {
    override suspend fun simplify(context: ScriptContext, tree: ASTNode): SimplificationStep? =
      simplifyBottomToTop(SimplificationType.EQUAL, context, tree) walker@{ currentNode ->
        // only work with equal node
        if (currentNode !is EqualNode) return@walker null

        // must have exactly 2 children
        if (currentNode.children.size != 2) {
          throw ScriptException.WrongParameterCount(currentNode, 2)
        }
        val child1 = currentNode.children[0]
        val child2 = currentNode.children[1]

        val checkResult =
          when {
            child1 is NumberNode && child2 is NumberNode -> child1.value == child2.value
            child1 is TextNode && child2 is TextNode -> child1.toText() == child2.toText()
            child1 is BoolNode && child2 is BoolNode -> child1.toBoolean() == child2.toBoolean()
            else -> return@walker null
          }

        val resultAsBoolNode = if (checkResult) TrueNode else FalseNode
        // kill parent and return equality check result
        return@walker resultAsBoolNode
      }
  }
