package io.github.sadellie.sukko.feature.saveaspreset

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavKey
import co.touchlab.kermit.Logger
import coil3.compose.AsyncImage
import google.material.design.symbols.Save
import google.material.design.symbols.Symbols
import io.github.sadellie.sukko.core.common.collectAsStateWithLifecycleKMP
import io.github.sadellie.sukko.core.data.WidgetDataPresetCustomRepository
import io.github.sadellie.sukko.core.data.WidgetDataRepository
import io.github.sadellie.sukko.core.designsystem.LocalImageLoader
import io.github.sadellie.sukko.core.designsystem.Preview2
import io.github.sadellie.sukko.core.designsystem.theme.Sizes
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.WidgetDataPreset
import io.github.sadellie.sukko.core.ui.ErrorScreenPlaceholder
import io.github.sadellie.sukko.core.ui.LargeButton
import io.github.sadellie.sukko.core.ui.LoadingScaffold
import io.github.sadellie.sukko.core.ui.LoadingScaffoldWithTopAppBar
import io.github.sadellie.sukko.core.ui.NavigateUpButton
import io.github.sadellie.sukko.core.ui.ScaffoldWithTopAppBar
import io.github.sadellie.sukko.core.ui.SukkoOutlinedTextField
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.common_error
import io.github.sadellie.sukko.resources.common_preset_name
import io.github.sadellie.sukko.resources.common_save
import io.github.sadellie.sukko.resources.save_as_preset_title
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import okio.Path
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Serializable data class SaveAsPresetRoute(val appWidgetId: Int) : NavKey

@Composable
fun SaveAsPresetScene(onNavigateUp: () -> Unit, appWidgetId: Int) {
  val viewModel: SaveAsPresetViewModel =
    koinViewModel(key = appWidgetId.toString()) { parametersOf(appWidgetId) }
  val uiState = viewModel.uiState.collectAsStateWithLifecycleKMP().value

  when (uiState) {
    null -> LoadingScaffoldWithTopAppBar(onNavigateUp = onNavigateUp, disableBack = false)
    SaveAsPresetUIState.Done,
    SaveAsPresetUIState.Saving -> {
      LoadingScaffold(disableBack = true)
      LaunchedEffect(uiState) { if (uiState is SaveAsPresetUIState.Done) onNavigateUp() }
    }
    SaveAsPresetUIState.Error ->
      ErrorScreenPlaceholder(
        onNavigateUp = onNavigateUp,
        screenTitle = stringResource(Res.string.common_error),
      )
    is SaveAsPresetUIState.Ready ->
      SaveAsPresetScreen(
        onNavigateUp = onNavigateUp,
        onSave = viewModel::savePreset,
        uiState = uiState,
      )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SaveAsPresetScreen(
  onNavigateUp: () -> Unit,
  onSave: (presetName: String) -> Unit,
  uiState: SaveAsPresetUIState.Ready,
) {
  ScaffoldWithTopAppBar(
    title = { Text(stringResource(Res.string.save_as_preset_title)) },
    navigationIcon = { NavigateUpButton(onNavigateUp) },
  ) { paddingValues ->
    Column(
      modifier = Modifier.verticalScroll(rememberScrollState()).padding(paddingValues),
      verticalArrangement = Arrangement.spacedBy(Sizes.large),
    ) {
      val previewPath = remember(uiState.widgetDataPreview) { uiState.widgetDataPreview.toString() }
      AsyncImage(
        modifier = Modifier.fillMaxWidth().heightIn(max = 300.dp),
        model = previewPath,
        imageLoader = LocalImageLoader.current,
        contentDescription = null,
      )

      val presetName = rememberTextFieldState()
      SukkoOutlinedTextField(
        state = presetName,
        modifier = Modifier.fillMaxWidth().padding(horizontal = Sizes.large),
        label = { Text(stringResource(Res.string.common_preset_name)) },
        lineLimits = TextFieldLineLimits.SingleLine,
      )

      LargeButton(
        modifier = Modifier.fillMaxWidth().padding(horizontal = Sizes.large),
        onClick = { onSave(presetName.text.toString()) },
        label = stringResource(Res.string.common_save),
        icon = Symbols.Save,
        contentDescription = null,
        enabled = presetName.text.isNotBlank(),
      )
    }
  }
}

class SaveAsPresetViewModel(
  private val appWidgetId: Int,
  private val widgetDataRepository: WidgetDataRepository,
  private val widgetDataPresetCustomRepository: WidgetDataPresetCustomRepository,
) : ViewModel() {
  @OptIn(ExperimentalCoroutinesApi::class)
  internal val uiState = MutableStateFlow<SaveAsPresetUIState?>(null)

  fun savePreset(presetName: String) =
    viewModelScope.launch(Dispatchers.Default) {
      try {
        uiState.update { SaveAsPresetUIState.Saving }
        // id will be updated internally
        val widgetData = widgetDataRepository.loadByAppWidgetId(appWidgetId)
        val previewPath = widgetData?.let { widgetDataRepository.getPreview(it) }
        val widgetDataPreset =
          WidgetDataPreset.Custom(
            presetId = 0,
            name = presetName,
            layers = widgetData?.layers ?: emptyList(),
            globals = widgetData?.globals ?: Globals(),
          )
        widgetDataPresetCustomRepository.insertNewWithPreview(
          widgetDataPreset = widgetDataPreset,
          previewPath = previewPath,
        )
        uiState.update { SaveAsPresetUIState.Done }
      } catch (e: Exception) {
        Logger.e(e) { "Failed to save preset" }
        uiState.update { SaveAsPresetUIState.Error }
      }
    }

  init {
    viewModelScope.launch {
      val data = widgetDataRepository.loadByAppWidgetId(appWidgetId)
      val previewPath = data?.let { widgetDataRepository.getPreview(it) }
      uiState.update {
        if (data == null) SaveAsPresetUIState.Error else SaveAsPresetUIState.Ready(previewPath)
      }
    }
  }
}

internal sealed interface SaveAsPresetUIState {
  data object Error : SaveAsPresetUIState

  data object Saving : SaveAsPresetUIState

  data object Done : SaveAsPresetUIState

  data class Ready(val widgetDataPreview: Path?) : SaveAsPresetUIState
}

@Preview
@Composable
private fun PreviewSaveAsPresetScreen() = Preview2 {
  SaveAsPresetScreen(
    onNavigateUp = {},
    onSave = {},
    uiState = SaveAsPresetUIState.Ready(widgetDataPreview = null),
  )
}
