package io.github.sadellie.sukko.feature.editor.selector

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.InputChip
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.composables.core.ModalBottomSheetState
import io.github.sadellie.sukko.core.designsystem.Preview2
import io.github.sadellie.sukko.core.designsystem.theme.ListArrangement
import io.github.sadellie.sukko.core.designsystem.theme.Sizes
import io.github.sadellie.sukko.core.model.basic.ShapeSource
import io.github.sadellie.sukko.core.ui.InputTransformationDp
import io.github.sadellie.sukko.core.ui.ListItem2
import io.github.sadellie.sukko.core.ui.ModalBottomSheetWithButtons
import io.github.sadellie.sukko.core.ui.SukkoOutlinedTextField
import io.github.sadellie.sukko.core.ui.SwitchWithCheckIcon
import io.github.sadellie.sukko.core.ui.firstShape
import io.github.sadellie.sukko.core.ui.lastShape
import io.github.sadellie.sukko.core.ui.middleShape
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.editor_selector_shape_corner_radius
import io.github.sadellie.sukko.resources.editor_selector_shape_inner_radius
import io.github.sadellie.sukko.resources.editor_selector_shape_rounded_corners
import io.github.sadellie.sukko.resources.editor_selector_shape_vertices_per_radius
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ShapeSelectorSheet(
  state: ModalBottomSheetState,
  onValueSelected: (newValue: ShapeSource) -> Unit,
  value: ShapeSource,
) {
  var isConfirmButtonEnabled by remember { mutableStateOf(true) }
  var currentShapeSource by remember(value) { mutableStateOf(value) }
  ModalBottomSheetWithButtons(
    state = state,
    onConfirm = { onValueSelected(currentShapeSource) },
    isConfirmButtonEnabled = isConfirmButtonEnabled,
  ) {
    ShapeSelectorSheetContent(
      onShapeSourceChange = { currentShapeSource = it },
      onConfirmButtonChange = { isConfirmButtonEnabled = it },
      shapeSource = currentShapeSource,
    )
  }
}

@Composable
private fun ShapeSelectorSheetContent(
  onShapeSourceChange: (ShapeSource) -> Unit,
  onConfirmButtonChange: (Boolean) -> Unit,
  shapeSource: ShapeSource,
) {
  Column(
    modifier = Modifier.verticalScroll(rememberScrollState()).padding(horizontal = Sizes.large),
    verticalArrangement = Arrangement.spacedBy(Sizes.large),
  ) {
    ShapePreview(
      modifier =
        Modifier.fillMaxWidth()
          .clip(MaterialTheme.shapes.large)
          .background(MaterialTheme.colorScheme.surfaceBright)
          .padding(Sizes.large),
      shapeSource = shapeSource,
    )

    ShapeSelector(
      modifier = Modifier,
      onShapeSourceUpdate = {
        onShapeSourceChange(it)
        // always switches to a shape with valid parameters
        onConfirmButtonChange(true)
      },
      shapeSource = shapeSource,
    )

    AnimatedContent(targetState = shapeSource, contentKey = { it::class }) { rectangle ->
      when (rectangle) {
        is ShapeSource.Star ->
          StarEditor(
            modifier = Modifier,
            onShapeSourceUpdate = { onShapeSourceChange(it) },
            shapeSource = rectangle,
          )
        is ShapeSource.CutCornersDp ->
          CutCornersDpEditor(
            modifier = Modifier,
            onShapeSourceUpdate = {
              onShapeSourceChange(it)
              // updates only when parameters are valid
              onConfirmButtonChange(true)
            },
            onDisableConfirmButton = { onConfirmButtonChange(false) },
            shapeSource = rectangle,
          )
        is ShapeSource.CutCornersPercent ->
          CutCornersPercentEditor(
            modifier = Modifier,
            onShapeSourceUpdate = { onShapeSourceChange(it) },
            shapeSource = rectangle,
          )
        else -> Unit
      }
    }
  }
}

@Composable
private fun ShapePreview(modifier: Modifier, shapeSource: ShapeSource) {
  Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(Sizes.small)) {
    AnimatedContent(
      targetState = shapeSource,
      modifier = Modifier.align(Alignment.CenterHorizontally),
      contentKey = { it::class },
    ) { state ->
      Box(
        Modifier.clip(remember(state) { state.getShape() })
          .size(128.dp)
          .aspectRatio(1f)
          .background(MaterialTheme.colorScheme.primary)
      )
    }
  }
}

@Composable
private fun ShapeSelector(
  modifier: Modifier,
  onShapeSourceUpdate: (ShapeSource) -> Unit,
  shapeSource: ShapeSource,
) {
  val shapes = remember { ShapeSource.allShapes() }
  val listState = rememberLazyListState(shapes.indexOf(shapeSource).coerceAtLeast(0))
  LazyRow(
    state = listState,
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(Sizes.small),
  ) {
    items(shapes) {
      InputChip(
        selected = shapeSource::class == it::class,
        onClick = { onShapeSourceUpdate(it) },
        label = { Text(stringResource(it.displayName)) },
      )
    }
  }
}

@Composable
private fun CutCornersDpEditor(
  modifier: Modifier,
  onShapeSourceUpdate: (ShapeSource.CutCornersDp) -> Unit,
  onDisableConfirmButton: () -> Unit,
  shapeSource: ShapeSource.CutCornersDp,
) {
  Column(modifier = modifier, verticalArrangement = ListArrangement) {
    ListItem2(
      modifier =
        Modifier.clickable {
          onShapeSourceUpdate(shapeSource.copy(isRounded = !shapeSource.isRounded))
        },
      headlineContent = { Text(stringResource(Res.string.editor_selector_shape_rounded_corners)) },
      trailingContent = {
        SwitchWithCheckIcon(checked = shapeSource.isRounded, onCheckedChange = null)
      },
      shape = ListItemDefaults.firstShape,
    )
    val inputTransformation = InputTransformationDp(0.dp..1_000.dp)
    val textFieldState = rememberTextFieldState(shapeSource.size.value.toString())
    LaunchedEffect(textFieldState.text) {
      val cornerRadius = inputTransformation.toValue(textFieldState.text)
      if (cornerRadius == null) {
        onDisableConfirmButton()
      } else {
        onShapeSourceUpdate(shapeSource.copy(size = cornerRadius))
      }
    }
    SukkoOutlinedTextField(
      modifier =
        Modifier.clip(ListItemDefaults.lastShape)
          .background(MaterialTheme.colorScheme.surfaceBright)
          .fillMaxWidth()
          .padding(Sizes.large),
      state = textFieldState,
      label = { Text(stringResource(Res.string.editor_selector_shape_corner_radius)) },
      suffix = { Text("dp") },
      lineLimits = TextFieldLineLimits.SingleLine,
      inputTransformation = inputTransformation,
    )
  }
}

@Composable
private fun CutCornersPercentEditor(
  modifier: Modifier,
  onShapeSourceUpdate: (ShapeSource.CutCornersPercent) -> Unit,
  shapeSource: ShapeSource.CutCornersPercent,
) {
  Column(modifier = modifier, verticalArrangement = ListArrangement) {
    ListItem2(
      modifier =
        Modifier.clickable {
          onShapeSourceUpdate(shapeSource.copy(isRounded = !shapeSource.isRounded))
        },
      headlineContent = { Text(stringResource(Res.string.editor_selector_shape_rounded_corners)) },
      trailingContent = {
        SwitchWithCheckIcon(checked = shapeSource.isRounded, onCheckedChange = null)
      },
      shape = ListItemDefaults.firstShape,
    )
    SliderListItem(
      modifier = Modifier.fillMaxWidth().padding(Sizes.large),
      label = stringResource(Res.string.editor_selector_shape_corner_radius),
      value = shapeSource.percent.toFloat(),
      onValueChange = { onShapeSourceUpdate(shapeSource.copy(percent = it.roundToInt())) },
      steps = 51,
      shape = ListItemDefaults.lastShape,
      valueRange = 0f..50f,
    )
  }
}

@Composable
private fun StarEditor(
  modifier: Modifier,
  onShapeSourceUpdate: (ShapeSource.Star) -> Unit,
  shapeSource: ShapeSource.Star,
) {
  Column(modifier = modifier, verticalArrangement = ListArrangement) {
    SliderListItem(
      modifier = Modifier.fillMaxWidth().padding(Sizes.large),
      label = stringResource(Res.string.editor_selector_shape_vertices_per_radius),
      value = shapeSource.numVerticesPerRadius.toFloat(),
      onValueChange = {
        onShapeSourceUpdate(shapeSource.copy(numVerticesPerRadius = it.roundToInt()))
      },
      steps = 10,
      shape = ListItemDefaults.firstShape,
      valueRange = 3f..12f,
    )
    SliderListItem(
      modifier = Modifier.fillMaxWidth().padding(Sizes.large),
      label = stringResource(Res.string.editor_selector_shape_inner_radius),
      value = shapeSource.innerRadius,
      onValueChange = { onShapeSourceUpdate(shapeSource.copy(innerRadius = it)) },
      shape = ListItemDefaults.middleShape,
      valueRange = 0.1f..0.9f,
    )
    SliderListItem(
      modifier = Modifier.fillMaxWidth().padding(Sizes.large),
      label = stringResource(Res.string.editor_selector_shape_corner_radius),
      value = shapeSource.cornerRounding,
      onValueChange = { onShapeSourceUpdate(shapeSource.copy(cornerRounding = it)) },
      shape = ListItemDefaults.lastShape,
      valueRange = 0f..0.9f,
    )
  }
}

@Composable
private fun BoxHeader(text: String, modifier: Modifier = Modifier) {
  Text(modifier = modifier, text = text, style = MaterialTheme.typography.labelMedium)
}

@Composable
private fun SliderListItem(
  modifier: Modifier,
  label: String,
  value: Float,
  steps: Int = 0,
  onValueChange: (Float) -> Unit,
  shape: Shape,
  valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
) {
  val hapticFeedback = LocalHapticFeedback.current
  Column(
    modifier =
      Modifier.clip(shape).background(MaterialTheme.colorScheme.surfaceBright).then(modifier),
    verticalArrangement = Arrangement.spacedBy(Sizes.small),
  ) {
    BoxHeader(label)
    Slider(
      modifier = Modifier,
      value = value,
      steps = steps,
      valueRange = valueRange,
      onValueChange = {
        hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
        onValueChange(it)
      },
    )
  }
}

@Composable
@Preview
private fun PreviewShapeSelectorSheetContentCutCornersDp() = Preview2 {
  ShapeSelectorSheetContent(
    onShapeSourceChange = {},
    onConfirmButtonChange = {},
    shapeSource = ShapeSource.CutCornersDp(),
  )
}

@Composable
@Preview
private fun PreviewShapeSelectorSheetContentCutCornersPercent() = Preview2 {
  ShapeSelectorSheetContent(
    onShapeSourceChange = {},
    onConfirmButtonChange = {},
    shapeSource = ShapeSource.CutCornersPercent(),
  )
}

@Composable
@Preview
private fun PreviewShapeSelectorSheetContentStar() = Preview2 {
  ShapeSelectorSheetContent(
    onShapeSourceChange = {},
    onConfirmButtonChange = {},
    shapeSource = ShapeSource.Star(),
  )
}
