package io.github.sadellie.sukko.feature.editor.selector

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.composables.core.ModalBottomSheetState
import google.material.design.symbols.SearchOff
import google.material.design.symbols.Symbols
import io.github.sadellie.sukko.core.common.collectAsStateWithLifecycleKMP
import io.github.sadellie.sukko.core.common.stateIn
import io.github.sadellie.sukko.core.data.InstalledAppsProvider
import io.github.sadellie.sukko.core.designsystem.Preview2
import io.github.sadellie.sukko.core.designsystem.theme.ListArrangement
import io.github.sadellie.sukko.core.designsystem.theme.Sizes
import io.github.sadellie.sukko.core.model.InstalledApp
import io.github.sadellie.sukko.core.ui.ListItem2
import io.github.sadellie.sukko.core.ui.LoadingBox
import io.github.sadellie.sukko.core.ui.ModalBottomSheet2
import io.github.sadellie.sukko.core.ui.ScenePlaceholder
import io.github.sadellie.sukko.core.ui.SheetContentWithButtons
import io.github.sadellie.sukko.core.ui.hide
import io.github.sadellie.sukko.core.ui.listedShape
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.editor_selector_app_empty_placeholder
import io.github.sadellie.sukko.resources.editor_selector_app_empty_placeholder_text
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun AppSelectorSheet(
  state: ModalBottomSheetState,
  onValueSelected: (label: String, packageName: String) -> Unit,
  packageName: String?,
) {
  ModalBottomSheet2(state = state) {
    // do not use ModalBottomSheetWithButtons to have VM in modal sheet
    val viewModel = koinViewModel<AppSelectorViewModel> { parametersOf(packageName) }
    val uiState = viewModel.uiState.collectAsStateWithLifecycleKMP().value
    SheetContentWithButtons(
      onDismiss = state::hide,
      onConfirm = {
        if (uiState.selectedPackageName != null) {
          val selectedApp =
            uiState.allApps?.firstOrNull { it.packageId == uiState.selectedPackageName }
          if (selectedApp != null) onValueSelected(selectedApp.label, selectedApp.packageId)
        }
      },
      isConfirmButtonEnabled = uiState.selectedPackageName != null,
      sheetContent = {
        AppSelectorSheetContent(onUpdateSelected = viewModel::updateSelectedApp, uiState = uiState)
      },
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppSelectorSheetContent(
  onUpdateSelected: (packageName: String) -> Unit,
  uiState: AppSelectorUIState,
) {
  Crossfade(uiState.allApps) { allApps ->
    when {
      allApps == null -> LoadingBox(modifier = Modifier.fillMaxWidth().padding(Sizes.large))
      allApps.isEmpty() ->
        ScenePlaceholder(
          modifier = Modifier.fillMaxWidth().padding(Sizes.large),
          icon = Symbols.SearchOff,
          title = stringResource(Res.string.editor_selector_app_empty_placeholder),
          text = stringResource(Res.string.editor_selector_app_empty_placeholder_text),
        )
      else ->
        LazyColumn(
          modifier = Modifier.fillMaxWidth().padding(horizontal = Sizes.large),
          verticalArrangement = ListArrangement,
        ) {
          itemsIndexed(allApps, { index, _ -> index }) { index, app ->
            AppListItem(
              modifier = Modifier.clickable { onUpdateSelected(app.packageId) },
              app = app,
              isSelected = app.packageId == uiState.selectedPackageName,
              shape = ListItemDefaults.listedShape(index, allApps.size),
            )
          }
        }
    }
  }
}

@Composable
private fun AppListItem(modifier: Modifier, app: InstalledApp, isSelected: Boolean, shape: Shape) {
  val selectedTransition = updateTransition(isSelected)
  val containerColor =
    selectedTransition.animateColor {
      if (it) MaterialTheme.colorScheme.primaryContainer
      else MaterialTheme.colorScheme.surfaceBright
    }

  val headlineColor =
    selectedTransition.animateColor {
      if (it) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
    }
  val supportingColor =
    selectedTransition.animateColor {
      if (it) MaterialTheme.colorScheme.onPrimaryContainer
      else MaterialTheme.colorScheme.onSurfaceVariant
    }
  ListItem2(
    modifier = modifier,
    leadingContent = {
      Image(
        bitmap = app.icon,
        contentDescription = null,
        modifier = Modifier.size(46.dp).clip(MaterialTheme.shapes.medium),
      )
    },
    headlineContent = { Text(text = app.label) },
    supportingContent = { Text(text = app.packageId) },
    colors =
      ListItemDefaults.colors(
        headlineColor = headlineColor.value,
        containerColor = containerColor.value,
        supportingColor = supportingColor.value,
      ),
    shape = shape,
  )
}

class AppSelectorViewModel(
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

@Composable
@Preview
private fun PreviewAppSelectorSheetContent(
  @PreviewParameter(UIStateCollection::class) uiState: AppSelectorUIState
) = Preview2 {
  var currentUiState by remember { mutableStateOf(uiState) }
  AppSelectorSheetContent(
    onUpdateSelected = { currentUiState = currentUiState.copy(selectedPackageName = it) },
    uiState = currentUiState,
  )
}

@Suppress("MagicNumber")
private class UIStateCollection(
  override val values: Sequence<AppSelectorUIState> =
    sequenceOf(
      AppSelectorUIState(selectedPackageName = null, allApps = emptyList()),
      AppSelectorUIState(selectedPackageName = null, allApps = null),
      AppSelectorUIState(
        selectedPackageName = null,
        allApps =
          List(9) {
            InstalledApp(
              label = "App $it",
              packageId = "app.$it",
              icon =
                createBitmap(100, 100).apply { this.eraseColor(Color.Red.toArgb()) }.asImageBitmap(),
            )
          },
      ),
      AppSelectorUIState(
        selectedPackageName = "app.3",
        allApps =
          List(15) {
            InstalledApp(
              label = "App $it",
              packageId = "app.$it",
              icon =
                createBitmap(100, 100).apply { this.eraseColor(Color.Red.toArgb()) }.asImageBitmap(),
            )
          },
      ),
    )
) : PreviewParameterProvider<AppSelectorUIState>
