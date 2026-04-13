package io.github.sadellie.sukko.feature.editor

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import io.github.pingpongboss.explodedlayers.ExplodedLayersRoot
import io.github.pingpongboss.explodedlayers.ExplodedLayersState
import io.github.pingpongboss.explodedlayers.rememberExplodedLayersState
import io.github.sadellie.sukko.core.model.layer.EvaluatedColumnLayer
import io.github.sadellie.sukko.core.model.layer.EvaluatedTextLayer
import io.github.sadellie.sukko.core.model.layer.Layer
import io.github.sadellie.sukko.core.model.layer.RenderOption
import io.github.sadellie.sukko.core.model.layer.Renderer
import io.github.sadellie.sukko.core.model.modifier.EvaluatedBackgroundColorModifier
import io.github.sadellie.sukko.core.model.modifier.EvaluatedFillMaxSizeModifier
import kotlin.math.min

@Composable
fun EditorCanvas(
  modifier: Modifier,
  layers: List<Layer.Evaluated>,
  canvasSize: DpSize,
  graphicsLayer: GraphicsLayer,
  renderOption: RenderOption.Editor,
  explodedLayersState: ExplodedLayersState,
) {
  BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
    // scale to make widget fill bounds
    val scaleFactor =
      remember(canvasSize) {
        if (canvasSize.width <= 0.dp) return@remember 0f
        if (canvasSize.height <= 0.dp) return@remember 0f
        val widthFactor = this.maxWidth / canvasSize.width
        val heightFactor = this.maxHeight / canvasSize.height

        min(widthFactor, heightFactor)
      }

    ExplodedLayersRoot(explodedLayersState) {
      Renderer(
        modifier =
          Modifier.graphicsLayer {
              scaleX = scaleFactor
              scaleY = scaleFactor
            }
            .drawWithContent {
              graphicsLayer.record { this@drawWithContent.drawContent() }
              drawLayer(graphicsLayer)
            }
            .requiredSize(canvasSize),
        renderOption = renderOption,
        layers = layers,
      )
    }
  }
}

@Preview
@Composable
private fun PreviewEditorCanvas() {
  EditorCanvas(
    modifier = Modifier.size(360.dp, 240.dp),
    layers =
      listOf(
        EvaluatedColumnLayer(
          id = 0,
          widgetModifiers =
            listOf(
              EvaluatedBackgroundColorModifier(
                id = 0,
                color = SolidColor(MaterialTheme.colorScheme.primaryContainer),
                shape = RectangleShape,
              ),
              EvaluatedFillMaxSizeModifier(id = 2, fraction = 1f),
            ),
        ),
        EvaluatedTextLayer(
          id = 1,
          parentId = 0,
          text = "Text",
          textColor = SolidColor(MaterialTheme.colorScheme.onPrimaryContainer),
        ),
      ),
    canvasSize = DpSize(240.dp, 240.dp),
    graphicsLayer = rememberGraphicsLayer(),
    renderOption = RenderOption.Editor(selectedLayerId = 1, highlightSelectedLayer = false),
    explodedLayersState = rememberExplodedLayersState(initialSpread = 0f, interactive = false),
  )
}
