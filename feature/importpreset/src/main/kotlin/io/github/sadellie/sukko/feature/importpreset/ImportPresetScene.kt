package io.github.sadellie.sukko.feature.importpreset

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavKey
import co.touchlab.kermit.Logger
import coil3.compose.AsyncImage
import google.material.design.symbols.Add
import google.material.design.symbols.Symbols
import io.github.sadellie.sukko.core.data.IconPackCustomRepository
import io.github.sadellie.sukko.core.designsystem.LocalImageLoader
import io.github.sadellie.sukko.core.designsystem.Preview2
import io.github.sadellie.sukko.core.designsystem.theme.ListArrangement
import io.github.sadellie.sukko.core.designsystem.theme.Sizes
import io.github.sadellie.sukko.core.iconfiles.IconPack
import io.github.sadellie.sukko.core.importexport.WidgetDataPresetExportImport
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.ImportingFontFile
import io.github.sadellie.sukko.core.model.ImportingIconPack
import io.github.sadellie.sukko.core.model.ImportingIconPackAction
import io.github.sadellie.sukko.core.model.ImportingWidgetDataPreset
import io.github.sadellie.sukko.core.model.WidgetDataPreset
import io.github.sadellie.sukko.core.ui.AlertDialogWithRadioItems
import io.github.sadellie.sukko.core.ui.AlertDialogWithText
import io.github.sadellie.sukko.core.ui.ErrorScreenPlaceholder
import io.github.sadellie.sukko.core.ui.LargeButton
import io.github.sadellie.sukko.core.ui.ListItem2
import io.github.sadellie.sukko.core.ui.LoadingScaffold
import io.github.sadellie.sukko.core.ui.NavigateUpButton
import io.github.sadellie.sukko.core.ui.ScaffoldWithTopAppBar
import io.github.sadellie.sukko.core.ui.listedShape
import io.github.sadellie.sukko.core.ui.singleShape
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.common_create_new
import io.github.sadellie.sukko.resources.common_preset_name
import io.github.sadellie.sukko.resources.import_preset_disclaimer_text
import io.github.sadellie.sukko.resources.import_preset_disclaimer_title
import io.github.sadellie.sukko.resources.import_preset_do_not_import
import io.github.sadellie.sukko.resources.import_preset_error_title
import io.github.sadellie.sukko.resources.import_preset_import
import io.github.sadellie.sukko.resources.import_preset_merge_into
import io.github.sadellie.sukko.resources.import_preset_overview_title
import io.github.sadellie.sukko.resources.import_preset_selected_action
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import okio.Path.Companion.toPath
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Serializable data class ImportPresetRoute(val selectedFileUri: String) : NavKey

@Composable
fun ImportPresetScene(navigateUp: () -> Unit, importingPresetUri: String) {
  val viewModel =
    koinViewModel<ImportPresetViewModel>(key = importingPresetUri) {
      parametersOf(importingPresetUri)
    }
  val forceNavigateUp = viewModel.navigateUpCallback.collectAsStateWithLifecycle(false).value

  LaunchedEffect(forceNavigateUp) { if (forceNavigateUp) navigateUp() }

  ImportPresetScreen(
    navigateUp = navigateUp,
    uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
    onConfirm = viewModel::import,
    onImportingIconPackUpdate = viewModel::onImportingIconPackUpdate,
    onImportingFontFileUpdate = viewModel::onImportingFontFileUpdate,
  )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun ImportPresetScreen(
  navigateUp: () -> Unit,
  uiState: ImportPresetUIState,
  onConfirm: () -> Unit,
  onImportingIconPackUpdate: (ImportingIconPack) -> Unit,
  onImportingFontFileUpdate: (ImportingFontFile) -> Unit,
) {
  when (uiState) {
    ImportPresetUIState.Importing -> LoadingScaffold(disableBack = true)
    ImportPresetUIState.Preloading -> LoadingScaffold(disableBack = false)
    is ImportPresetUIState.PresetOverview ->
      PresetOverviewScreen(
        navigateUp = navigateUp,
        uiState = uiState,
        onConfirm = onConfirm,
        onImportingIconPackUpdate = onImportingIconPackUpdate,
        onImportingFontFileUpdate = onImportingFontFileUpdate,
      )
    ImportPresetUIState.Error ->
      ErrorScreenPlaceholder(
        onNavigateUp = navigateUp,
        screenTitle = stringResource(Res.string.import_preset_error_title),
      )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PresetOverviewScreen(
  navigateUp: () -> Unit,
  onConfirm: () -> Unit,
  onImportingIconPackUpdate: (ImportingIconPack) -> Unit,
  onImportingFontFileUpdate: (ImportingFontFile) -> Unit,
  uiState: ImportPresetUIState.PresetOverview,
) {
  var showImportDisclaimer by rememberSaveable { mutableStateOf(false) }
  ScaffoldWithTopAppBar(
    title = { Text(stringResource(Res.string.import_preset_overview_title)) },
    navigationIcon = { NavigateUpButton(navigateUp) },
  ) { paddingValues ->
    Column(
      modifier = Modifier.verticalScroll(rememberScrollState()).padding(paddingValues),
      verticalArrangement = Arrangement.spacedBy(Sizes.small),
    ) {
      if (uiState.importingWidgetDataPreset.fullPreviewPath != null) {
        AsyncImage(
          modifier = Modifier.fillMaxWidth().heightIn(max = 300.dp),
          model = uiState.importingWidgetDataPreset.fullPreviewPath.toString(),
          imageLoader = LocalImageLoader.current,
          contentDescription = null,
        )
      }
      ListItem2(
        modifier = Modifier.padding(horizontal = Sizes.large).clip(MaterialTheme.shapes.large),
        headlineContent = { Text(stringResource(Res.string.common_preset_name)) },
        supportingContent = { Text(uiState.importingWidgetDataPreset.widgetDataPreset.name) },
        shape = ListItemDefaults.singleShape,
      )

      if (uiState.importingWidgetDataPreset.importingIconPacks.isNotEmpty()) {
        IconPackImporter(
          modifier = Modifier.fillMaxWidth().padding(horizontal = Sizes.large),
          onImportingIconPackUpdate = onImportingIconPackUpdate,
          iconPackActions = uiState.iconPackActions,
          importingIconPacks = uiState.importingWidgetDataPreset.importingIconPacks,
        )
      }

      if (uiState.importingWidgetDataPreset.importingFontFiles.isNotEmpty()) {
        FontFileImporter(
          modifier = Modifier.fillMaxWidth().padding(horizontal = Sizes.large),
          onImportingFontFileUpdate = onImportingFontFileUpdate,
          importingFontFiles = uiState.importingWidgetDataPreset.importingFontFiles,
        )
      }

      LargeButton(
        modifier =
          Modifier.fillMaxWidth()
            .padding(start = Sizes.large, end = Sizes.large, bottom = Sizes.large),
        onClick = { showImportDisclaimer = true },
        label = stringResource(Res.string.import_preset_import),
        icon = Symbols.Add,
        contentDescription = null,
      )
    }
  }

  if (showImportDisclaimer) {
    AlertDialogWithText(
      onDismiss = { showImportDisclaimer = false },
      onConfirm = onConfirm,
      confirmButtonLabel = stringResource(Res.string.import_preset_import),
      title = stringResource(Res.string.import_preset_disclaimer_title),
      text = stringResource(Res.string.import_preset_disclaimer_text),
    )
  }
}

@Composable
private fun IconPackImporter(
  modifier: Modifier,
  onImportingIconPackUpdate: (ImportingIconPack) -> Unit,
  iconPackActions: List<ImportingIconPackAction>,
  importingIconPacks: List<ImportingIconPack>,
) {
  Column(modifier = modifier, verticalArrangement = ListArrangement) {
    importingIconPacks.forEachIndexed { index, importingIconPack ->
      var showActionSelector by remember { mutableStateOf(false) }
      ListItem2(
        headlineContent = { Text(importingIconPack.importingName) },
        supportingContent = { Text(importingIconPack.action.displayName()) },
        modifier = Modifier.fillMaxWidth().clickable { showActionSelector = true },
        shape = ListItemDefaults.listedShape(index, importingIconPacks.size),
      )
      if (showActionSelector) {
        AlertDialogWithRadioItems(
          title = stringResource(Res.string.import_preset_selected_action),
          onDismiss = { showActionSelector = false },
          items = iconPackActions,
          key = null,
          headlineText = { it.displayName() },
          onClick = { onImportingIconPackUpdate(importingIconPack.copy(action = it)) },
          isSelected = { importingIconPack.action == it },
        )
      }
    }
  }
}

@Composable
private fun FontFileImporter(
  modifier: Modifier,
  onImportingFontFileUpdate: (ImportingFontFile) -> Unit,
  importingFontFiles: List<ImportingFontFile>,
) {
  Column(modifier = modifier, verticalArrangement = ListArrangement) {
    importingFontFiles.forEachIndexed { index, importingFontFile ->
      ListItem2(
        headlineContent = { Text(importingFontFile.importingName) },
        supportingContent = {
          Text(
            stringResource(
              if (importingFontFile.import) Res.string.import_preset_import
              else Res.string.import_preset_do_not_import
            )
          )
        },
        trailingContent = { Switch(importingFontFile.import, null) },
        modifier =
          Modifier.clickable {
            onImportingFontFileUpdate(importingFontFile.copy(import = !importingFontFile.import))
          },
        shape = ListItemDefaults.listedShape(index, importingFontFiles.size),
      )
    }
  }
}

@Composable
private fun ImportingIconPackAction.displayName() =
  when (this) {
    ImportingIconPackAction.CreateNew -> stringResource(Res.string.common_create_new)
    is ImportingIconPackAction.Merge ->
      stringResource(Res.string.import_preset_merge_into, destinationIconPack.name)
  }

class ImportPresetViewModel(
  private val importingPresetUri: String,
  private val widgetDataPresetExportImport: WidgetDataPresetExportImport,
  private val iconPackCustomRepository: IconPackCustomRepository,
) : ViewModel() {
  internal val uiState = MutableStateFlow<ImportPresetUIState>(ImportPresetUIState.Preloading)
  internal val navigateUpCallback = MutableSharedFlow<Boolean>()

  fun preload() =
    viewModelScope.launch {
      try {
        uiState.update { ImportPresetUIState.Preloading }
        val importingWidgetDataPreset = widgetDataPresetExportImport.preloadData(importingPresetUri)
        val customIconPacks = iconPackCustomRepository.getAll().first()
        uiState.update {
          ImportPresetUIState.PresetOverview(
            importingWidgetDataPreset = importingWidgetDataPreset,
            iconPackActions = ImportingIconPackAction.actions(customIconPacks),
          )
        }
      } catch (e: Exception) {
        Logger.e(TAG, e) { "Failed to preload" }
        uiState.update { ImportPresetUIState.Error }
      }
    }

  fun import() =
    viewModelScope.launch {
      try {
        val currentUiState = uiState.value
        if (currentUiState !is ImportPresetUIState.PresetOverview) return@launch
        val currentPreview = currentUiState.importingWidgetDataPreset
        uiState.update { ImportPresetUIState.Importing }
        widgetDataPresetExportImport.import(currentPreview)
      } catch (e: Exception) {
        Logger.e(TAG, e) { "Failed to import" }
        uiState.update { ImportPresetUIState.Error }
      }
      navigateUpCallback.emit(true)
    }

  fun onImportingIconPackUpdate(updatedImportingIconPack: ImportingIconPack) {
    uiState.update { currentState ->
      if (currentState !is ImportPresetUIState.PresetOverview) return@update currentState
      val modifiedImportPacks =
        currentState.importingWidgetDataPreset.importingIconPacks.toMutableList()
      val indexToModify =
        modifiedImportPacks.indexOfFirst { it.importingId == updatedImportingIconPack.importingId }
      modifiedImportPacks[indexToModify] = updatedImportingIconPack
      val updatedPreview =
        currentState.importingWidgetDataPreset.copy(importingIconPacks = modifiedImportPacks)
      currentState.copy(importingWidgetDataPreset = updatedPreview)
    }
  }

  fun onImportingFontFileUpdate(updatedImportingFontFile: ImportingFontFile) {
    uiState.update { currentState ->
      if (currentState !is ImportPresetUIState.PresetOverview) return@update currentState
      val modifierImportingFontFiles =
        currentState.importingWidgetDataPreset.importingFontFiles.toMutableList()
      val indexToModify =
        modifierImportingFontFiles.indexOfFirst {
          it.importingName == updatedImportingFontFile.importingName
        }
      modifierImportingFontFiles[indexToModify] = updatedImportingFontFile
      val updatePreview =
        currentState.importingWidgetDataPreset.copy(importingFontFiles = modifierImportingFontFiles)
      currentState.copy(importingWidgetDataPreset = updatePreview)
    }
  }

  init {
    preload()
  }
}

internal sealed interface ImportPresetUIState {
  data object Preloading : ImportPresetUIState

  data object Error : ImportPresetUIState

  data class PresetOverview(
    val importingWidgetDataPreset: ImportingWidgetDataPreset,
    val iconPackActions: List<ImportingIconPackAction>,
  ) : ImportPresetUIState

  data object Importing : ImportPresetUIState
}

private const val TAG = "ImportPresetScene"

@Composable
@Preview
private fun PreviewImportPresetScreen(
  @PreviewParameter(ImportPresetUIStateCollection::class) uiState: ImportPresetUIState
) = Preview2 {
  var currentUiState by remember { mutableStateOf(uiState) }
  ImportPresetScreen(
    navigateUp = {},
    uiState = currentUiState,
    onConfirm = {},
    onImportingIconPackUpdate = {},
    onImportingFontFileUpdate = {},
  )
}

@Suppress("MagicNumber")
private class ImportPresetUIStateCollection(
  override val values: Sequence<ImportPresetUIState> =
    sequenceOf(
      ImportPresetUIState.Preloading,
      ImportPresetUIState.Importing,
      ImportPresetUIState.Error,
      ImportPresetUIState.PresetOverview(
        importingWidgetDataPreset =
          ImportingWidgetDataPreset(
            widgetDataPreset =
              WidgetDataPreset.Custom(0, "Preset 1", emptyList(), globals = Globals()),
            fullPreviewPath = null,
            importingIconPacks = emptyList(),
            importingFontFiles = emptyList(),
          ),
        iconPackActions =
          ImportingIconPackAction.actions(
            List(5) { IconPack.Custom(iconPackId = it.toLong(), name = "icon pack $it") }
          ),
      ),
      ImportPresetUIState.PresetOverview(
        importingWidgetDataPreset =
          ImportingWidgetDataPreset(
            widgetDataPreset =
              WidgetDataPreset.Custom(0, "Preset 1", emptyList(), globals = Globals()),
            fullPreviewPath = "path.png".toPath(),
            importingIconPacks =
              listOf(
                ImportingIconPack(
                  importingId = 0,
                  importingName = "Importing icon pack 0",
                  action = ImportingIconPackAction.CreateNew,
                ),
                ImportingIconPack(
                  importingId = 1,
                  importingName = "Importing icon pack 1",
                  action = ImportingIconPackAction.CreateNew,
                ),
                ImportingIconPack(
                  importingId = 2,
                  importingName = "Importing icon pack 2",
                  action = ImportingIconPackAction.Merge(IconPack.Custom(1L, "Icon pack 1")),
                ),
              ),
            importingFontFiles = List(5) { ImportingFontFile("font1.otf", it % 2 == 0) },
          ),
        iconPackActions =
          ImportingIconPackAction.actions(
            List(5) { IconPack.Custom(iconPackId = it.toLong(), name = "icon pack $it") }
          ),
      ),
    )
) : PreviewParameterProvider<ImportPresetUIState>
