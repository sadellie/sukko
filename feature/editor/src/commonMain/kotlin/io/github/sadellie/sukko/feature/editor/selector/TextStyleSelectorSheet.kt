package io.github.sadellie.sukko.feature.editor.selector

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.composables.core.ModalBottomSheetState
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import io.github.sadellie.sukko.core.common.stateIn
import io.github.sadellie.sukko.core.data.TextStyleSourceEvaluator
import io.github.sadellie.sukko.core.designsystem.Preview2
import io.github.sadellie.sukko.core.designsystem.theme.ListArrangement
import io.github.sadellie.sukko.core.designsystem.theme.Sizes
import io.github.sadellie.sukko.core.fontfiles.FontFile
import io.github.sadellie.sukko.core.model.GlobalValueCache
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.basic.FontStyleSource
import io.github.sadellie.sukko.core.model.basic.GlobalValue
import io.github.sadellie.sukko.core.model.basic.LocalScriptableDisplay
import io.github.sadellie.sukko.core.model.basic.ScriptableDouble
import io.github.sadellie.sukko.core.model.basic.TextAlignSource
import io.github.sadellie.sukko.core.model.basic.TextStyleSource
import io.github.sadellie.sukko.core.ui.AlertDialogWithRadioItems
import io.github.sadellie.sukko.core.ui.BackHandler
import io.github.sadellie.sukko.core.ui.ListItem2
import io.github.sadellie.sukko.core.ui.SheetContentWithButtons
import io.github.sadellie.sukko.core.ui.firstShapes
import io.github.sadellie.sukko.core.ui.hide
import io.github.sadellie.sukko.core.ui.lastShapes
import io.github.sadellie.sukko.core.ui.middleShapes
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.common_back
import io.github.sadellie.sukko.resources.common_select
import io.github.sadellie.sukko.resources.core_model_text_style_global
import io.github.sadellie.sukko.resources.core_model_text_style_local
import io.github.sadellie.sukko.resources.editor_parameters_font_style
import io.github.sadellie.sukko.resources.editor_selector_text_style_align
import io.github.sadellie.sukko.resources.editor_selector_text_style_font
import io.github.sadellie.sukko.resources.editor_selector_text_style_preview
import io.github.sadellie.sukko.resources.editor_selector_text_style_size
import io.github.sadellie.sukko.resources.editor_selector_text_style_weight
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun TextStyleSelectorSheet(
  state: ModalBottomSheetState,
  onValueSelected: (newValue: TextStyleSource) -> Unit,
  value: TextStyleSource,
  globals: Globals,
) {
  var inputMode by rememberSaveable {
    mutableStateOf(TextStyleInputMode.determineInitialMode(value))
  }

  SelectorSheetTemplate(
    state = state,
    inputModes = remember { TextStyleInputMode.entries },
    currentInputMode = inputMode,
    onInputModeUpdate = { inputMode = it },
  ) { currentMode ->
    when (currentMode) {
      TextStyleInputMode.LOCAL ->
        LocalTextStyle(
          onDismiss = state::hide,
          onConfirm = onValueSelected,
          initialValue = value,
          globals = globals,
        )
      TextStyleInputMode.GLOBAL ->
        GlobalTextStyle(
          onDismiss = state::hide,
          onConfirm = onValueSelected,
          initialValue = value,
          globals = globals,
        )
    }
  }
}

@Composable
private fun LocalTextStyle(
  onDismiss: () -> Unit,
  onConfirm: (value: TextStyleSource.Local) -> Unit,
  initialValue: TextStyleSource,
  globals: Globals,
) {
  val viewModel =
    assistedMetroViewModel<TextStyleSelectorViewModel, TextStyleSelectorViewModel.Factory> {
      create(initialValue, globals)
    }
  var page by remember { mutableStateOf<SelectorPage>(SelectorPage.Parameters) }
  BackHandler(page != SelectorPage.Parameters) { page = SelectorPage.Parameters }
  val textStyleSource = viewModel.textStyleSource.collectAsStateWithLifecycle().value
  val textStyle = viewModel.evaluatedTextStyle.collectAsStateWithLifecycle().value
  Column(verticalArrangement = Arrangement.spacedBy(Sizes.small)) {
    Text(
      text = stringResource(Res.string.editor_selector_text_style_preview),
      style = textStyle,
      color = MaterialTheme.colorScheme.onSurface,
      modifier =
        Modifier.padding(horizontal = Sizes.large)
          .fillMaxWidth()
          .clip(MaterialTheme.shapes.large)
          .background(MaterialTheme.colorScheme.surfaceContainerHigh)
          .padding(Sizes.large),
    )

    AnimatedContent(targetState = page, modifier = Modifier.fillMaxWidth()) { currentPage ->
      when (currentPage) {
        is SelectorPage.FontFileSelector ->
          FontFileSelectorSheetContent(
            onDismissRequest = { page = SelectorPage.Parameters },
            onValueSelected = {
              viewModel.onTextStyleSourceUpdate(textStyleSource.copy(fontFile = it))
            },
            value = textStyleSource.fontFile,
            dismissLabel = stringResource(Res.string.common_back),
            confirmLabel = stringResource(Res.string.common_select),
          )
        is SelectorPage.FontSizeSelector ->
          DoubleSelectorSheetContent(
            onDismissRequest = { page = SelectorPage.Parameters },
            onValueSelected = {
              if (it != null) viewModel.onTextStyleSourceUpdate(textStyleSource.copy(fontSize = it))
            },
            value = currentPage.fonSize,
            range = TextStyleSource.fontSizeRange,
            allowFraction = true,
            allowNullable = false,
            globals = globals,
            dismissLabel = stringResource(Res.string.common_back),
            confirmLabel = stringResource(Res.string.common_select),
          )
        is SelectorPage.FontWeightSelector ->
          DoubleSelectorSheetContent(
            onDismissRequest = { page = SelectorPage.Parameters },
            onValueSelected = {
              if (it != null)
                viewModel.onTextStyleSourceUpdate(textStyleSource.copy(fontWeight = it))
            },
            value = currentPage.fontWeight,
            range = TextStyleSource.fontWeightRange,
            allowFraction = false,
            globals = globals,
            dismissLabel = stringResource(Res.string.common_back),
            confirmLabel = stringResource(Res.string.common_select),
            allowNullable = false,
          )
        SelectorPage.Parameters ->
          LocalTextStyleParameters(
            onConfirm = { onConfirm(textStyleSource) },
            onDismiss = onDismiss,
            switchPage = { page = it },
            onTextStyleSourceUpdate = viewModel::onTextStyleSourceUpdate,
            textStyleSource = textStyleSource,
          )
      }
    }
  }
}

@Composable
private fun LocalTextStyleParameters(
  onDismiss: () -> Unit,
  onConfirm: () -> Unit,
  switchPage: (page: SelectorPage) -> Unit,
  onTextStyleSourceUpdate: (newTextStyleSource: TextStyleSource.Local) -> Unit,
  textStyleSource: TextStyleSource.Local,
) {
  SheetContentWithButtons(onDismiss = onDismiss, onConfirm = onConfirm) {
    Column(
      modifier = Modifier.verticalScroll(rememberScrollState()).padding(horizontal = Sizes.large),
      verticalArrangement = ListArrangement,
    ) {
      val scriptableDisplay = LocalScriptableDisplay.current
      ListItem2(
        content = { Text(stringResource(Res.string.editor_selector_text_style_font)) },
        supportingContent = { Text(textStyleSource.fontFile.toDisplayString()) },
        onClick = { switchPage(SelectorPage.FontFileSelector(textStyleSource.fontFile)) },
        shapes = ListItemDefaults.firstShapes,
      )
      ListItem2(
        content = { Text(stringResource(Res.string.editor_selector_text_style_size)) },
        supportingContent = { Text(scriptableDisplay.displayString(textStyleSource.fontSize)) },
        onClick = { switchPage(SelectorPage.FontSizeSelector(textStyleSource.fontSize)) },
        shapes = ListItemDefaults.middleShapes,
      )
      ListItem2(
        content = { Text(stringResource(Res.string.editor_selector_text_style_weight)) },
        supportingContent = { Text(scriptableDisplay.displayString(textStyleSource.fontWeight)) },
        onClick = { switchPage(SelectorPage.FontWeightSelector(textStyleSource.fontWeight)) },
        shapes = ListItemDefaults.middleShapes,
      )
      var showTextAlignDialog by rememberSaveable { mutableStateOf(false) }
      ListItem2(
        content = { Text(stringResource(Res.string.editor_selector_text_style_align)) },
        supportingContent = { Text(stringResource(textStyleSource.textAlignSource.displayName)) },
        onClick = { showTextAlignDialog = true },
        shapes = ListItemDefaults.middleShapes,
      )
      if (showTextAlignDialog) {
        AlertDialogWithRadioItems(
          title = stringResource(Res.string.editor_selector_text_style_align),
          onDismiss = { showTextAlignDialog = false },
          headlineText = { stringResource(it.displayName) },
          items = remember { TextAlignSource.values() },
          isSelected = { it == textStyleSource.textAlignSource },
          key = null,
          onClick = { onTextStyleSourceUpdate(textStyleSource.copy(textAlignSource = it)) },
        )
      }
      var showFontStyleDialog by rememberSaveable { mutableStateOf(false) }
      ListItem2(
        content = { Text(stringResource(Res.string.editor_parameters_font_style)) },
        supportingContent = { Text(stringResource(textStyleSource.fontStyle.displayName)) },
        onClick = { showFontStyleDialog = true },
        shapes = ListItemDefaults.lastShapes,
      )
      if (showFontStyleDialog) {
        AlertDialogWithRadioItems(
          title = stringResource(Res.string.editor_parameters_font_style),
          onDismiss = { showFontStyleDialog = false },
          headlineText = { stringResource(it.displayName) },
          items = remember { FontStyleSource.values() },
          isSelected = { it == textStyleSource.fontStyle },
          key = null,
          onClick = { onTextStyleSourceUpdate(textStyleSource.copy(fontStyle = it)) },
        )
      }
    }
  }
}

@Composable
private fun GlobalTextStyle(
  onDismiss: () -> Unit,
  onConfirm: (value: TextStyleSource.Global) -> Unit,
  initialValue: TextStyleSource,
  globals: Globals,
) {
  GlobalSelectorSheetContent(
    onDismiss = onDismiss,
    onConfirm = { onConfirm(TextStyleSource.Global(it)) },
    initialGlobalId = remember { (initialValue as? TextStyleSource.Global)?.id },
    globals = globals.textStyles,
  )
}

private sealed interface SelectorPage {
  data object Parameters : SelectorPage

  data class FontFileSelector(val fontFile: FontFile) : SelectorPage

  data class FontSizeSelector(val fonSize: ScriptableDouble) : SelectorPage

  data class FontWeightSelector(val fontWeight: ScriptableDouble) : SelectorPage
}

private enum class TextStyleInputMode(override val displayName: StringResource) : InputMode {
  LOCAL(Res.string.core_model_text_style_local),
  GLOBAL(Res.string.core_model_text_style_global);

  companion object {
    fun determineInitialMode(textStyleSource: TextStyleSource) =
      when (textStyleSource) {
        is TextStyleSource.Local -> LOCAL
        is TextStyleSource.Global -> GLOBAL
      }
  }
}

@AssistedInject
class TextStyleSelectorViewModel(
  @Assisted initialValue: TextStyleSource,
  @Assisted private val globals: Globals,
  textStyleSourceEvaluatorFactory: TextStyleSourceEvaluator.Factory,
) : ViewModel() {
  private val _textStyleSource =
    MutableStateFlow(initialValue as? TextStyleSource.Local ?: TextStyleSource.Local())
  internal val textStyleSource = _textStyleSource.asStateFlow()
  private val _textStyleSourceEvaluator =
    textStyleSourceEvaluatorFactory.create(globals, GlobalValueCache())
  internal val evaluatedTextStyle =
    _textStyleSource
      .mapLatest { source -> _textStyleSourceEvaluator.evaluate(textStyleSource = source) }
      .stateIn(viewModelScope, TextStyle())

  internal fun onTextStyleSourceUpdate(newSource: TextStyleSource.Local) {
    _textStyleSource.update { newSource }
  }

  @AssistedFactory
  @ManualViewModelAssistedFactoryKey
  @ContributesIntoMap(AppScope::class)
  interface Factory : ManualViewModelAssistedFactory {
    fun create(initialValue: TextStyleSource, globals: Globals): TextStyleSelectorViewModel
  }
}

@Composable
@Preview
private fun PreviewLocalTextStyle() = Preview2 {
  var textStyleSource by remember { mutableStateOf(TextStyleSource.Local()) }
  LocalTextStyleParameters(
    onDismiss = {},
    onConfirm = {},
    onTextStyleSourceUpdate = { textStyleSource = it },
    textStyleSource = textStyleSource,
    switchPage = {},
  )
}

@Composable
@Preview
private fun PreviewGlobalTextStyle() = Preview2 {
  GlobalTextStyle(
    onDismiss = {},
    onConfirm = {},
    initialValue = TextStyleSource.Global(1L),
    globals =
      Globals(
        textStyles =
          List(7) {
            GlobalValue.GlobalTextStyle(
              id = it.toLong(),
              label = "Text style $it",
              initialValue = TextStyleSource.Local(),
            )
          }
      ),
  )
}
