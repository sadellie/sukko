package io.github.sadellie.sukko.core.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import io.github.sadellie.sukko.core.model.basic.GlobalValue
import io.github.sadellie.sukko.core.model.basic.ScriptableString
import io.github.sadellie.sukko.core.model.layer.ColdTextLayer
import io.github.sadellie.sukko.core.model.layer.EvaluatedTextLayer
import io.github.sadellie.sukko.core.model.layer.Layer
import kotlin.test.Test
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.test.runTest

// flaky tests
class EvaluateGlobalConcurrentTest {
  @OptIn(FlowPreview::class)
  @Test
  fun testRandomOrderOfEvaluation() = runTest {
    // linked scriptables: global2 -> global1 -> local(script batteryStatus)
    val globals =
      Globals(
        strings =
          listOf(
            GlobalValue.GlobalString(
              id = 1,
              label = "global1",
              value = ScriptableString.Script("batteryStatus"),
            ),
            GlobalValue.GlobalString(id = 2, label = "global2", value = ScriptableString.Global(1)),
          )
      )
    val coldLayers =
      List<Layer.Cold>(10) { ColdTextLayer(id = it, text = ScriptableString.Global(id = 2)) }

    val expectedEvaluatedLayers =
      List<Layer.Evaluated>(10) {
        EvaluatedTextLayer(
          id = it,
          parentId = null,
          name = null,
          widgetModifiers = emptyList(),
          clickActions = emptyList(),
          textStyle =
            TextStyle(
              fontSize = 16.sp,
              fontWeight = FontWeight(400),
              textAlign = TextAlign.Start,
              fontFamily = FontFamily.Default,
            ),
          text = "test battery status",
          textColor = SolidColor(Color.Unspecified),
        )
      }

    observeEvaluation(coldLayers, expectedEvaluatedLayers, globals)
  }

  @OptIn(FlowPreview::class)
  @Test
  fun testRandomOrderOfCyclicLinkEvaluation() = runTest {
    // linked scriptables: global2 -> global1 -> global2 -> ...
    val globals =
      Globals(
        strings =
          listOf(
            GlobalValue.GlobalString(id = 1, label = "global1", value = ScriptableString.Global(2)),
            GlobalValue.GlobalString(id = 2, label = "global2", value = ScriptableString.Global(1)),
          )
      )
    val coldLayers =
      List<Layer.Cold>(10) { ColdTextLayer(id = it, text = ScriptableString.Global(id = 2)) }
    val expectedEvaluatedLayers =
      List<Layer.Evaluated>(10) {
        EvaluatedTextLayer(
          id = it,
          parentId = null,
          name = null,
          widgetModifiers = emptyList(),
          clickActions = emptyList(),
          textStyle =
            TextStyle(
              fontSize = 16.sp,
              fontWeight = FontWeight(400),
              textAlign = TextAlign.Start,
              fontFamily = FontFamily.Default,
            ),
          text = "",
          textColor = SolidColor(Color.Unspecified),
        )
      }

    observeEvaluation(coldLayers, expectedEvaluatedLayers, globals)
  }
}
