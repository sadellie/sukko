package io.github.sadellie.sukko.core.model.basic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.isUnspecified
import io.github.sadellie.sukko.core.common.toHex
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.common_not_selected
import io.github.sadellie.sukko.resources.core_model_scriptable_boolean_false
import io.github.sadellie.sukko.resources.core_model_scriptable_boolean_true
import org.jetbrains.compose.resources.stringResource

val LocalScriptableDisplay = compositionLocalOf { ScriptableDisplay(Globals()) }

class ScriptableDisplay(private val globals: Globals) {
  @Composable
  fun displayString(scriptable: Scriptable<*>?) =
    when (scriptable) {
      is ScriptableBoolean.Fixed ->
        stringResource(
          if (scriptable.value) Res.string.core_model_scriptable_boolean_true
          else Res.string.core_model_scriptable_boolean_false
        )
      is ScriptableString.Fixed -> scriptable.value
      is ScriptableDouble.Fixed -> scriptable.value.toString()
      is ScriptableColor.FixedCustom ->
        if (scriptable.value.isUnspecified) stringResource(Res.string.common_not_selected)
        else "#${scriptable.value.toHex()}"
      is ScriptableColor.FixedM3 -> stringResource(scriptable.value.displayName)
      is Scriptable.Script<*> -> scriptable.script
      is ScriptableBoolean.Global ->
        produceState("") {
            value = globals.findBoolean(scriptable.id)?.label ?: return@produceState
          }
          .value
      is ScriptableColor.Global ->
        produceState("") { value = globals.findColor(scriptable.id)?.label ?: return@produceState }
          .value
      is ScriptableDouble.Global ->
        produceState("") { value = globals.findDouble(scriptable.id)?.label ?: return@produceState }
          .value
      is ScriptableString.Global ->
        produceState("") { value = globals.findString(scriptable.id)?.label ?: return@produceState }
          .value
      null -> stringResource(Res.string.common_not_selected)
    }
}
