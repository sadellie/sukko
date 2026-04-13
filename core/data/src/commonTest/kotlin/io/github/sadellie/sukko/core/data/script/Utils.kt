package io.github.sadellie.sukko.core.data.script

import io.github.sadellie.sukko.core.data.fakeBatteryInfoProvider
import io.github.sadellie.sukko.core.data.fakeDateTimeProvider
import io.github.sadellie.sukko.core.data.fakeDeviceInfoProvider
import io.github.sadellie.sukko.core.data.fakeDynamicColorSchemeProvider
import io.github.sadellie.sukko.core.data.fakeMediaInfoProvider
import io.github.sadellie.sukko.core.data.noImpl
import io.github.sadellie.sukko.core.data.script.simplify.SimplificationRule
import io.github.sadellie.sukko.core.data.script.simplify.SimplificationStep
import io.github.sadellie.sukko.core.data.script.simplify.SimplificationType
import io.github.sadellie.sukko.core.data.script.token.tokenize
import kotlinx.coroutines.test.runTest
import kotlin.test.assertEquals

internal fun assertSimplifySingleRule(
  rule: SimplificationRule,
  simplificationType: SimplificationType,
  context: ScriptContext = fakeScriptContext(),
  input: String,
  expected: ASTNode?,
) = runTest {
  val inputTree = buildTreeAndCollapse(input)
  val actual = rule.simplify(context, inputTree)

  val expectedStep =
    expected?.let { SimplificationStep(simplifiedASTNode = expected, type = simplificationType) }
  assertEquals(expectedStep, actual)
}

internal fun buildTreeAndCollapse(input: String): ASTNode {
  val tokens = tokenize(input)
  return ASTNode.buildTreesAndCollapse(
      tokens = tokens,
      scriptContext = fakeScriptContext(),
      enableGlobalOverridesAPI = false,
    )
    .first()
}

internal fun fakeScriptContext() =
  ScriptContext(
    batteryInfoProvider = fakeBatteryInfoProvider(),
    dateTimeProvider = fakeDateTimeProvider(),
    dynamicColorSchemeProvider = fakeDynamicColorSchemeProvider(),
    mediaInfoProvider = fakeMediaInfoProvider(),
    getGlobalDoubleValue = { noImpl },
    getGlobalStringValue = { noImpl },
    getGlobalBooleanValue = { noImpl },
    setGlobalStringValue = { _, _ -> noImpl },
    setGlobalDoubleValue = { _, _ -> noImpl },
    setGlobalBooleanValue = { _, _ -> noImpl },
    deviceInfoProvider = fakeDeviceInfoProvider(),
  )
