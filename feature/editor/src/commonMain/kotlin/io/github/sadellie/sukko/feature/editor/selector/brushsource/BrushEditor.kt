package io.github.sadellie.sukko.feature.editor.selector.brushsource

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
internal fun BrushParametersColorSlider(
  modifier: Modifier,
  brushSourcePreview: Brush,
  colors: List<EvaluatedBrushColor>,
  selectedIndex: Int,
  onDragStopped: (index: Int, newStop: Float) -> Unit,
  onDragStarted: (index: Int) -> Unit,
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
    colors.forEachIndexed { index, evaluatedBrushColor ->
      var localStop by
      remember(evaluatedBrushColor.stop) { mutableStateOf(evaluatedBrushColor.stop) }
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
            .background(evaluatedBrushColor.color)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.large)
            .fillMaxHeight()
            .width(shapeWidthAnimated.value)
      )
    }
  }
}
