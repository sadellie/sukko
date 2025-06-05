package io.github.sadellie.sukko.core.model

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.sadellie.sukko.core.model.basic.ScriptableBoolean
import io.github.sadellie.sukko.core.model.layer.ColdBoxLayer
import io.github.sadellie.sukko.core.model.layer.ColdColumnLayer
import io.github.sadellie.sukko.core.model.layer.ColdImageLayer
import io.github.sadellie.sukko.core.model.layer.ColdProgressBarLayer
import io.github.sadellie.sukko.core.model.layer.ColdRowLayer
import io.github.sadellie.sukko.core.model.layer.ColdTextLayer
import io.github.sadellie.sukko.core.model.layer.EvaluatedBoxLayer
import io.github.sadellie.sukko.core.model.layer.EvaluatedColumnLayer
import io.github.sadellie.sukko.core.model.layer.EvaluatedImageLayer
import io.github.sadellie.sukko.core.model.layer.EvaluatedProgressBarLayer
import io.github.sadellie.sukko.core.model.layer.EvaluatedRowLayer
import io.github.sadellie.sukko.core.model.layer.EvaluatedTextLayer
import io.github.sadellie.sukko.core.model.layer.Layer
import io.github.sadellie.sukko.core.model.layer.ProgressBarType
import io.github.sadellie.sukko.core.model.modifier.EvaluatedSizeModifier
import kotlin.test.Test
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.test.runTest

class EvaluateEnabledOnlyTest {
  @OptIn(FlowPreview::class)
  @Test
  fun evaluateEnabled_allEnabled() = runTest {
    val expectedLayers =
      listOf(
        boxLayer().copy(id = 0, parentId = null),
        textLayer().copy(id = 1, parentId = 0),
        columnLayer().copy(id = 2, parentId = null),
        textLayer().copy(id = 3, parentId = 2),
        rowLayer().copy(id = 4, parentId = null),
        textLayer().copy(id = 5, parentId = 4),
        textLayer().copy(id = 6, parentId = null),
        imageLayer().copy(id = 7, parentId = null),
        progressBarLayer().copy(id = 8, parentId = null),
      )
    val coldLayers =
      listOf(
        // box with text inside
        ColdBoxLayer(id = 0, parentId = null),
        ColdTextLayer(id = 1, parentId = 0),

        // column with text inside
        ColdColumnLayer(id = 2, parentId = null),
        ColdTextLayer(id = 3, parentId = 2),

        // row with text inside
        ColdRowLayer(id = 4, parentId = null),
        ColdTextLayer(id = 5, parentId = 4),

        // atomic layers
        ColdTextLayer(id = 6, parentId = null),
        ColdImageLayer(id = 7, parentId = null),
        ColdProgressBarLayer(id = 8, parentId = null),
      )
    observeEvaluation(coldLayers, expectedLayers)
  }

  @OptIn(FlowPreview::class)
  @Test
  fun evaluateEnabled_allDisabled() = runTest {
    val falseValue = ScriptableBoolean.Fixed(false)
    val expectedLayers = emptyList<Layer.Evaluated>()
    val coldLayers =
      listOf(
        // box with text inside
        ColdBoxLayer(id = 0, parentId = null, isEnabled = falseValue),
        ColdTextLayer(id = 1, parentId = 0, isEnabled = falseValue),

        // column with text inside
        ColdColumnLayer(id = 2, parentId = null, isEnabled = falseValue),
        ColdTextLayer(id = 3, parentId = 2, isEnabled = falseValue),

        // row with text inside
        ColdRowLayer(id = 4, parentId = null, isEnabled = falseValue),
        ColdTextLayer(id = 5, parentId = 4, isEnabled = falseValue),

        // atomic layers
        ColdTextLayer(id = 6, parentId = null, isEnabled = falseValue),
        ColdImageLayer(id = 7, parentId = null, isEnabled = falseValue),
        ColdProgressBarLayer(id = 8, parentId = null, isEnabled = falseValue),
      )
    observeEvaluation(coldLayers, expectedLayers)
  }

  @OptIn(FlowPreview::class)
  @Test
  fun evaluateEnabled_parentsDisabled() = runTest {
    val expectedLayers = emptyList<Layer.Evaluated>()
    val falseValue = ScriptableBoolean.Fixed(false)
    val coldLayers =
      listOf(
        // box with text inside
        ColdBoxLayer(id = 0, parentId = null, isEnabled = falseValue),
        ColdTextLayer(id = 1, parentId = 0),

        // column with text inside
        ColdColumnLayer(id = 2, parentId = null, isEnabled = falseValue),
        ColdTextLayer(id = 3, parentId = 2),

        // row with text inside
        ColdRowLayer(id = 4, parentId = null, isEnabled = falseValue),
        ColdTextLayer(id = 5, parentId = 4),

        // atomic layers
        ColdTextLayer(id = 6, parentId = null, isEnabled = falseValue),
        ColdImageLayer(id = 7, parentId = null, isEnabled = falseValue),
        ColdProgressBarLayer(id = 8, parentId = null, isEnabled = falseValue),
      )
    observeEvaluation(coldLayers, expectedLayers)
  }

  @OptIn(FlowPreview::class)
  @Test
  fun evaluateEnabled_atomicDisabled() = runTest {
    val falseValue = ScriptableBoolean.Fixed(false)
    val expectedLayers =
      listOf(
        boxLayer().copy(id = 0, parentId = null),
        columnLayer().copy(id = 2, parentId = null),
        rowLayer().copy(id = 4, parentId = null),
      )
    val coldLayers =
      listOf(
        // box with text inside
        ColdBoxLayer(id = 0, parentId = null),
        ColdTextLayer(id = 1, parentId = 0, isEnabled = falseValue),

        // column with text inside
        ColdColumnLayer(id = 2, parentId = null),
        ColdTextLayer(id = 3, parentId = 2, isEnabled = falseValue),

        // row with text inside
        ColdRowLayer(id = 4, parentId = null),
        ColdTextLayer(id = 5, parentId = 4, isEnabled = falseValue),

        // atomic layers
        ColdTextLayer(id = 6, parentId = null, isEnabled = falseValue),
        ColdImageLayer(id = 7, parentId = null, isEnabled = falseValue),
        ColdProgressBarLayer(id = 8, parentId = null, isEnabled = falseValue),
      )
    observeEvaluation(coldLayers, expectedLayers)
  }

  private fun boxLayer() =
    EvaluatedBoxLayer(
      id = 0,
      parentId = null,
      name = null,
      widgetModifiers = emptyList(),
      clickActions = emptyList(),
      alignment = Alignment.TopStart,
    )

  private fun textLayer() =
    EvaluatedTextLayer(
      id = 0,
      parentId = null,
      name = null,
      widgetModifiers = emptyList(),
      clickActions = emptyList(),
      textStyle =
        TextStyle(
          fontSize = 16.sp,
          fontWeight = FontWeight(400),
          fontFamily = FontFamily.Default,
          textAlign = TextAlign.Start,
        ),
      textColor = SolidColor(Color.Unspecified),
      text = "Fixed text",
    )

  private fun columnLayer() =
    EvaluatedColumnLayer(
      id = 0,
      parentId = null,
      name = null,
      widgetModifiers = emptyList(),
      clickActions = emptyList(),
      arrangement = Arrangement.Top,
      alignment = Alignment.Start,
    )

  private fun rowLayer() =
    EvaluatedRowLayer(
      id = 0,
      parentId = null,
      name = null,
      widgetModifiers = emptyList(),
      clickActions = emptyList(),
      arrangement = Arrangement.Start,
      alignment = Alignment.Top,
    )

  private fun imageLayer() =
    EvaluatedImageLayer(
      id = 0,
      parentId = null,
      name = null,
      widgetModifiers = listOf(EvaluatedSizeModifier(0, 146.dp)),
      clickActions = emptyList(),
      imageUri = null,
      contentScale = ContentScale.Fit,
      tint = null,
    )

  private fun progressBarLayer() =
    EvaluatedProgressBarLayer(
      id = 0,
      parentId = null,
      name = null,
      widgetModifiers = emptyList(),
      clickActions = emptyList(),
      progress = 0.5f,
      progressBarType = ProgressBarType.LINEAR,
      color = Color.Unspecified,
      trackColor = Color.Unspecified,
      gapSize = 4.dp,
      amplitude = 1f,
      waveLength = 15.dp,
    )
}
