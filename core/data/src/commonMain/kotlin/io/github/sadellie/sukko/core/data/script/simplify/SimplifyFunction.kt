package io.github.sadellie.sukko.core.data.script.simplify

import io.github.sadellie.sukko.core.data.script.ASTNode
import io.github.sadellie.sukko.core.data.script.BoolNode
import io.github.sadellie.sukko.core.data.script.FalseNode
import io.github.sadellie.sukko.core.data.script.FunctionNode
import io.github.sadellie.sukko.core.data.script.NumberNode
import io.github.sadellie.sukko.core.data.script.ScriptContext
import io.github.sadellie.sukko.core.data.script.ScriptException
import io.github.sadellie.sukko.core.data.script.TextNode
import io.github.sadellie.sukko.core.data.script.TrueNode
import io.github.sadellie.sukko.core.data.script.token.Token3

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

        if (currentNode.children.size != currentNode.token.parametersCount)
          throw ScriptException.WrongParameterCount(currentNode, currentNode.token.parametersCount)

        when (currentNode.token) {
          Token3.Function.CurrentDate -> {
            val format = currentNode.getParameter<TextNode>(0)?.value ?: return@walker null
            val result = context.dateTimeProvider.currentDate(format)
            return@walker TextNode(result)
          }

          Token3.Function.CurrentDateWithTimeZone -> {
            val format = currentNode.getParameter<TextNode>(0)?.value ?: return@walker null
            val timeZoneId = currentNode.getParameter<TextNode>(1)?.value ?: return@walker null
            val result = context.dateTimeProvider.currentDateWithTimeZone(format, timeZoneId)
            return@walker TextNode(result)
          }

          Token3.Function.DynamicColor -> {
            val m3ColorName = currentNode.getParameter<TextNode>(0)?.value ?: return@walker null
            val result =
              context.dynamicColorSchemeProvider.extractHexFromSystemColorScheme(
                m3ColorName.uppercase()
              )
            return@walker TextNode(result)
          }

          Token3.Function.ColorScheme -> {
            val colorName = currentNode.getParameter<TextNode>(0)?.value ?: return@walker null
            val source = currentNode.getParameter<TextNode>(1)?.value ?: return@walker null
            val result =
              context.dynamicColorSchemeProvider.extractHexFromImageColorScheme(
                m3ColorName = colorName,
                imageUri = source,
              )
            return@walker TextNode(result)
          }

          Token3.Function.If -> {
            val condition = currentNode.getParameter<BoolNode>(0) ?: return@walker null
            val result = if (condition.value) currentNode.children[1] else currentNode.children[2]
            return@walker result
          }

          Token3.Function.FormatTimestamp -> {
            val timeStamp =
              currentNode.getParameter<NumberNode>(0)?.value?.toLong() ?: return@walker null
            val format = currentNode.getParameter<TextNode>(1)?.value ?: return@walker null
            val result = context.dateTimeProvider.formatTimestamp(timeStamp, format)
            return@walker TextNode(result)
          }

          Token3.Function.GetGlobalString -> {
            val globalId = currentNode.getParameter<NumberNode>(0) ?: return@walker null
            val globalStringValue = context.getGlobalStringValue(globalId.value.toLong())
            return@walker TextNode(globalStringValue)
          }

          Token3.Function.GetGlobalNumber -> {
            val globalId = currentNode.getParameter<NumberNode>(0) ?: return@walker null
            val globalDoubleValue = context.getGlobalDoubleValue(globalId.value.toLong())
            return@walker NumberNode(globalDoubleValue)
          }

          Token3.Function.GetGlobalBoolean -> {
            val globalId = currentNode.getParameter<NumberNode>(0) ?: return@walker null
            val globalDoubleValue = context.getGlobalBooleanValue(globalId.value.toLong())
            return@walker if (globalDoubleValue) TrueNode else FalseNode
          }

          Token3.Function.SetGlobalNumber -> {
            val globalId = currentNode.getParameter<NumberNode>(0) ?: return@walker null
            val newGlobalValue = currentNode.getParameter<NumberNode>(1) ?: return@walker null
            context.setGlobalDoubleValue(globalId.value.toLong(), newGlobalValue.value)
            return@walker newGlobalValue
          }

          Token3.Function.SetGlobalString -> {
            val globalId = currentNode.getParameter<NumberNode>(0) ?: return@walker null
            val newGlobalValue = currentNode.getParameter<TextNode>(1) ?: return@walker null
            context.setGlobalStringValue(globalId.value.toLong(), newGlobalValue.value)
            return@walker newGlobalValue
          }

          Token3.Function.SetGlobalBoolean -> {
            val globalId = currentNode.getParameter<NumberNode>(0) ?: return@walker null
            val newGlobalValue = currentNode.getParameter<BoolNode>(1) ?: return@walker null
            context.setGlobalBooleanValue(globalId.value.toLong(), newGlobalValue.value)
            return@walker newGlobalValue
          }
        }
      }
  }
