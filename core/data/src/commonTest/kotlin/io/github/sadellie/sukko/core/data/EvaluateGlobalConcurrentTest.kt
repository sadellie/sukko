package io.github.sadellie.sukko.core.data

import androidx.compose.ui.unit.dp
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.basic.GlobalValue
import io.github.sadellie.sukko.core.model.basic.ScriptableDouble
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
    // linked scriptables: global2 -> global1 -> local(fixed 12.0)
    val globals =
      Globals(
        doubles =
          listOf(
            GlobalValue.GlobalDouble(
              id = 1,
              label = "global1",
              initialValue = ScriptableDouble.Fixed(12.0),
            ),
            GlobalValue.GlobalDouble(
              id = 2,
              label = "global2",
              initialValue = ScriptableDouble.Global(1),
            ),
          )
      )
    val coldLayers =
      List<Layer.Cold>(10) {
        ColdBoxLayer(
          id = it,
          widgetModifiers = listOf(ColdWidthModifier(id = 0, width = ScriptableDouble.Global(2))),
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
        doubles =
          listOf(
            GlobalValue.GlobalDouble(
              id = 1,
              label = "global1",
              initialValue = ScriptableDouble.Global(2),
            ),
            GlobalValue.GlobalDouble(
              id = 2,
              label = "global2",
              initialValue = ScriptableDouble.Global(1),
            ),
          )
      )
    val coldLayers =
      List<Layer.Cold>(10) {
        ColdBoxLayer(
          id = it,
          widgetModifiers = listOf(ColdWidthModifier(id = 0, width = ScriptableDouble.Global(2))),
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

  @OptIn(FlowPreview::class)
  @Test
  fun testRandomOrderInScript() = runTest {
    // linked scriptables: global3 -> global2 -> local(fixed 6)
    val globals =
      Globals(
        doubles =
          listOf(
            GlobalValue.GlobalDouble(
              id = 1,
              label = "global 1",
              initialValue = ScriptableDouble.Fixed(5.0),
            ),
            GlobalValue.GlobalDouble(
              id = 2,
              label = "global 2",
              initialValue = ScriptableDouble.Fixed(6.0),
            ),
            GlobalValue.GlobalDouble(
              id = 3,
              label = "global 3",
              initialValue = ScriptableDouble.Script("globalNumber(2)"),
            ),
          )
      )
    val coldLayers =
      List<Layer.Cold>(10) {
        ColdBoxLayer(
          id = it,
          widgetModifiers =
            listOf(
              ColdWidthModifier(
                id = 0,
                // order of globals should not matter even in scripts.
                // recursion support across concurrent evaluation of same script by multiple layers:
                // will compute each global only once even though this script is called 10 times
                // - compute 2
                // - compute 1
                // - compute 3, which links to 2 (will take value from cache)
                width =
                  ScriptableDouble.Script("globalNumber(2) + globalNumber(1) + globalNumber(3)"),
              )
            ),
        )
      }

    val expectedEvaluatedLayers =
      List<Layer.Evaluated>(10) {
        EvaluatedBoxLayer(
          id = it,
          parentId = null,
          name = null,
          widgetModifiers = listOf(EvaluatedWidthModifier(id = 0, width = 17.dp)),
          clickActions = emptyList(),
        )
      }

    observeEvaluation(coldLayers, expectedEvaluatedLayers, globals)
  }
}
