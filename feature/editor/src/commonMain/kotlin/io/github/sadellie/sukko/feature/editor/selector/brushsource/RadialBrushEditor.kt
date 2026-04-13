package io.github.sadellie.sukko.feature.editor.selector.brushsource

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ListItemShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import io.github.sadellie.sukko.core.common.stateIn
import io.github.sadellie.sukko.core.data.BrushSourceEvaluator
import io.github.sadellie.sukko.core.data.ScriptableEvaluator
import io.github.sadellie.sukko.core.designsystem.theme.ListArrangement
import io.github.sadellie.sukko.core.designsystem.theme.Sizes
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.basic.BrushSource
import io.github.sadellie.sukko.core.model.basic.LocalScriptableDisplay
import io.github.sadellie.sukko.core.model.basic.M3Color
import io.github.sadellie.sukko.core.model.basic.ScriptableColor
import io.github.sadellie.sukko.core.model.basic.ScriptableDouble
import io.github.sadellie.sukko.core.ui.BackHandler
import io.github.sadellie.sukko.core.ui.EmptyScreen
import io.github.sadellie.sukko.core.ui.ListItem2
import io.github.sadellie.sukko.core.ui.SheetContentWithButtons
import io.github.sadellie.sukko.core.ui.listedShapes
import io.github.sadellie.sukko.core.ui.singleShapes
import io.github.sadellie.sukko.feature.editor.selector.ColorSelectorSheetContent
import io.github.sadellie.sukko.feature.editor.selector.DoubleSelectorSheetContent
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.common_back
import io.github.sadellie.sukko.resources.common_select
import io.github.sadellie.sukko.resources.editor_selector_brush_add_color
import io.github.sadellie.sukko.resources.editor_selector_brush_radius
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun RadialBrushEditor(
  onDismiss: () -> Unit,
  onConfirm: (BrushSource.RadialGradient) -> Unit,
  initialValue: BrushSource,
  globals: Globals,
) {
  val viewModel =
    assistedMetroViewModel<
      RadialBrushSourceSelectorViewModel,
      RadialBrushSourceSelectorViewModel.Factory,
    > {
      create(initialValue, globals)
    }
  val brushEvaluationResult =
    viewModel.brushEvaluationResult.collectAsStateWithLifecycle().value ?: return EmptyScreen()
  val currentValue = viewModel.brushSource.collectAsStateWithLifecycle().value

  RadialBrushContent(
    onDismiss = onDismiss,
    onConfirm = { onConfirm(currentValue) },
    currentValue = currentValue,
    onBrushSourceUpdate = viewModel::onBrushSourceUpdate,
    brushEvaluationResult = brushEvaluationResult,
    globals = globals,
  )
}

@Composable
private fun RadialBrushContent(
  onDismiss: () -> Unit,
  onConfirm: () -> Unit,
  currentValue: BrushSource.RadialGradient,
  onBrushSourceUpdate: (BrushSource.RadialGradient) -> Unit,
  brushEvaluationResult: RadialBrushEvaluationResult,
  globals: Globals,
) {
  var currentPage by remember { mutableStateOf<RadialBrushPage>(RadialBrushPage.Parameters) }
  BackHandler(currentPage != RadialBrushPage.Parameters) {
    currentPage = RadialBrushPage.Parameters
  }
  AnimatedContent(targetState = currentPage, modifier = Modifier.fillMaxWidth()) { page ->
    when (page) {
      is RadialBrushPage.Parameters ->
        RadialBrushParameters(
          onDismiss = onDismiss,
          onConfirm = onConfirm,
          onBrushSourceUpdate = onBrushSourceUpdate,
          brushSource = currentValue,
          switchPage = { currentPage = it },
          brushEvaluationResult = brushEvaluationResult,
        )
      is RadialBrushPage.EditRadius -> {
        DoubleSelectorSheetContent(
          onDismissRequest = { currentPage = RadialBrushPage.Parameters },
          onValueSelected = { newRadius ->
            if (newRadius != null) onBrushSourceUpdate(currentValue.copy(radius = newRadius))
          },
          value = currentValue.radius,
          range = remember { 0.0..1.0 },
          globals = globals,
          allowFraction = true,
          allowNullable = false,
        )
      }
      is RadialBrushPage.AddColor ->
        ColorSelectorSheetContent(
          onDismissRequest = { currentPage = RadialBrushPage.Parameters },
          onValueSelected = { newColor ->
            val (color1, color2) = currentValue.colors.takeLast(2)
            val newStop = (color1.first + color2.first) / 2
            val newColorWithStop = newStop to newColor
            val updatedColors = currentValue.colors + newColorWithStop
            val sortedUpdateColors = updatedColors.sortedBy { it.first }
            onBrushSourceUpdate(currentValue.copy(colors = sortedUpdateColors))
          },
          value = remember { ScriptableColor.FixedM3(M3Color.PRIMARY) },
          globals = globals,
          dismissLabel = stringResource(Res.string.common_back),
          confirmLabel = stringResource(Res.string.common_select),
        )
      is RadialBrushPage.EditColor ->
        ColorSelectorSheetContent(
          onDismissRequest = { currentPage = RadialBrushPage.Parameters },
          onValueSelected = { newColor ->
            val updatedColors = currentValue.colors.toMutableList()
            updatedColors[page.index] = updatedColors[page.index].copy(second = newColor)
            onBrushSourceUpdate(currentValue.copy(colors = updatedColors))
          },
          value = page.color,
          globals = globals,
          dismissLabel = stringResource(Res.string.common_back),
          confirmLabel = stringResource(Res.string.common_select),
        )
    }
  }
}

@Composable
private fun RadialBrushParameters(
  onDismiss: () -> Unit,
  onConfirm: () -> Unit,
  onBrushSourceUpdate: (BrushSource.RadialGradient) -> Unit,
  brushSource: BrushSource.RadialGradient,
  switchPage: (RadialBrushPage) -> Unit,
  brushEvaluationResult: RadialBrushEvaluationResult,
) {
  SheetContentWithButtons(onDismiss = onDismiss, onConfirm = onConfirm) {
    Column(
      modifier = Modifier.padding(horizontal = Sizes.large),
      verticalArrangement = Arrangement.spacedBy(Sizes.small),
    ) {
      var selectedColorIndex by remember(brushSource.colors) { mutableStateOf(-1) }
      RadialBrushParametersPreview(
        modifier = Modifier.fillMaxWidth(),
        brushSourcePreviewSlider = brushEvaluationResult.brushSourcePreviewSlider,
        brushSourcePreviewHero = brushEvaluationResult.brushSourcePreviewHero,
        colors = brushEvaluationResult.evaluatedColors,
        onDragStopped = { index, newStop ->
          val newColors = brushSource.colors.toMutableList()
          newColors[index] = newColors[index].copy(first = newStop)
          newColors.sortBy { it.first }
          onBrushSourceUpdate(brushSource.copy(colors = newColors))
        },
        onDragStarted = { selectedColorIndex = it },
        selectedIndex = selectedColorIndex,
      )
      RadialBrushParametersRadius(
        modifier = Modifier.fillMaxWidth(),
        radius = brushSource.radius,
        onClick = { switchPage(RadialBrushPage.EditRadius) },
        shapes = ListItemDefaults.singleShapes,
      )
      OutlinedButton(
        modifier = Modifier.fillMaxWidth(),
        onClick = { switchPage(RadialBrushPage.AddColor) },
        shapes = ButtonDefaults.shapes(),
      ) {
        Text(stringResource(Res.string.editor_selector_brush_add_color))
      }

      val isRemoveEnabled = remember(brushSource.colors.size) { brushSource.colors.size > 2 }
      LazyColumn(modifier = Modifier.fillMaxWidth(), verticalArrangement = ListArrangement) {
        itemsIndexed(items = brushEvaluationResult.evaluatedColors) { index, evaluatedBrushColor ->
          ColorListItem(
            modifier = Modifier,
            color = evaluatedBrushColor.color,
            scriptableColor = evaluatedBrushColor.scriptableColor,
            onClick = {
              switchPage(RadialBrushPage.EditColor(index, evaluatedBrushColor.scriptableColor))
            },
            onIconClick = { selectedColorIndex = index },
            isSelected = selectedColorIndex == index,
            onRemove = {
              if (brushSource.colors.size > 2) {
                val updateColors = brushSource.colors.toMutableList()
                updateColors.removeAt(index)
                onBrushSourceUpdate(brushSource.copy(colors = updateColors))
              }
            },
            isRemoveEnabled = isRemoveEnabled,
            shapes = ListItemDefaults.listedShapes(index, brushSource.colors.size),
          )
        }
      }
    }
  }
}

@Composable
private fun RadialBrushParametersRadius(
  modifier: Modifier,
  radius: ScriptableDouble,
  onClick: () -> Unit,
  shapes: ListItemShapes,
) {
  ListItem2(
    modifier = modifier,
    onClick = onClick,
    content = { Text(stringResource(Res.string.editor_selector_brush_radius)) },
    supportingContent = { Text(LocalScriptableDisplay.current.displayString(radius)) },
    shapes = shapes,
  )
}

@Composable
private fun RadialBrushParametersPreview(
  modifier: Modifier,
  brushSourcePreviewSlider: Brush,
  brushSourcePreviewHero: Brush,
  colors: List<EvaluatedBrushColor>,
  selectedIndex: Int,
  onDragStopped: (index: Int, newStop: Float) -> Unit,
  onDragStarted: (index: Int) -> Unit,
) {
  Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(Sizes.small)) {
    Box(
      modifier =
        Modifier.fillMaxWidth()
          .clip(MaterialTheme.shapes.large)
          .aspectRatio(1f)
          .background(brushSourcePreviewHero)
    )
    BrushParametersColorSlider(
      modifier = Modifier.fillMaxWidth(),
      brushSourcePreview = brushSourcePreviewSlider,
      colors = colors,
      selectedIndex = selectedIndex,
      onDragStopped = onDragStopped,
      onDragStarted = onDragStarted,
    )
  }
}

@AssistedInject
class RadialBrushSourceSelectorViewModel(
  @Assisted initialBrushSource: BrushSource,
  @Assisted globals: Globals,
  scriptableEvaluatorFactory: ScriptableEvaluator.ScriptableEvaluatorFactory,
) : ViewModel() {
  private val _brushSource =
    MutableStateFlow(
      initialBrushSource as? BrushSource.RadialGradient
        ?: BrushSource.RadialGradient(
          colors =
            listOf(
              0f to ScriptableColor.FixedCustom(Color.LightGray),
              1f to ScriptableColor.FixedCustom(Color.DarkGray),
            ),
          radius = ScriptableDouble.Fixed(Float.POSITIVE_INFINITY.toDouble()),
        )
    )
  val brushSource = _brushSource.asStateFlow()
  private val _scriptableEvaluator = scriptableEvaluatorFactory.create(globals)
  private val _brushSourceEvaluator = BrushSourceEvaluator(_scriptableEvaluator)

  internal fun onBrushSourceUpdate(brushSource: BrushSource.RadialGradient) =
    _brushSource.update { brushSource }

  internal val brushEvaluationResult =
    _brushSource
      .mapLatest { brushSource ->
        val brushSourcePreviewSlider =
          _brushSourceEvaluator.evaluate(
            BrushSource.LinearGradient(brushSource.colors, horizontal = true)
          )
        val brushSourcePreviewHero =
          _brushSourceEvaluator.evaluate(
            BrushSource.LinearGradient(brushSource.colors, horizontal = true)
          )
        val evaluatedBrushColors =
          brushSource.colors.map { (stop, scriptableColor) ->
            EvaluatedBrushColor(
              scriptableColor = scriptableColor,
              stop = stop,
              color = _scriptableEvaluator.evaluateColor(scriptableColor),
            )
          }

        RadialBrushEvaluationResult(
          evaluatedColors = evaluatedBrushColors,
          brushSourcePreviewSlider = brushSourcePreviewSlider,
          brushSourcePreviewHero = brushSourcePreviewHero,
        )
      }
      .stateIn(viewModelScope, null)

  @AssistedFactory
  @ManualViewModelAssistedFactoryKey
  @ContributesIntoMap(AppScope::class)
  fun interface Factory : ManualViewModelAssistedFactory {
    fun create(
      initialBrushSource: BrushSource,
      globals: Globals,
    ): RadialBrushSourceSelectorViewModel
  }
}

private sealed interface RadialBrushPage {
  data object Parameters : RadialBrushPage

  data object AddColor : RadialBrushPage

  data class EditColor(val index: Int, val color: ScriptableColor) : RadialBrushPage

  data object EditRadius : RadialBrushPage
}

internal data class RadialBrushEvaluationResult(
  val evaluatedColors: List<EvaluatedBrushColor>,
  val brushSourcePreviewHero: Brush,
  val brushSourcePreviewSlider: Brush,
)

@Composable
@Preview
private fun PreviewRadialBrushSourceContent() {
  RadialBrushContent(
    onDismiss = {},
    onConfirm = {},
    currentValue =
      remember {
        BrushSource.RadialGradient(
          colors =
            listOf(
              0f to ScriptableColor.FixedCustom(Color.Red),
              1f to ScriptableColor.FixedCustom(Color.Gray),
            ),
          radius = ScriptableDouble.Fixed(Float.POSITIVE_INFINITY.toDouble()),
        )
      },
    onBrushSourceUpdate = {},
    brushEvaluationResult =
      remember {
        RadialBrushEvaluationResult(
          evaluatedColors =
            listOf(
              EvaluatedBrushColor(
                scriptableColor = ScriptableColor.FixedCustom(Color.Red),
                stop = 0f,
                color = Color.Red,
              ),
              EvaluatedBrushColor(
                scriptableColor = ScriptableColor.FixedCustom(Color.Blue),
                stop = 1f,
                color = Color.Blue,
              ),
            ),
          brushSourcePreviewSlider = Brush.linearGradient(0f to Color.Red, 1f to Color.Blue),
          brushSourcePreviewHero = Brush.radialGradient(0f to Color.Red, 1f to Color.Blue),
        )
      },
    globals = remember { Globals() },
  )
}
