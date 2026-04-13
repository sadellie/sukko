package io.github.sadellie.sukko.core.data

import io.github.sadellie.sukko.core.model.basic.ScriptableBoolean
import io.github.sadellie.sukko.core.model.layer.ColdBoxLayer
import io.github.sadellie.sukko.core.model.layer.ColdColumnLayer
import io.github.sadellie.sukko.core.model.layer.ColdImageLayer
import io.github.sadellie.sukko.core.model.layer.ColdProgressBarLayer
import io.github.sadellie.sukko.core.model.layer.ColdRowLayer
import io.github.sadellie.sukko.core.model.layer.ColdTextLayer
import io.github.sadellie.sukko.core.model.layer.EvaluatedBoxLayer
import io.github.sadellie.sukko.core.model.layer.EvaluatedColumnLayer
import io.github.sadellie.sukko.core.model.layer.EvaluatedRowLayer
import io.github.sadellie.sukko.core.model.layer.Layer
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class LayerEvaluatorEnableTest {
  @OptIn(FlowPreview::class)
  @Test
  fun evaluateEnabled_allEnabled() = runTest {
    val expectedLayers =
      listOf(
        EvaluatedBoxLayer(id = 0, parentId = null),
        EvaluatedBoxLayer(id = 1, parentId = 0),
        EvaluatedColumnLayer(id = 2, parentId = null),
        EvaluatedBoxLayer(id = 3, parentId = 2),
        EvaluatedRowLayer(id = 4, parentId = null),
        EvaluatedBoxLayer(id = 5, parentId = 4),
        EvaluatedBoxLayer(id = 6, parentId = null),
      )
    val coldLayers =
      listOf(
        // box with box inside
        ColdBoxLayer(id = 0, parentId = null),
        ColdBoxLayer(id = 1, parentId = 0),

        // column with box inside
        ColdColumnLayer(id = 2, parentId = null),
        ColdBoxLayer(id = 3, parentId = 2),

        // row with box inside
        ColdRowLayer(id = 4, parentId = null),
        ColdBoxLayer(id = 5, parentId = 4),

        // single layer
        ColdBoxLayer(id = 6, parentId = null),
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
        EvaluatedBoxLayer(id = 0, parentId = null),
        EvaluatedColumnLayer(id = 2, parentId = null),
        EvaluatedRowLayer(id = 4, parentId = null),
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
}
