package io.github.sadellie.sukko.core.model.basic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isUnspecified
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.touchlab.kermit.Logger
import io.github.sadellie.sukko.core.common.ColorSerializer
import io.github.sadellie.sukko.core.common.DpSerializer
import io.github.sadellie.sukko.core.common.SpSerializer
import io.github.sadellie.sukko.core.common.hexToColor
import io.github.sadellie.sukko.core.common.toHex
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.LayerContext
import io.github.sadellie.sukko.core.script.evaluateScriptBoolean
import io.github.sadellie.sukko.core.script.evaluateScriptDouble
import io.github.sadellie.sukko.core.script.evaluateScriptString
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.common_not_selected
import io.github.sadellie.sukko.resources.core_model_scriptable_boolean_false
import io.github.sadellie.sukko.resources.core_model_scriptable_boolean_true
import io.github.sadellie.sukko.resources.core_model_scriptable_dp
import io.github.sadellie.sukko.resources.core_model_scriptable_sp
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

sealed interface Scriptable<V> {
  /**
   * Get a real value
   *
   * @param layerContext Current context
   * @param globals [Globals] used by [Global] scriptables
   * @param globalIds This method is recursive. Set of already visited ids helps to detect cyclic
   *   links.
   * @return A real value with type of [V] or [defaultValue] if exception occurs.
   */
  suspend fun getValue(
    layerContext: LayerContext,
    globals: Globals,
    globalIds: Set<Long> = emptySet(),
  ): V

  fun defaultValue(): V

  sealed interface Fixed<T, V> : Scriptable<V> {
    val value: T
  }

  sealed interface Script<T> : Scriptable<T> {
    val script: String

    override suspend fun getValue(
      layerContext: LayerContext,
      globals: Globals,
      globalIds: Set<Long>,
    ): T {
      return try {
        evaluateUnsafe(layerContext).value
      } catch (e: Exception) {
        Logger.e(TAG, e) { "Failed to evaluate: $script" }
        defaultValue()
      }
    }

    /** Evaluate [script]. Caller is responsible for error handling. */
    suspend fun evaluateUnsafe(layerContext: LayerContext): Fixed<T, T>
  }

  /** @property id id of global in [Globals] */
  sealed interface Global<V> : Scriptable<V> {
    val id: Long

    suspend fun findGlobal(globals: Globals?): GlobalValue<out Scriptable<V>>?

    override suspend fun getValue(
      layerContext: LayerContext,
      globals: Globals,
      globalIds: Set<Long>,
    ): V {
      if (id in globalIds) {
        Logger.e(TAG) { "getValue: Cyclic link for $this. Globals in link: $globalIds" }
        return defaultValue()
      }
      val global = findGlobal(globals)
      if (global == null) {
        Logger.e(TAG) { "getValue: Failed to find global with id $id in $globals" }
        return defaultValue()
      }

      // avoid self locking (global -> global)
      val isInRecursion = globalIds.isNotEmpty()
      val value =
        layerContext.globalValueCache.getOrPut(global, lock = !isInRecursion) {
          Logger.d(TAG) { "getValue: update cache ${global.id} ${global.label}" }
          global.value.getValue(layerContext, globals, globalIds + id)
        }

      return value ?: defaultValue()
    }
  }
}

@Serializable
sealed interface ScriptableBoolean : Scriptable<Boolean> {
  override fun defaultValue() = false

  @Serializable
  data class Fixed(override val value: Boolean) :
    ScriptableBoolean, Scriptable.Fixed<Boolean, Boolean> {
    override suspend fun getValue(
      layerContext: LayerContext,
      globals: Globals,
      globalIds: Set<Long>,
    ) = value
  }

  @Serializable
  data class Script(override val script: String) : ScriptableBoolean, Scriptable.Script<Boolean> {
    override suspend fun evaluateUnsafe(layerContext: LayerContext) =
      Fixed(evaluateScriptBoolean(script, layerContext.scriptContext()))
  }

  @Serializable
  data class Global(override val id: Long) : ScriptableBoolean, Scriptable.Global<Boolean> {
    override suspend fun findGlobal(globals: Globals?) = globals?.findBoolean(id)
  }
}

@Serializable
sealed interface ScriptableString : Scriptable<String> {
  override fun defaultValue() = ""

  @Serializable
  data class Fixed(override val value: String) :
    ScriptableString, Scriptable.Fixed<String, String> {
    override suspend fun getValue(
      layerContext: LayerContext,
      globals: Globals,
      globalIds: Set<Long>,
    ) = value
  }

  @Serializable
  data class Script(override val script: String) : ScriptableString, Scriptable.Script<String> {
    override suspend fun evaluateUnsafe(layerContext: LayerContext) =
      Fixed(evaluateScriptString(script, layerContext.scriptContext()))
  }

  @Serializable
  data class Global(override val id: Long) : ScriptableString, Scriptable.Global<String> {
    override suspend fun findGlobal(globals: Globals?) = globals?.findString(id)
  }
}

@Serializable
sealed interface ScriptableDouble : Scriptable<Double> {
  override fun defaultValue() = 0.0

  @Serializable
  data class Fixed(override val value: Double) :
    ScriptableDouble, Scriptable.Fixed<Double, Double> {
    override suspend fun getValue(
      layerContext: LayerContext,
      globals: Globals,
      globalIds: Set<Long>,
    ) = value
  }

  @Serializable
  data class Script(override val script: String) : ScriptableDouble, Scriptable.Script<Double> {
    override suspend fun evaluateUnsafe(layerContext: LayerContext) =
      Fixed(evaluateScriptDouble(script, layerContext.scriptContext()))
  }

  @Serializable
  data class Global(override val id: Long) : ScriptableDouble, Scriptable.Global<Double> {
    override suspend fun findGlobal(globals: Globals?) = globals?.findDouble(id)
  }
}

@Serializable
sealed interface ScriptableDp : Scriptable<Dp> {
  override fun defaultValue() = 0.dp

  @Serializable
  data class Fixed(@Serializable(DpSerializer::class) override val value: Dp) :
    ScriptableDp, Scriptable.Fixed<Dp, Dp> {
    override suspend fun getValue(
      layerContext: LayerContext,
      globals: Globals,
      globalIds: Set<Long>,
    ) = value
  }

  @Serializable
  data class Script(override val script: String) : ScriptableDp, Scriptable.Script<Dp> {
    override suspend fun evaluateUnsafe(layerContext: LayerContext) =
      Fixed(evaluateScriptDouble(script, layerContext.scriptContext()).dp)
  }

  @Serializable
  data class Global(override val id: Long) : ScriptableDp, Scriptable.Global<Dp> {
    override suspend fun findGlobal(globals: Globals?) = globals?.findDp(id)
  }
}

@Serializable
sealed interface ScriptableSp : Scriptable<TextUnit> {
  override fun defaultValue() = 0.sp

  @Serializable
  data class Fixed(@Serializable(SpSerializer::class) override val value: TextUnit) :
    ScriptableSp, Scriptable.Fixed<TextUnit, TextUnit> {
    override suspend fun getValue(
      layerContext: LayerContext,
      globals: Globals,
      globalIds: Set<Long>,
    ) = value
  }

  @Serializable
  data class Script(override val script: String) : ScriptableSp, Scriptable.Script<TextUnit> {
    override suspend fun evaluateUnsafe(layerContext: LayerContext) =
      Fixed(evaluateScriptDouble(script, layerContext.scriptContext()).sp)
  }

  @Serializable
  data class Global(override val id: Long) : ScriptableSp, Scriptable.Global<TextUnit> {
    override suspend fun findGlobal(globals: Globals?) = globals?.findSp(id)
  }
}

@Serializable
sealed interface ScriptableColor : Scriptable<Color> {
  override fun defaultValue() = Color.Unspecified

  @Serializable
  data class FixedCustom(@Serializable(ColorSerializer::class) override val value: Color) :
    ScriptableColor, Scriptable.Fixed<Color, Color> {
    override suspend fun getValue(
      layerContext: LayerContext,
      globals: Globals,
      globalIds: Set<Long>,
    ) = value
  }

  @Serializable
  data class FixedM3(override val value: M3Color) :
    ScriptableColor, Scriptable.Fixed<M3Color, Color> {
    override suspend fun getValue(
      layerContext: LayerContext,
      globals: Globals,
      globalIds: Set<Long>,
    ) = layerContext.dynamicColorSchemeProvider.getColorFromSystemColorScheme(value)
  }

  @Serializable
  data class Script(override val script: String) : ScriptableColor, Scriptable.Script<Color> {
    override suspend fun evaluateUnsafe(layerContext: LayerContext) =
      FixedCustom(evaluateScriptString(script, layerContext.scriptContext()).hexToColor())
  }

  @Serializable
  data class Global(override val id: Long) : ScriptableColor, Scriptable.Global<Color> {
    override suspend fun findGlobal(globals: Globals?) = globals?.findColor(id)
  }
}

class ScriptableDisplay(private val globals: Globals) {
  @Composable
  fun displayString(scriptable: Scriptable<*>) =
    when (scriptable) {
      is ScriptableBoolean.Fixed ->
        stringResource(
          if (scriptable.value) Res.string.core_model_scriptable_boolean_true
          else Res.string.core_model_scriptable_boolean_false
        )
      is ScriptableString.Fixed -> scriptable.value
      is ScriptableDouble.Fixed -> scriptable.value.toString()
      is ScriptableDp.Fixed ->
        "${scriptable.value.value} ${stringResource(Res.string.core_model_scriptable_dp)}"
      is ScriptableSp.Fixed ->
        "${scriptable.value.value} ${stringResource(Res.string.core_model_scriptable_sp)}"
      is ScriptableColor.FixedCustom ->
        if (scriptable.value.isUnspecified) stringResource(Res.string.common_not_selected)
        else "#${scriptable.value.toHex()}"
      is ScriptableColor.FixedM3 -> stringResource(scriptable.value.displayName)
      is Scriptable.Script<*> -> scriptable.script
      is Scriptable.Global<*> ->
        produceState("") { value = scriptable.findGlobal(globals)?.label ?: return@produceState }
          .value
    }
}

val LocalScriptableDisplay = compositionLocalOf { ScriptableDisplay(Globals()) }

private const val TAG = "Scriptable"
