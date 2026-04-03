package io.github.sadellie.sukko.core.data

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.sadellie.sukko.core.model.basic.AlignmentSource
import io.github.sadellie.sukko.core.model.basic.ArrangementSource
import io.github.sadellie.sukko.core.model.basic.BrushSource
import io.github.sadellie.sukko.core.model.basic.ClickAction
import io.github.sadellie.sukko.core.model.basic.ContentScaleSource
import io.github.sadellie.sukko.core.model.basic.ImageUriSource
import io.github.sadellie.sukko.core.model.basic.ScriptableBoolean
import io.github.sadellie.sukko.core.model.basic.ScriptableColor
import io.github.sadellie.sukko.core.model.basic.ScriptableDouble
import io.github.sadellie.sukko.core.model.basic.ScriptableDp
import io.github.sadellie.sukko.core.model.basic.ScriptableString
import io.github.sadellie.sukko.core.model.basic.ShapeSource
import io.github.sadellie.sukko.core.model.basic.TextOverflowSource
import io.github.sadellie.sukko.core.model.basic.TextStyleSource
import io.github.sadellie.sukko.core.model.layer.ColdBoxLayer
import io.github.sadellie.sukko.core.model.layer.ColdColumnLayer
import io.github.sadellie.sukko.core.model.layer.ColdImageLayer
import io.github.sadellie.sukko.core.model.layer.ColdProgressBarLayer
import io.github.sadellie.sukko.core.model.layer.ColdRowLayer
import io.github.sadellie.sukko.core.model.layer.ColdStepIndicatorLayer
import io.github.sadellie.sukko.core.model.layer.ColdTextLayer
import io.github.sadellie.sukko.core.model.layer.EvaluatedBoxLayer
import io.github.sadellie.sukko.core.model.layer.EvaluatedColumnLayer
import io.github.sadellie.sukko.core.model.layer.EvaluatedImageLayer
import io.github.sadellie.sukko.core.model.layer.EvaluatedProgressBarLayer
import io.github.sadellie.sukko.core.model.layer.EvaluatedRowLayer
import io.github.sadellie.sukko.core.model.layer.EvaluatedStepIndicatorLayer
import io.github.sadellie.sukko.core.model.layer.EvaluatedTextLayer
import io.github.sadellie.sukko.core.model.layer.Layer
import io.github.sadellie.sukko.core.model.layer.ProgressBarType
import io.github.sadellie.sukko.core.model.modifier.ColdWidthModifier
import io.github.sadellie.sukko.core.model.modifier.EvaluatedWidthModifier
import io.github.sadellie.sukko.core.model.observeEvaluation
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class LayerEvaluatorTest {
  @Test
  fun `evaluate single ColdBoxLayer`() = runTest {
    val coldLayers =
      listOf(
        ColdBoxLayer(
          id = 0,
          parentId = null,
          name = "layer 1",
          widgetModifiers = listOf(ColdWidthModifier(id = 0, width = ScriptableDp.Fixed(64.dp))),
          clickActions = listOf<ClickAction.Cold>(ClickAction.MediaPause(0)),
          isEnabled = ScriptableBoolean.Fixed(true),
          alignmentSource = AlignmentSource.Center,
        )
      )
    val expected =
      listOf<Layer.Evaluated>(
        EvaluatedBoxLayer(
          id = 0,
          parentId = null,
          name = "layer 1",
          widgetModifiers = listOf(EvaluatedWidthModifier(id = 0, width = 64.dp)),
          clickActions = listOf(ClickAction.MediaPause(0)),
          alignment = Alignment.Center,
        )
      )

    observeEvaluation(coldLayers, expected)
  }

  @Test
  fun `evaluate single ColdColumnLayer`() = runTest {
    val coldLayers =
      listOf(
        ColdColumnLayer(
          id = 0,
          parentId = null,
          name = "column layer",
          widgetModifiers = listOf(ColdWidthModifier(id = 0, width = ScriptableDp.Fixed(80.dp))),
          clickActions = listOf(ClickAction.MediaPlay(0)),
          isEnabled = ScriptableBoolean.Fixed(true),
          arrangementSource = ArrangementSource.Bottom,
          alignmentSource = AlignmentSource.End,
        )
      )
    val expected =
      listOf<Layer.Evaluated>(
        EvaluatedColumnLayer(
          id = 0,
          parentId = null,
          name = "column layer",
          widgetModifiers = listOf(EvaluatedWidthModifier(id = 0, width = 80.dp)),
          clickActions = listOf(ClickAction.MediaPlay(0)),
          arrangement = Arrangement.Bottom,
          alignment = Alignment.End,
        )
      )

    observeEvaluation(coldLayers, expected)
  }

  @Test
  fun `evaluate single ColdRowLayer`() = runTest {
    val coldLayers =
      listOf(
        ColdRowLayer(
          id = 0,
          parentId = null,
          name = "row layer",
          widgetModifiers = listOf(ColdWidthModifier(id = 0, width = ScriptableDp.Fixed(90.dp))),
          clickActions = listOf(ClickAction.MediaSkipToNext(0)),
          isEnabled = ScriptableBoolean.Fixed(true),
          arrangementSource = ArrangementSource.End,
          alignmentSource = AlignmentSource.Bottom,
        )
      )
    val expected =
      listOf<Layer.Evaluated>(
        EvaluatedRowLayer(
          id = 0,
          parentId = null,
          name = "row layer",
          widgetModifiers = listOf(EvaluatedWidthModifier(id = 0, width = 90.dp)),
          clickActions = listOf(ClickAction.MediaSkipToNext(0)),
          arrangement = Arrangement.End,
          alignment = Alignment.Bottom,
        )
      )

    observeEvaluation(coldLayers, expected)
  }

  @Test
  fun `evaluate single ColdTextLayer`() = runTest {
    val coldLayers =
      listOf(
        ColdTextLayer(
          id = 0,
          parentId = null,
          name = "text layer",
          widgetModifiers = listOf(ColdWidthModifier(id = 0, width = ScriptableDp.Fixed(100.dp))),
          clickActions = listOf(ClickAction.MediaSkipToPrevious(0)),
          isEnabled = ScriptableBoolean.Fixed(true),
          textStyleSource = TextStyleSource.Local(),
          text = ScriptableString.Fixed("Hello World"),
          textColor = BrushSource.SolidColor(ScriptableColor.FixedCustom(Color.Black)),
          minLines = ScriptableDouble.Fixed(2.0),
          maxLines = ScriptableDouble.Fixed(5.0),
          textOverflowSource = TextOverflowSource.Ellipsis,
        )
      )
    val expected =
      listOf<Layer.Evaluated>(
        EvaluatedTextLayer(
          id = 0,
          parentId = null,
          name = "text layer",
          widgetModifiers = listOf(EvaluatedWidthModifier(id = 0, width = 100.dp)),
          clickActions = listOf(ClickAction.MediaSkipToPrevious(0)),
          textStyle =
            TextStyle(
              fontSize = 16.sp,
              fontFamily = FontFamily.Default,
              fontWeight = FontWeight(400),
              fontStyle = FontStyle.Normal,
              textAlign = TextAlign.Start,
            ),
          textColor = SolidColor(Color.Black),
          text = "Hello World",
          minLines = 2,
          maxLines = 5,
          overflow = TextOverflow.Ellipsis,
        )
      )

    observeEvaluation(coldLayers, expected)
  }

  @Test
  fun `evaluate single ColdImageLayer`() = runTest {
    val coldLayers =
      listOf(
        ColdImageLayer(
          id = 0,
          parentId = null,
          name = "image layer",
          widgetModifiers = listOf(ColdWidthModifier(id = 0, width = ScriptableDp.Fixed(120.dp))),
          clickActions = listOf(ClickAction.MediaPause(0)),
          isEnabled = ScriptableBoolean.Fixed(true),
          imageUriSource = ImageUriSource.Gallery(null),
          contentScale = ContentScaleSource.Crop,
          tint = ScriptableColor.FixedCustom(Color.Red),
        )
      )
    val expected =
      listOf<Layer.Evaluated>(
        EvaluatedImageLayer(
          id = 0,
          parentId = null,
          name = "image layer",
          widgetModifiers = listOf(EvaluatedWidthModifier(id = 0, width = 120.dp)),
          clickActions = listOf(ClickAction.MediaPause(0)),
          image = null,
          contentScale = ContentScale.Crop,
          tint = Color.Red,
        )
      )

    observeEvaluation(coldLayers, expected)
  }

  @Test
  fun `evaluate single ColdProgressBarLayer`() = runTest {
    val coldLayers =
      listOf(
        ColdProgressBarLayer(
          id = 0,
          parentId = null,
          name = "progress bar layer",
          widgetModifiers = listOf(ColdWidthModifier(id = 0, width = ScriptableDp.Fixed(130.dp))),
          clickActions = listOf(ClickAction.MediaPause(0)),
          isEnabled = ScriptableBoolean.Fixed(true),
          progress = ScriptableDouble.Fixed(0.75),
          progressBarType = ProgressBarType.CIRCULAR,
          color = ScriptableColor.FixedCustom(Color.Blue),
          trackColor = ScriptableColor.FixedCustom(Color.Gray),
          gapSize = ScriptableDp.Fixed(6.dp),
          amplitude = ScriptableDouble.Fixed(0.5),
          waveLength = ScriptableDp.Fixed(20.dp),
        )
      )
    val expected =
      listOf<Layer.Evaluated>(
        EvaluatedProgressBarLayer(
          id = 0,
          parentId = null,
          name = "progress bar layer",
          widgetModifiers = listOf(EvaluatedWidthModifier(id = 0, width = 130.dp)),
          clickActions = listOf(ClickAction.MediaPause(0)),
          progress = 0.75f,
          progressBarType = ProgressBarType.CIRCULAR,
          color = Color.Blue,
          trackColor = Color.Gray,
          gapSize = 6.dp,
          amplitude = 0.5f,
          waveLength = 20.dp,
        )
      )

    observeEvaluation(coldLayers, expected)
  }

  @Test
  fun `evaluate single ColdStepIndicatorLayer`() = runTest {
    val coldLayers =
      listOf(
        ColdStepIndicatorLayer(
          id = 0,
          parentId = null,
          name = "step indicator layer",
          widgetModifiers = listOf(ColdWidthModifier(id = 0, width = ScriptableDp.Fixed(140.dp))),
          clickActions = listOf(ClickAction.MediaPlay(0)),
          isEnabled = ScriptableBoolean.Fixed(true),
          fill = ScriptableBoolean.Fixed(false),
          totalSteps = ScriptableDouble.Fixed(8.0),
          currentStep = ScriptableDouble.Fixed(3.0),
          indicatorSize = ScriptableDp.Fixed(10.dp),
          activeColor = ScriptableColor.FixedCustom(Color.Green),
          inactiveColor = ScriptableColor.FixedCustom(Color.Yellow),
          shape = ShapeSource.CutCornersDp(isRounded = false, size = 0.dp),
        )
      )
    val expected =
      listOf<Layer.Evaluated>(
        EvaluatedStepIndicatorLayer(
          id = 0,
          parentId = null,
          name = "step indicator layer",
          widgetModifiers = listOf(EvaluatedWidthModifier(id = 0, width = 140.dp)),
          clickActions = listOf(ClickAction.MediaPlay(0)),
          fill = false,
          totalSteps = 8,
          currentStep = 3,
          indicatorSize = 10.dp,
          activeColor = Color.Green,
          inactiveColor = Color.Yellow,
          shape = RectangleShape,
        )
      )

    observeEvaluation(coldLayers, expected)
  }
}
