package io.github.sadellie.sukko.feature.editor.selector.scripteditor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import co.touchlab.kermit.Logger
import io.github.sadellie.sukko.core.model.basic.Scriptable
import io.github.sadellie.sukko.core.script.ScriptException
import io.github.sadellie.sukko.feature.editor.selector.rememberLayerContext
import kotlinx.coroutines.delay

internal sealed interface ScriptEditorState<T> {
  val isAutoReloadEnabled: Boolean

  @Stable data class Loading<T>(override val isAutoReloadEnabled: Boolean) : ScriptEditorState<T>

  @Stable
  data class Success<T>(val scriptResult: T, override val isAutoReloadEnabled: Boolean) :
    ScriptEditorState<T>

  @Stable
  data class ScriptError<T>(
    val throwable: ScriptException,
    override val isAutoReloadEnabled: Boolean,
  ) : ScriptEditorState<T>

  @Stable
  data class GenericError<T>(val throwable: Exception, override val isAutoReloadEnabled: Boolean) :
    ScriptEditorState<T>
}

@Composable
internal fun <T> produceScriptEditorState(
  key1: Any?,
  autoReload: Boolean,
  debounce: Long = 700L,
  block: suspend () -> Scriptable.Script<T>,
): State<ScriptEditorState<T>> {
  val layerContext = rememberLayerContext()
  return produceState<ScriptEditorState<T>>(
    initialValue = ScriptEditorState.Loading(autoReload),
    key1 = key1,
    key2 = autoReload,
  ) {
    if (!autoReload) return@produceState
    // do not debounce on initial value
    if (this.value !is ScriptEditorState.Loading<T>) delay(debounce)
    value =
      try {
        ScriptEditorState.Success(block().evaluateUnsafe(layerContext).value, autoReload)
      } catch (e: ScriptException) {
        Logger.e(throwable = e, tag = "ScriptEditor") { "Script exception" }
        ScriptEditorState.ScriptError(e, autoReload)
      } catch (e: Exception) {
        Logger.e(throwable = e, tag = "ScriptEditor") { "Generic exception" }
        ScriptEditorState.GenericError(e, autoReload)
      }
  }
}
