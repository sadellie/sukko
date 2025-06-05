package io.github.sadellie.sukko.core.model.layer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import io.github.sadellie.sukko.core.designsystem.Preview2
import io.github.sadellie.sukko.core.model.modifier.EvaluatedBackgroundColorModifier
import io.github.sadellie.sukko.core.model.modifier.EvaluatedSizeModifier
import org.jetbrains.compose.ui.tooling.preview.Preview

/** Render entrance. Call once at the top. */
@Composable
fun Renderer(
  modifier: Modifier,
  renderOption: RenderOption?,
  layers: List<Layer.Evaluated>,
  onGloballyPositioned: (Int, Rect) -> Unit = { _, _ -> },
) {
  Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    NestedRenderer(layers, renderOption, null, onGloballyPositioned, this)
  }
}

@Composable
internal fun NestedRenderer(
  layers: List<Layer.Evaluated>,
  renderOption: RenderOption?,
  parentId: Int?,
  onGloballyPositioned: (Int, Rect) -> Unit,
  scope: Any,
) {
  val layersToRender = remember(layers, parentId) { layers.filter { it.parentId == parentId } }
  layersToRender.forEach { layer ->
    layer.Render(Modifier, renderOption, layers, onGloballyPositioned, scope)
  }
}

@Composable
@Preview
private fun PreviewRenderer() = Preview2 {
  val fakeWidgetLayers = remember {
    listOf(
      EvaluatedColumnLayer(
        id = 0,
        parentId = null,
        name = "Group",
        widgetModifiers =
          listOf(
            EvaluatedBackgroundColorModifier(
              id = 0,
              color = SolidColor(Color.White),
              shape = RectangleShape,
            )
          ),
        clickActions = emptyList(),
        arrangement = Arrangement.Top,
        alignment = Alignment.Start,
      ),
      EvaluatedTextLayer(
        id = 1,
        parentId = 0,
        name = "Text 1",
        widgetModifiers =
          listOf(
            EvaluatedBackgroundColorModifier(
              id = 0,
              color = SolidColor(Color.Blue),
              shape = RectangleShape,
            )
          ),
        clickActions = emptyList(),
        textStyle = TextStyle(),
        text = "Basic text layer 1",
        textColor = SolidColor(Color.White),
      ),
      EvaluatedTextLayer(
        id = 2,
        parentId = 0,
        name = "Text 2",
        widgetModifiers = emptyList(),
        clickActions = emptyList(),
        textStyle = TextStyle(),
        text = "Basic text layer 22",
        textColor = SolidColor(Color.Black),
      ),
      EvaluatedTextLayer(
        id = 2,
        parentId = 0,
        name = "Text 2",
        widgetModifiers = emptyList(),
        clickActions = emptyList(),
        textStyle = TextStyle(),
        text = "Basic text layer 22",
        textColor = SolidColor(Color.Black),
      ),
      EvaluatedImageLayer(
        id = 3,
        parentId = 0,
        name = null,
        widgetModifiers = listOf(EvaluatedSizeModifier(0, 46.dp)),
        clickActions = emptyList(),
        imageUri = "",
        tint = null,
        contentScale = ContentScale.Fit,
      ),
      EvaluatedProgressBarLayer(
        id = 4,
        parentId = 0,
        name = null,
        widgetModifiers = emptyList(),
        clickActions = emptyList(),
        progress = 0.5f,
        progressBarType = ProgressBarType.LINEAR,
        color = Color.Blue,
        trackColor = Color.Red,
        gapSize = 4.dp,
        amplitude = 1f,
        waveLength = 15.dp,
      ),
    )
  }

  Renderer(
    modifier = Modifier.background(Color.Gray).size(320.dp, 240.dp),
    renderOption = null,
    layers = fakeWidgetLayers,
  )
}
