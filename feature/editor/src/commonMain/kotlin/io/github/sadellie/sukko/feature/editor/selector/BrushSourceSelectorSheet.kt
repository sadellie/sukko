package io.github.sadellie.sukko.feature.editor.selector

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.RoundedPolygon
import co.touchlab.kermit.Logger
import com.composables.core.ModalBottomSheetState
import io.github.sadellie.sukko.core.designsystem.Preview2
import io.github.sadellie.sukko.core.designsystem.theme.ListArrangement
import io.github.sadellie.sukko.core.designsystem.theme.Sizes
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.LayerContext
import io.github.sadellie.sukko.core.model.basic.BrushSource
import io.github.sadellie.sukko.core.model.basic.LocalScriptableDisplay
import io.github.sadellie.sukko.core.model.basic.M3Color
import io.github.sadellie.sukko.core.model.basic.ScriptableColor
import io.github.sadellie.sukko.core.ui.BackHandler
import io.github.sadellie.sukko.core.ui.ListItem2
import io.github.sadellie.sukko.core.ui.ModalBottomSheet2
import io.github.sadellie.sukko.core.ui.RemoveButton
import io.github.sadellie.sukko.core.ui.SheetContentWithButtons
import io.github.sadellie.sukko.core.ui.hide
import io.github.sadellie.sukko.core.ui.listedShape
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.common_back
import io.github.sadellie.sukko.resources.common_select
import io.github.sadellie.sukko.resources.core_model_brush_linear_gradient
import io.github.sadellie.sukko.resources.core_model_brush_radial_gradient
import io.github.sadellie.sukko.resources.core_model_brush_solid
import io.github.sadellie.sukko.resources.editor_selector_brush_add_color
import io.github.sadellie.sukko.resources.editor_selector_brush_horizontal
import io.github.sadellie.sukko.resources.editor_selector_brush_vertical
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun BrushSourceSelectorSheet(
  state: ModalBottomSheetState,
  onValueSelected: (newValue: BrushSource) -> Unit,
  value: BrushSource,
  globals: Globals,
) {
  ModalBottomSheet2(state) {
    BrushSourcesSelectorSheetContent(
      onDismiss = state::hide,
      onValueSelected = onValueSelected,
      value = value,
      globals = globals,
    )
  }
}

@Composable
private fun BrushSourcesSelectorSheetContent(
  onDismiss: () -> Unit,
  onValueSelected: (newValue: BrushSource) -> Unit,
  value: BrushSource,
  globals: Globals,
) {
  val layerContext = rememberLayerContext()
  var currentInputMode by rememberSaveable { mutableStateOf(BrushInputMode.initialMode(value)) }
  SelectorSheetTemplateContent(
    currentInputMode = currentInputMode,
    inputModes = remember { BrushInputMode.entries },
    onInputModeUpdate = { currentInputMode = it },
  ) { inputMode ->
    when (inputMode) {
      BrushInputMode.SOLID ->
        SolidBrush(
          onDismiss = onDismiss,
          onConfirm = onValueSelected,
          initialValue = value,
          globals = globals,
        )
      BrushInputMode.LINEAR ->
        LinearBrush(
          onDismiss = onDismiss,
          onConfirm = onValueSelected,
          initialValue = value,
          layerContext = layerContext,
          globals = globals,
        )
      BrushInputMode.RADIAL -> RadialBrush(onDismiss = onDismiss)
    }
  }
}

@Composable
private fun SolidBrush(
  onDismiss: () -> Unit,
  onConfirm: (BrushSource.SolidColor) -> Unit,
  initialValue: BrushSource,
  globals: Globals,
) {
  ColorSelectorSheetContent(
    onDismissRequest = onDismiss,
    onValueSelected = { onConfirm(BrushSource.SolidColor(it)) },
    value =
      remember {
        if (initialValue is BrushSource.SolidColor) initialValue.color
        else ScriptableColor.FixedCustom(Color.Unspecified)
      },
    globals = globals.colors,
  )
}

@Composable
private fun LinearBrush(
  onDismiss: () -> Unit,
  onConfirm: (BrushSource.LinearGradient) -> Unit,
  initialValue: BrushSource,
  layerContext: LayerContext,
  globals: Globals,
) {
  var currentPage by remember { mutableStateOf<LinearBrushPage>(LinearBrushPage.Parameters) }
  var currentValue by remember {
    mutableStateOf(
      initialValue as? BrushSource.LinearGradient
        ?: BrushSource.LinearGradient(
          colors =
            listOf(
              0f to ScriptableColor.FixedCustom(Color.LightGray),
              1f to ScriptableColor.FixedCustom(Color.DarkGray),
            ),
          horizontal = true,
        )
    )
  }
  BackHandler(currentPage != LinearBrushPage.Parameters) {
    currentPage = LinearBrushPage.Parameters
  }
  AnimatedContent(targetState = currentPage, modifier = Modifier.fillMaxWidth()) { page ->
    when (page) {
      is LinearBrushPage.Parameters ->
        LinearBrushParameters(
          onDismiss = onDismiss,
          onConfirm = { onConfirm(currentValue) },
          onBrushSourceUpdate = { currentValue = it },
          brushSource = currentValue,
          switchPage = { currentPage = it },
          layerContext = layerContext,
          globals = globals,
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
            currentValue = currentValue.copy(colors = sortedUpdateColors)
          },
          value = remember { ScriptableColor.FixedM3(M3Color.PRIMARY) },
          globals = globals.colors,
          dismissLabel = stringResource(Res.string.common_back),
          confirmLabel = stringResource(Res.string.common_select),
        )
      is LinearBrushPage.EditColor ->
        ColorSelectorSheetContent(
          onDismissRequest = { currentPage = LinearBrushPage.Parameters },
          onValueSelected = { newColor ->
            val updatedColors = currentValue.colors.toMutableList()
            updatedColors[page.index] = updatedColors[page.index].copy(second = newColor)
            currentValue = currentValue.copy(colors = updatedColors)
          },
          value = page.color,
          globals = globals.colors,
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
  layerContext: LayerContext,
  globals: Globals,
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
      // preview is always in horizontal
      val brushSourcePreview =
        produceBrush(
          brushSource = brushSource.copy(horizontal = true),
          layerContext = layerContext,
          globals = globals,
        )
      LinearBrushParametersPreview(
        modifier = Modifier.fillMaxWidth(),
        brushSourcePreview = brushSourcePreview.value,
        colors = brushSource.colors,
        onDragStopped = { index, newStop ->
          val newColors = brushSource.colors.toMutableList()
          newColors[index] = newColors[index].copy(first = newStop)
          newColors.sortBy { it.first }
          onBrushSourceUpdate(brushSource.copy(colors = newColors))
        },
        onDragStarted = { selectedColorIndex = it },
        selectedIndex = selectedColorIndex,
        layerContext = layerContext,
        globals = globals,
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
        itemsIndexed(items = brushSource.colors) { index, (_, color) ->
          ColorListItem(
            modifier = Modifier,
            color = color,
            layerContext = layerContext,
            onClick = { switchPage(LinearBrushPage.EditColor(index, color)) },
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
            globals = globals,
            shape = ListItemDefaults.listedShape(index, brushSource.colors.size),
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

@Composable
private fun LinearBrushParametersPreview(
  modifier: Modifier,
  brushSourcePreview: Brush,
  colors: List<Pair<Float, ScriptableColor>>,
  selectedIndex: Int,
  onDragStopped: (index: Int, newStop: Float) -> Unit,
  onDragStarted: (index: Int) -> Unit,
  layerContext: LayerContext,
  globals: Globals,
) {
  val shapeWidth = 16.dp
  BoxWithConstraints(
    modifier =
      modifier
        .clip(MaterialTheme.shapes.large)
        .background(brushSourcePreview)
        .height(56.dp)
        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.large)
        // half of draggable width for correct padding
        .padding(horizontal = shapeWidth / 2)
  ) {
    val maxWidth = this.maxWidth
    val density = LocalDensity.current
    colors.forEachIndexed { index, (stop, color) ->
      var localStop by remember(stop) { mutableStateOf(stop) }
      val shapeWidthAnimated =
        animateDpAsState(if (selectedIndex == index) shapeWidth * 2 else shapeWidth)
      Box(
        modifier =
          Modifier.offset {
              val shapeWidthPx = shapeWidth.toPx()
              val scaledOffset = maxWidth.toPx() * localStop
              // subtract half of the shape to center
              IntOffset(x = (scaledOffset - (shapeWidthPx / 2)).roundToInt(), y = 0)
            }
            .draggable(
              state =
                rememberDraggableState { delta ->
                  val maxWidthPx = with(density) { maxWidth.toPx() }
                  val proportionalDelta = delta / maxWidthPx
                  localStop = (localStop + proportionalDelta).coerceIn(0f..1f)
                },
              orientation = Orientation.Horizontal,
              onDragStopped = { onDragStopped(index, localStop) },
              onDragStarted = { onDragStarted(index) },
            )
            .clip(MaterialTheme.shapes.large)
            .background(produceColor(color, layerContext, globals).value)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.large)
            .fillMaxHeight()
            .width(shapeWidthAnimated.value)
      )
    }
  }
}

@Composable
private fun ColorListItem(
  modifier: Modifier,
  color: ScriptableColor,
  layerContext: LayerContext,
  globals: Globals,
  isSelected: Boolean = false,
  onClick: () -> Unit,
  onRemove: (() -> Unit)? = null,
  onIconClick: () -> Unit = {},
  isRemoveEnabled: Boolean = true,
  polygon: RoundedPolygon = MaterialShapes.Pill,
  shape: Shape,
) {
  val selectedTransition = updateTransition(isSelected)
  val backgroundColor =
    selectedTransition.animateColor {
      if (it) MaterialTheme.colorScheme.primaryContainer
      else MaterialTheme.colorScheme.surfaceBright
    }
  val headlineColor =
    selectedTransition.animateColor {
      if (it) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
    }

  ListItem2(
    modifier = modifier.clickable(onClick = onClick),
    colors =
      ListItemDefaults.colors(
        containerColor = backgroundColor.value,
        headlineColor = headlineColor.value,
      ),
    headlineContent = { Text(LocalScriptableDisplay.current.displayString(color)) },
    leadingContent = {
      val shape = polygon.toShape()
      Box(
        Modifier.clip(shape)
          .clickable(onClick = onIconClick)
          .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape)
          .background(produceColor(color, layerContext, globals).value)
          .size(40.dp)
      )
    },
    trailingContent =
      onRemove?.let { { RemoveButton(onRemoveClick = onRemove, enabled = isRemoveEnabled) } },
    shape = shape,
  )
}

@Composable
private fun RadialBrush(onDismiss: () -> Unit) {
  SheetContentWithButtons(onDismiss = onDismiss, onConfirm = {}) { Text("Not yet implemented") }
}

private enum class BrushInputMode(override val displayName: StringResource) : InputMode {
  SOLID(displayName = Res.string.core_model_brush_solid),
  LINEAR(displayName = Res.string.core_model_brush_linear_gradient),
  RADIAL(displayName = Res.string.core_model_brush_radial_gradient);

  companion object {
    fun initialMode(brushSource: BrushSource) =
      when (brushSource) {
        is BrushSource.SolidColor -> SOLID
        is BrushSource.RadialGradient -> RADIAL
        is BrushSource.LinearGradient -> LINEAR
      }
  }
}

private sealed interface LinearBrushPage {
  data object Parameters : LinearBrushPage

  data object AddColor : LinearBrushPage

  data class EditColor(val index: Int, val color: ScriptableColor) : LinearBrushPage
}

@Composable
private fun produceBrush(brushSource: BrushSource, layerContext: LayerContext, globals: Globals) =
  produceState<Brush>(
    initialValue = SolidColor(Color.Unspecified),
    key1 = brushSource,
    key2 = layerContext,
  ) {
    value =
      try {
        brushSource.getBrush(layerContext, globals)
      } catch (e: Exception) {
        Logger.e(TAG, e) { "Failed to produce brush" }
        SolidColor(Color.Unspecified)
      }
  }

private const val TAG = "BrushSourceSelectorSheet"

@Composable
@Preview
private fun PreviewBrushSourcesSelectorSheetContent() = Preview2 {
  BrushSourcesSelectorSheetContent(
    onDismiss = {},
    onValueSelected = {},
    value =
      BrushSource.LinearGradient(
        colors =
          listOf(
            0f to ScriptableColor.FixedM3(M3Color.PRIMARY_CONTAINER),
            0.3f to ScriptableColor.FixedM3(M3Color.PRIMARY),
            1f to ScriptableColor.FixedM3(M3Color.TERTIARY_CONTAINER),
          ),
        horizontal = true,
      ),
    globals = remember { Globals() },
  )
}
