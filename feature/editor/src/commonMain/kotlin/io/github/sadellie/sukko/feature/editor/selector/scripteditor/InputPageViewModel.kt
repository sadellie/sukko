package io.github.sadellie.sukko.feature.editor.selector.scripteditor

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import io.github.sadellie.sukko.core.common.observe
import io.github.sadellie.sukko.core.common.stateIn
import io.github.sadellie.sukko.core.data.ScriptableEvaluator
import io.github.sadellie.sukko.core.model.Globals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@AssistedInject
class InputPageViewModel(
  @Assisted initialInput: String,
  @Assisted globals: Globals,
  @Assisted private val enableGlobalOverrides: Boolean,
  scriptableEvaluatorFactory: ScriptableEvaluator.ScriptableEvaluatorFactory,
) : ViewModel() {
  private val _input = TextFieldState(initialInput)
  private val _result = MutableStateFlow<ScriptResult>(ScriptResult.Loading)
  private val _scriptableEvaluator = scriptableEvaluatorFactory.create(globals = globals)
  private var _scriptEvaluationJob: Job? = null

  internal val uiState =
    _result
      .mapLatest { result -> InputPageUIState(input = _input, result = result) }
      .stateIn(viewModelScope, null)

  internal suspend fun observeInput() {
    _input.observe().collectLatest { input -> evaluateScript(script = input.toString()) }
  }

  internal fun insertInInput(text: String) = _input.insert(text)

  private fun evaluateScript(script: String) {
    _scriptEvaluationJob?.cancel()
    _scriptEvaluationJob =
      viewModelScope.launch(Dispatchers.Default) {
        val result =
          _scriptableEvaluator.evaluateScriptWithFormattedResult(
            script = script,
            readOnly = true,
            enableGlobalOverridesAPI = enableGlobalOverrides,
          )
        _result.update { ScriptResult.Success(result) }
      }
  }

  @AssistedFactory
  @ManualViewModelAssistedFactoryKey
  @ContributesIntoMap(AppScope::class)
  fun interface Factory : ManualViewModelAssistedFactory {
    fun create(
      initialInput: String,
      enableGlobalOverrides: Boolean,
      globals: Globals,
    ): InputPageViewModel
  }
}
