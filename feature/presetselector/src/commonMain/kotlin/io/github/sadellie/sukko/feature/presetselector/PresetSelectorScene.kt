package io.github.sadellie.sukko.feature.presetselector

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import dev.zacsweers.metrox.viewmodel.metroViewModel
import google.material.design.symbols.EmojiPeople
import google.material.design.symbols.Symbols
import io.github.sadellie.sukko.core.common.collectAsStateWithLifecycleKMP
import io.github.sadellie.sukko.core.common.stateIn
import io.github.sadellie.sukko.core.data.WidgetDataPresetCustomRepository
import io.github.sadellie.sukko.core.designsystem.LocalFilesDirPath
import io.github.sadellie.sukko.core.designsystem.Preview2
import io.github.sadellie.sukko.core.designsystem.theme.Sizes
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.WidgetDataPreset
import io.github.sadellie.sukko.core.ui.LoadingBox
import io.github.sadellie.sukko.core.ui.NavigateUpButton
import io.github.sadellie.sukko.core.ui.ScaffoldWithLargeTopAppBar
import io.github.sadellie.sukko.core.ui.ScenePlaceholder
import io.github.sadellie.sukko.core.ui.WidgetDataPresetList
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.common_preset_list_placeholder_text
import io.github.sadellie.sukko.resources.common_preset_list_placeholder_title
import io.github.sadellie.sukko.resources.preset_selector_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun PresetSelectorScene(
  onNavigateUp: () -> Unit,
  onSelect: (presetId: Long, isBuiltIn: Boolean) -> Unit,
) {
  val viewModel = metroViewModel<PresetSelectorViewModel>()
  val presets = viewModel.presets.collectAsStateWithLifecycleKMP()
  PresetSelectorScreen(onNavigateUp = onNavigateUp, onSelect = onSelect, presets = presets.value)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PresetSelectorScreen(
  onNavigateUp: () -> Unit,
  onSelect: (presetId: Long, isBuiltIn: Boolean) -> Unit,
  presets: List<WidgetDataPreset>?,
) {
  val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
  ScaffoldWithLargeTopAppBar(
    title = { Text(stringResource(Res.string.preset_selector_title)) },
    navigationIcon = { NavigateUpButton(onNavigateUp) },
    scrollBehavior = scrollBehavior,
  ) { paddingValues ->
    val filesDirPath = LocalFilesDirPath.current
    if (presets == null) {
      LoadingBox(modifier = Modifier.padding(paddingValues).fillMaxSize())
    } else {
      WidgetDataPresetList(
        modifier =
          Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
            .consumeWindowInsets(paddingValues)
            .padding(horizontal = Sizes.large)
            .fillMaxSize(),
        widgetDataPresetsCustom = presets,
        widgetDataPresetsBuiltIn = remember { WidgetDataPreset.builtIns() },
        key = { it.presetId },
        previewSrc = { it.getPreviewPath(filesDirPath).toString() },
        name = { it.name },
        contentPadding = paddingValues,
        placeholder = {
          ScenePlaceholder(
            modifier = Modifier.padding(Sizes.large).fillMaxWidth(),
            icon = Symbols.EmojiPeople,
            title = stringResource(Res.string.common_preset_list_placeholder_title),
            text = stringResource(Res.string.common_preset_list_placeholder_text),
          )
        },
        onClick = { onSelect(it.presetId, it is WidgetDataPreset.BuiltIn) },
      )
    }
  }
}

@Inject
@ViewModelKey
@ContributesIntoMap(AppScope::class)
class PresetSelectorViewModel(widgetDataPresetCustomRepository: WidgetDataPresetCustomRepository) :
  ViewModel() {
  val presets =
    widgetDataPresetCustomRepository
      .allWidgetDataPresets(decodeExtra = false)
      .stateIn(viewModelScope, null)
}

@Composable
@Preview
private fun PreviewPresetSelectorScene() = Preview2 {
  PresetSelectorScreen(
    onNavigateUp = {},
    onSelect = { _, _ -> },
    presets =
      List(4) {
        WidgetDataPreset.Custom(
          presetId = it.toLong(),
          name = "Widget $it",
          layers = emptyList(),
          globals = Globals(),
        )
      },
  )
}
