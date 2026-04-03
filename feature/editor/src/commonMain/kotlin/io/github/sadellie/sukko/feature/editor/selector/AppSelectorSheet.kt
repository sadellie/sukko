package io.github.sadellie.sukko.feature.editor.selector

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.composables.core.ModalBottomSheetState
import io.github.sadellie.sukko.core.common.stateIn
import io.github.sadellie.sukko.core.data.InstalledAppsProvider
import io.github.sadellie.sukko.core.model.InstalledApp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Composable
expect fun AppSelectorSheet(
  state: ModalBottomSheetState,
  onValueSelected: (label: String, packageName: String) -> Unit,
  packageName: String?,
)

internal class AppSelectorViewModel(
  packageName: String?,
  private val installedAppsProvider: InstalledAppsProvider,
) : ViewModel() {
  private val _allApps = MutableStateFlow<List<InstalledApp>?>(null)
  private val _selectedPackageName = MutableStateFlow(packageName)
  internal val uiState: StateFlow<AppSelectorUIState> =
    combine(_selectedPackageName, _allApps) { selectedPackageName, allApps ->
        AppSelectorUIState(selectedPackageName = selectedPackageName, allApps = allApps)
      }
      .stateIn(viewModelScope, AppSelectorUIState(null, null))

  fun updateSelectedApp(packageName: String) = _selectedPackageName.update { packageName }

  init {
    viewModelScope.launch {
      val apps = installedAppsProvider.getAllApps()
      _allApps.update { apps }
    }
  }
}

internal data class AppSelectorUIState(
  val selectedPackageName: String?,
  val allApps: List<InstalledApp>?,
)
