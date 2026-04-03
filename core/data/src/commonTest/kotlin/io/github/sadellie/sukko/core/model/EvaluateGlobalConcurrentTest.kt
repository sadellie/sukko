package io.github.sadellie.sukko.core.model

import androidx.compose.ui.unit.dp
import io.github.sadellie.sukko.core.model.basic.GlobalValue
import io.github.sadellie.sukko.core.model.basic.ScriptableDp
import io.github.sadellie.sukko.core.model.layer.ColdBoxLayer
import io.github.sadellie.sukko.core.model.layer.EvaluatedBoxLayer
import io.github.sadellie.sukko.core.model.layer.Layer
import io.github.sadellie.sukko.core.model.modifier.ColdWidthModifier
import io.github.sadellie.sukko.core.model.modifier.EvaluatedWidthModifier
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

// flaky tests
class EvaluateGlobalConcurrentTest {
  @OptIn(FlowPreview::class)
  @Test
  fun testRandomOrderOfEvaluation() = runTest {
    // linked scriptables: global2 -> global1 -> local(fixed 12.dp)
    val globals =
      Globals(
        dps =
          listOf(
            GlobalValue.GlobalDp(id = 1, label = "global1", value = ScriptableDp.Fixed(12.dp)),
            GlobalValue.GlobalDp(id = 2, label = "global2", value = ScriptableDp.Global(1)),
          )
      )
    val coldLayers =
      List<Layer.Cold>(10) {
        ColdBoxLayer(
          id = it,
          widgetModifiers = listOf(ColdWidthModifier(id = 0, width = ScriptableDp.Global(2))),
        )
      }

    val expectedEvaluatedLayers =
      List<Layer.Evaluated>(10) {
        EvaluatedBoxLayer(
          id = it,
          parentId = null,
          name = null,
          widgetModifiers = listOf(EvaluatedWidthModifier(id = 0, width = 12.dp)),
          clickActions = emptyList(),
        )
      }

    observeEvaluation(coldLayers, expectedEvaluatedLayers, globals)
  }

  @OptIn(FlowPreview::class)
  @Test
  fun testRandomOrderOfCyclicLinkEvaluation() = runTest {
    // linked scriptables: global2 -> global1 -> global2 -> ... should set default value (0.dp)
    val globals =
      Globals(
        dps =
          listOf(
            GlobalValue.GlobalDp(id = 1, label = "global1", value = ScriptableDp.Global(2)),
            GlobalValue.GlobalDp(id = 2, label = "global2", value = ScriptableDp.Global(1)),
          )
      )
    val coldLayers =
      List<Layer.Cold>(10) {
        ColdBoxLayer(
          id = it,
          widgetModifiers = listOf(ColdWidthModifier(id = 0, width = ScriptableDp.Global(2))),
        )
      }

    val expectedEvaluatedLayers =
      List<Layer.Evaluated>(10) {
        EvaluatedBoxLayer(
          id = it,
          parentId = null,
          name = null,
          widgetModifiers = listOf(EvaluatedWidthModifier(id = 0, width = 0.dp)),
          clickActions = emptyList(),
        )
      }

    observeEvaluation(coldLayers, expectedEvaluatedLayers, globals)
  }
}
