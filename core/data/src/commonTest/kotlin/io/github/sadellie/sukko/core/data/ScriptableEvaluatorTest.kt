package io.github.sadellie.sukko.core.data

import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.basic.GlobalValue
import io.github.sadellie.sukko.core.model.basic.ScriptableDouble
import io.github.sadellie.sukko.core.model.basic.ScriptableString
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ScriptableEvaluatorTest {
  @Test
  fun evaluateString_scriptWithGlobalString() = runTest {
    val scriptableEvaluator =
      fakeScriptableEvaluator(
        globals =
          Globals(
            strings =
              listOf(
                GlobalValue.GlobalString(7, "global 7", ScriptableString.Global(8)),
                GlobalValue.GlobalString(8, "global 8", ScriptableString.Fixed("this is a test")),
              )
          )
      )
    val scriptable = ScriptableString.Script("globalString(7)")
    val result = scriptableEvaluator.evaluateString(scriptable)
    assertEquals("this is a test", result)
  }

  @Test
  fun evaluateDouble_scriptWithGlobalString() = runTest {
    val scriptableEvaluator =
      fakeScriptableEvaluator(
        globals =
          Globals(
            doubles =
              listOf(
                GlobalValue.GlobalDouble(7, "global 7", ScriptableDouble.Global(8)),
                GlobalValue.GlobalDouble(8, "global 8", ScriptableDouble.Fixed(777.0)),
              )
          )
      )
    val scriptable = ScriptableDouble.Script("globalNumber(7)")
    val result = scriptableEvaluator.evaluateDouble(scriptable)
    assertEquals(777.0, result)
  }

  @Test
  fun evaluateDouble_scriptWithSetGlobalNumber() = runTest {
    val scriptableEvaluator =
      fakeScriptableEvaluator(
        globals =
          Globals(
            doubles = listOf(GlobalValue.GlobalDouble(7, "global 7", ScriptableDouble.Fixed(2.0)))
          )
      )
    val script = "setGlobalNumber(7, 99)"
    scriptableEvaluator.evaluateScriptWithFormattedResult(
      script = script,
      readOnly = false,
      enableGlobalOverridesAPI = true,
    )
    val result = scriptableEvaluator.globalCurrentValueStore?.getCurrentDoubleValue(7)
    assertEquals(99.0, result)
  }

  @Test
  fun evaluateDouble_scriptWithGetAndSetGlobalNumber() = runTest {
    val scriptableEvaluator =
      fakeScriptableEvaluator(
        globals =
          Globals(
            doubles = listOf(GlobalValue.GlobalDouble(7, "global 7", ScriptableDouble.Fixed(2.0)))
          )
      )
    val script = "setGlobalNumber(7, globalNumber(7) + 2)"
    scriptableEvaluator.evaluateScriptWithFormattedResult(
      script = script,
      readOnly = false,
      enableGlobalOverridesAPI = true,
    )

    val result = scriptableEvaluator.globalCurrentValueStore?.getCurrentDoubleValue(7)
    assertEquals(4.0, result)
  }
}
