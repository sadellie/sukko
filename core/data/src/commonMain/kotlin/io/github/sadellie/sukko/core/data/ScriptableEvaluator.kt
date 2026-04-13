package io.github.sadellie.sukko.core.data

import androidx.compose.ui.graphics.Color
import co.touchlab.kermit.Logger
import dev.zacsweers.metro.Inject
import io.github.sadellie.sukko.core.common.hexToColor
import io.github.sadellie.sukko.core.data.script.ScriptContext
import io.github.sadellie.sukko.core.data.script.evaluateScriptBoolean
import io.github.sadellie.sukko.core.data.script.evaluateScriptDouble
import io.github.sadellie.sukko.core.data.script.evaluateScriptString
import io.github.sadellie.sukko.core.data.script.evaluateScriptToFormattedString
import io.github.sadellie.sukko.core.model.GlobalValueCache
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.basic.GlobalValue
import io.github.sadellie.sukko.core.model.basic.Scriptable
import io.github.sadellie.sukko.core.model.basic.ScriptableBoolean
import io.github.sadellie.sukko.core.model.basic.ScriptableColor
import io.github.sadellie.sukko.core.model.basic.ScriptableDouble
import io.github.sadellie.sukko.core.model.basic.ScriptableString

class ScriptableEvaluator(
  internal val globals: Globals,
  internal val globalValueCache: GlobalValueCache,
  internal val scriptableEvaluatorContext: ScriptableEvaluatorContext,
  internal val globalCurrentValueStore: GlobalCurrentValueStore?,
) {
  @Inject
  class ScriptableEvaluatorFactory(
    private val dynamicColorSchemeProvider: DynamicColorSchemeProvider,
    private val batteryInfoProvider: BatteryInfoProvider,
    private val dateTimeProvider: DateTimeProvider,
    private val mediaInfoProvider: MediaInfoProvider,
    private val deviceInfoProvider: DeviceInfoProvider,
    private val widgetDataStoreManager: WidgetDataStoreManager,
  ) {
    /**
     * @param widgetId Pass `null` to skip creating GlobalCurrentValueStore. If it is not created,
     *   overriding globals in scripts will be skipped. See
     *   [ScriptableEvaluator.evaluateScriptWithFormattedResult]
     */
    fun create(
      globals: Globals,
      widgetId: Int? = null,
      globalValueCache: GlobalValueCache = GlobalValueCache(),
    ): ScriptableEvaluator {
      return ScriptableEvaluator(
        globals = globals,
        globalValueCache = globalValueCache,
        scriptableEvaluatorContext =
          ScriptableEvaluatorContext(
            dynamicColorSchemeProvider = dynamicColorSchemeProvider,
            batteryInfoProvider = batteryInfoProvider,
            dateTimeProvider = dateTimeProvider,
            deviceInfoProvider = deviceInfoProvider,
            mediaInfoProvider = mediaInfoProvider,
          ),
        globalCurrentValueStore =
          widgetId?.let { GlobalCurrentValueStoreImpl(it, widgetDataStoreManager) },
      )
    }
  }

  /**
   * Evaluates a script and format result.
   *
   * @param script Script to evaluate
   * @param readOnly Allow override globals and make other real changes. Should only be called from
   *   Widget. In editor this must be true to avoid unexpected results.
   * @param enableGlobalOverridesAPI Allows using API to set globals, otherwise will throw syntax
   *   error.
   */
  suspend fun evaluateScriptWithFormattedResult(
    script: String,
    readOnly: Boolean,
    enableGlobalOverridesAPI: Boolean,
  ): String? =
    try {
      evaluateScriptToFormattedString(
        input = script,
        context = newScriptContext(globalIds = emptySet(), readOnly = readOnly),
        enableGlobalOverridesAPI = enableGlobalOverridesAPI,
      )
    } catch (e: Exception) {
      Logger.e(e, TAG) { "Failed to evaluate script with formatted result. Script: $script" }
      null
    }

  suspend fun evaluateString(
    scriptable: ScriptableString,
    globalIds: Set<Long> = emptySet(),
  ): String {
    return when (scriptable) {
      is ScriptableString.Fixed -> scriptable.value
      is ScriptableString.Global -> {
        val global = globals.findString(scriptable.id) ?: return scriptable.defaultValue()
        evaluateGlobal(globalIds, global) { newGlobalIds ->
          evaluateString(global.initialValue, newGlobalIds)
        } ?: scriptable.defaultValue()
      }
      is ScriptableString.Script ->
        evaluateScriptSafe(scriptable.script) {
          evaluateScriptString(scriptable.script, newScriptContext(globalIds))
        } ?: scriptable.defaultValue()
    }
  }

  suspend fun evaluateDouble(
    scriptable: ScriptableDouble,
    globalIds: Set<Long> = emptySet(),
  ): Double {
    return when (scriptable) {
      is ScriptableDouble.Fixed -> scriptable.value
      is ScriptableDouble.Global -> {
        val global = globals.findDouble(scriptable.id) ?: return scriptable.defaultValue()
        evaluateGlobal(globalIds, global) { newGlobalIds ->
          evaluateDouble(global.initialValue, newGlobalIds)
        } ?: scriptable.defaultValue()
      }
      is ScriptableDouble.Script ->
        evaluateScriptSafe(scriptable.script) {
          evaluateScriptDouble(scriptable.script, newScriptContext(globalIds))
        } ?: scriptable.defaultValue()
    }
  }

  suspend fun evaluateBoolean(
    scriptable: ScriptableBoolean,
    globalIds: Set<Long> = emptySet(),
  ): Boolean {
    return when (scriptable) {
      is ScriptableBoolean.Fixed -> scriptable.value
      is ScriptableBoolean.Global -> {
        val global = globals.findBoolean(scriptable.id) ?: return scriptable.defaultValue()
        evaluateGlobal(globalIds, global) { newGlobalIds ->
          evaluateBoolean(global.initialValue, newGlobalIds)
        } ?: scriptable.defaultValue()
      }
      is ScriptableBoolean.Script ->
        evaluateScriptSafe(scriptable.script) {
          evaluateScriptBoolean(scriptable.script, newScriptContext(globalIds))
        } ?: scriptable.defaultValue()
    }
  }

  suspend fun evaluateColor(scriptable: ScriptableColor, globalIds: Set<Long> = emptySet()): Color {
    return when (scriptable) {
      is ScriptableColor.FixedM3 ->
        scriptableEvaluatorContext.dynamicColorSchemeProvider.getColorFromSystemColorScheme(scriptable.value)
      is ScriptableColor.FixedCustom -> scriptable.value
      is ScriptableColor.Global -> {
        val global = globals.findColor(scriptable.id) ?: return scriptable.defaultValue()
        evaluateGlobal(globalIds, global) { newGlobalIds ->
          evaluateColor(global.initialValue, newGlobalIds)
        } ?: scriptable.defaultValue()
      }
      is ScriptableColor.Script ->
        evaluateScriptSafe(scriptable.script) {
          evaluateScriptString(scriptable.script, newScriptContext(globalIds)).hexToColor()
        } ?: scriptable.defaultValue()
    }
  }

  private suspend fun <T> evaluateGlobal(
    globalIds: Set<Long>,
    global: GlobalValue<out Scriptable<T>>,
    block: suspend (globalIds: Set<Long>) -> T,
  ): T? {
    if (global.id in globalIds) {
      Logger.e(tag = TAG) { "Cyclic link. ${global.id} is already in link: $globalIds" }
      return null
    }
    val isInRecursion = globalIds.isNotEmpty()
    val result = globalValueCache.getOrPut(global, !isInRecursion) { block(globalIds + global.id) }
    return result
  }

  /**
   * Creates new [ScriptContext] with clean [ScriptContext.variableValueMemory]
   *
   * @param globalIds [GlobalValue.id]s to detect cyclic link.
   * @param readOnly When `true` will not execute methods that can create a persistent result. For
   *   example, updating globals or passing data to other apps.
   * @param globalCurrentValueStore Object to save global values. Will skip saving globals if `null`
   */
  private fun newScriptContext(globalIds: Set<Long>, readOnly: Boolean = true): ScriptContext {
    return ScriptContext(
      batteryInfoProvider = scriptableEvaluatorContext.batteryInfoProvider,
      dateTimeProvider = scriptableEvaluatorContext.dateTimeProvider,
      dynamicColorSchemeProvider = scriptableEvaluatorContext.dynamicColorSchemeProvider,
      mediaInfoProvider = scriptableEvaluatorContext.mediaInfoProvider,
      getGlobalStringValue = { id -> evaluateString(ScriptableString.Global(id), globalIds) },
      getGlobalDoubleValue = { id -> evaluateDouble(ScriptableDouble.Global(id), globalIds) },
      getGlobalBooleanValue = { id -> evaluateBoolean(ScriptableBoolean.Global(id), globalIds) },
      setGlobalStringValue = { id, value ->
        if (readOnly) error("Not allowed to set globals")
        globalCurrentValueStore?.saveStringValue(id, value)
      },
      setGlobalDoubleValue = { id, value ->
        if (readOnly) error("Not allowed to set globals")
        globalCurrentValueStore?.saveDoubleValue(id, value)
      },
      setGlobalBooleanValue = { id, value ->
        if (readOnly) error("Not allowed to set globals")
        globalCurrentValueStore?.saveBooleanValue(id, value)
      },
      deviceInfoProvider = scriptableEvaluatorContext.deviceInfoProvider,
    )
  }

  private suspend fun <T> evaluateScriptSafe(script: String, block: suspend () -> T): T? =
    try {
      block()
    } catch (e: Exception) {
      Logger.e(throwable = e, tag = TAG) { "Failed to evaluate: $script" }
      null
    }
}

private const val TAG = "ScriptableEvaluator"
