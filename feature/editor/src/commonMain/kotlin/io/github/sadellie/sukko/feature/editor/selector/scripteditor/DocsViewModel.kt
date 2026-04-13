package io.github.sadellie.sukko.feature.editor.selector.scripteditor

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import io.github.sadellie.sukko.core.common.observe
import io.github.sadellie.sukko.core.data.script.docs.Docs
import io.github.sadellie.sukko.core.data.script.docs.DocsRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Inject
@ViewModelKey
@ContributesIntoMap(AppScope::class)
class DocsViewModel(private val repository: DocsRepository) : ViewModel() {
  val searchTextFieldState = TextFieldState()
  private val _isLoading = MutableStateFlow(true)
  private val _results = MutableStateFlow<Docs?>(null)
  val results: StateFlow<Docs?> = _results.asStateFlow()

  init {
    viewModelScope.launch {
      repository.load()
      _isLoading.update { false }
    }
  }

  @OptIn(FlowPreview::class)
  suspend fun observeSearchQuery(lang: String) {
    val queryFlow = searchTextFieldState.observe()
    combine(queryFlow, _isLoading) { query, isLoading ->
        _results.update { if (isLoading) null else repository.search(query.toString(), lang) }
      }
      .collectLatest {}
  }
}
