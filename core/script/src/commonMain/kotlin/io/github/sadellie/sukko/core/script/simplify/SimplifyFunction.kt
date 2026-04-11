package io.github.sadellie.sukko.core.script.simplify

import io.github.sadellie.sukko.core.script.ASTNode
import io.github.sadellie.sukko.core.script.BoolNode
import io.github.sadellie.sukko.core.script.FunctionNode
import io.github.sadellie.sukko.core.script.NumberNode
import io.github.sadellie.sukko.core.script.ScriptContext
import io.github.sadellie.sukko.core.script.ScriptException
import io.github.sadellie.sukko.core.script.TextNode
import io.github.sadellie.sukko.core.script.token.Token3

/**
 * Functions do not have precedence and their order of evaluation doesn't matter so they can be
 * simplified in one rule
 */
internal val SimplifyFunction =
  object : SimplificationRule {
    override suspend fun simplify(context: ScriptContext, tree: ASTNode): SimplificationStep? =
      simplifyBottomToTop(SimplificationType.EVAL_FUNCTION, context, tree) walker@{ currentNode ->
        // only work with function
        if (currentNode !is FunctionNode) return@walker null

        if (currentNode.children.size != currentNode.token.parameters)
          throw ScriptException.WrongParameterCount(currentNode, currentNode.token.parameters)

        when (currentNode.token) {
          Token3.Function.CurrentDate -> {
            val format = currentNode.getParameter<TextNode>(0)?.token?.symbol ?: return@walker null
            val result = context.currentDate(format)
            return@walker TextNode(result)
          }

          Token3.Function.CurrentDateWithTimeZone -> {
            val format = currentNode.getParameter<TextNode>(0)?.token?.symbol ?: return@walker null
            val timeZoneId =
              currentNode.getParameter<TextNode>(1)?.token?.symbol ?: return@walker null
            val result = context.currentDateWithTimeZone(format, timeZoneId)
            return@walker TextNode(result)
          }

          Token3.Function.DynamicColor -> {
            val m3ColorName =
              currentNode.getParameter<TextNode>(0)?.token?.symbol ?: return@walker null
            val result = context.dynamicColor(m3ColorName.uppercase())
            return@walker TextNode(result)
          }

          Token3.Function.ColorScheme -> {
            val colorName =
              currentNode.getParameter<TextNode>(0)?.token?.symbol ?: return@walker null
            val source = currentNode.getParameter<TextNode>(1)?.token?.symbol ?: return@walker null
            val result = context.colorScheme(colorName, source)
            return@walker TextNode(result)
          }

          Token3.Function.If -> {
            val condition = currentNode.getParameter<BoolNode>(0) ?: return@walker null
            val result =
              if (condition.toBoolean()) currentNode.children[1] else currentNode.children[2]
            return@walker result
          }

          Token3.Function.FormatTimestamp -> {
            val timeStamp =
              currentNode.getParameter<NumberNode>(0)?.value?.toLong() ?: return@walker null
            val format = currentNode.getParameter<TextNode>(1)?.token?.symbol ?: return@walker null
            val result = context.formatTimestamp(timeStamp, format)
            return@walker TextNode(result)
          }
        }
      }
  }
