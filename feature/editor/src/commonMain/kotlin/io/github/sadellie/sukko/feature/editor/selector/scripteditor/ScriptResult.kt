package io.github.sadellie.sukko.feature.editor.selector.scripteditor

import androidx.compose.runtime.Stable
import io.github.sadellie.sukko.core.data.script.ScriptException

internal sealed interface ScriptResult {
  @Stable data object Loading : ScriptResult

  @Stable data class Success(val scriptResult: String?) : ScriptResult

  @Stable data class ScriptError(val throwable: ScriptException) : ScriptResult

  @Stable data class GenericError(val throwable: Exception) : ScriptResult
}
