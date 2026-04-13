package io.github.sadellie.sukko.feature.editor.selector.brushsource

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import io.github.sadellie.sukko.core.model.basic.M3Color
import io.github.sadellie.sukko.core.model.basic.ScriptableColor
import io.github.sadellie.sukko.core.ui.BackHandler
import io.github.sadellie.sukko.core.ui.EmptyScreen
import io.github.sadellie.sukko.core.ui.SheetContentWithButtons
import io.github.sadellie.sukko.core.ui.listedShapes
import io.github.sadellie.sukko.feature.editor.selector.ColorSelectorSheetContent
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.common_back
import io.github.sadellie.sukko.resources.common_select
import io.github.sadellie.sukko.resources.editor_selector_brush_add_color
import io.github.sadellie.sukko.resources.editor_selector_brush_horizontal
import io.github.sadellie.sukko.resources.editor_selector_brush_vertical
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun LinearBrushEditor(
  onDismiss: () -> Unit,
  onConfirm: (BrushSource.LinearGradient) -> Unit,
  initialValue: BrushSource,
  globals: Globals,
) {
  val viewModel =
    assistedMetroViewModel<
      LinearBrushSourceSelectorViewModel,
      LinearBrushSourceSelectorViewModel.Factory,
    > {
      create(initialValue, globals)
    }
  val brushEvaluationResult =
    viewModel.linearBrushEvaluationResult.collectAsStateWithLifecycle().value
      ?: return EmptyScreen()
  val currentValue = viewModel.brushSource.collectAsStateWithLifecycle().value

  LinearBrushContent(
    onDismiss = onDismiss,
    onConfirm = { onConfirm(currentValue) },
    currentValue = currentValue,
    onBrushSourceUpdate = viewModel::onBrushSourceUpdate,
    brushEvaluationResult = brushEvaluationResult,
    globals = globals,
  )
}

@Composable
private fun LinearBrushContent(
  onDismiss: () -> Unit,
  onConfirm: () -> Unit,
  currentValue: BrushSource.LinearGradient,
  onBrushSourceUpdate: (BrushSource.LinearGradient) -> Unit,
  brushEvaluationResult: LinearBrushEvaluationResult,
  globals: Globals,
) {
  var currentPage by remember { mutableStateOf<LinearBrushPage>(LinearBrushPage.Parameters) }
  BackHandler(currentPage != LinearBrushPage.Parameters) {
    currentPage = LinearBrushPage.Parameters
  }
  AnimatedContent(targetState = currentPage, modifier = Modifier.fillMaxWidth()) { page ->
    when (page) {
      is LinearBrushPage.Parameters ->
        LinearBrushParameters(
          onDismiss = onDismiss,
          onConfirm = onConfirm,
          onBrushSourceUpdate = onBrushSourceUpdate,
          brushSource = currentValue,
          switchPage = { currentPage = it },
          linearBrushEvaluationResult = brushEvaluationResult,
        )
      is LinearBrushPage.AddColor ->
        ColorSelectorSheetContent(
          onDismissRequest = { currentPage = LinearBrushPage.Parameters },
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
      is LinearBrushPage.EditColor ->
        ColorSelectorSheetContent(
          onDismissRequest = { currentPage = LinearBrushPage.Parameters },
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
private fun LinearBrushParameters(
  onDismiss: () -> Unit,
  onConfirm: () -> Unit,
  onBrushSourceUpdate: (BrushSource.LinearGradient) -> Unit,
  brushSource: BrushSource.LinearGradient,
  switchPage: (LinearBrushPage) -> Unit,
  linearBrushEvaluationResult: LinearBrushEvaluationResult,
) {
  SheetContentWithButtons(onDismiss = onDismiss, onConfirm = onConfirm) {
    Column(
      modifier = Modifier.padding(horizontal = Sizes.large),
      verticalArrangement = Arrangement.spacedBy(Sizes.small),
    ) {
      LinearBrushParametersDirection(
        modifier = Modifier.fillMaxWidth(),
        brushSource = brushSource,
        onBrushSourceUpdate = onBrushSourceUpdate,
      )

      var selectedColorIndex by remember(brushSource.colors) { mutableStateOf(-1) }
      BrushParametersColorSlider(
        modifier = Modifier.fillMaxWidth(),
        brushSourcePreview = linearBrushEvaluationResult.evaluatedBrush,
        colors = linearBrushEvaluationResult.evaluatedColors,
        onDragStopped = { index, newStop ->
          val newColors = brushSource.colors.toMutableList()
          newColors[index] = newColors[index].copy(first = newStop)
          newColors.sortBy { it.first }
          onBrushSourceUpdate(brushSource.copy(colors = newColors))
        },
        onDragStarted = { selectedColorIndex = it },
        selectedIndex = selectedColorIndex,
      )

      OutlinedButton(
        modifier = Modifier.fillMaxWidth(),
        onClick = { switchPage(LinearBrushPage.AddColor) },
        shapes = ButtonDefaults.shapes(),
      ) {
        Text(stringResource(Res.string.editor_selector_brush_add_color))
      }

      val isRemoveEnabled = remember(brushSource.colors.size) { brushSource.colors.size > 2 }
      LazyColumn(modifier = Modifier.fillMaxWidth(), verticalArrangement = ListArrangement) {
        itemsIndexed(items = linearBrushEvaluationResult.evaluatedColors) {
          index,
          evaluatedBrushColor ->
          ColorListItem(
            modifier = Modifier,
            color = evaluatedBrushColor.color,
            scriptableColor = evaluatedBrushColor.scriptableColor,
            onClick = {
              switchPage(LinearBrushPage.EditColor(index, evaluatedBrushColor.scriptableColor))
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
private fun LinearBrushParametersDirection(
  modifier: Modifier,
  brushSource: BrushSource.LinearGradient,
  onBrushSourceUpdate: (BrushSource.LinearGradient) -> Unit,
) {
  Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
    ToggleButton(
      checked = brushSource.horizontal,
      shapes = ToggleButtonDefaults.shapes(),
      modifier = Modifier.weight(1f),
      onCheckedChange = { onBrushSourceUpdate(brushSource.copy(horizontal = true)) },
    ) {
      Text(stringResource(Res.string.editor_selector_brush_horizontal))
    }
    ToggleButton(
      checked = !brushSource.horizontal,
      shapes = ToggleButtonDefaults.shapes(),
      modifier = Modifier.weight(1f),
      onCheckedChange = { onBrushSourceUpdate(brushSource.copy(horizontal = false)) },
    ) {
      Text(stringResource(Res.string.editor_selector_brush_vertical))
    }
  }
}

@AssistedInject
class LinearBrushSourceSelectorViewModel(
  @Assisted initialBrushSource: BrushSource,
  @Assisted globals: Globals,
  scriptableEvaluatorFactory: ScriptableEvaluator.ScriptableEvaluatorFactory,
) : ViewModel() {
  private val _brushSource =
    MutableStateFlow(
      initialBrushSource as? BrushSource.LinearGradient
        ?: BrushSource.LinearGradient(
          colors =
            listOf(
              0f to ScriptableColor.FixedCustom(Color.LightGray),
              1f to ScriptableColor.FixedCustom(Color.DarkGray),
            ),
          horizontal = true,
        )
    )
  val brushSource = _brushSource.asStateFlow()
  private val _scriptableEvaluator = scriptableEvaluatorFactory.create(globals)
  private val _brushSourceEvaluator = BrushSourceEvaluator(_scriptableEvaluator)

  internal fun onBrushSourceUpdate(brushSource: BrushSource.LinearGradient) =
    _brushSource.update { brushSource }

  internal val linearBrushEvaluationResult =
    _brushSource
      .mapLatest { brushSource ->
        // preview is always horizontal
        val evaluatedBrush = _brushSourceEvaluator.evaluate(brushSource.copy(horizontal = true))
        val evaluatedBrushColors =
          brushSource.colors.map { (stop, scriptableColor) ->
            EvaluatedBrushColor(
              scriptableColor = scriptableColor,
              stop = stop,
              color = _scriptableEvaluator.evaluateColor(scriptableColor),
            )
          }

        LinearBrushEvaluationResult(evaluatedBrushColors, evaluatedBrush)
      }
      .stateIn(viewModelScope, null)

  @AssistedFactory
  @ManualViewModelAssistedFactoryKey
  @ContributesIntoMap(AppScope::class)
  fun interface Factory : ManualViewModelAssistedFactory {
    fun create(
      initialBrushSource: BrushSource,
      globals: Globals,
    ): LinearBrushSourceSelectorViewModel
  }
}

private sealed interface LinearBrushPage {
  data object Parameters : LinearBrushPage

  data object AddColor : LinearBrushPage

  data class EditColor(val index: Int, val color: ScriptableColor) : LinearBrushPage
}

internal data class LinearBrushEvaluationResult(
  val evaluatedColors: List<EvaluatedBrushColor>,
  val evaluatedBrush: Brush,
)

internal data class EvaluatedBrushColor(
  val scriptableColor: ScriptableColor,
  val stop: Float,
  val color: Color,
)

@Composable
@Preview
private fun PreviewLinearBrushSourceContent() {
  LinearBrushContent(
    onDismiss = {},
    onConfirm = {},
    currentValue =
      remember {
        BrushSource.LinearGradient(
          colors =
            listOf(
              0f to ScriptableColor.FixedCustom(Color.Red),
              1f to ScriptableColor.FixedCustom(Color.Blue),
            ),
          horizontal = true,
        )
      },
    onBrushSourceUpdate = {},
    brushEvaluationResult =
      remember {
        LinearBrushEvaluationResult(
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
          evaluatedBrush = Brush.linearGradient(0f to Color.Red, 1f to Color.Blue),
        )
      },
    globals = remember { Globals() },
  )
}
