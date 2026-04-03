package io.github.sadellie.sukko.core.data

import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.FixedScale
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.LayerContext
import io.github.sadellie.sukko.core.model.basic.ContentScaleSource
import io.github.sadellie.sukko.core.model.basic.ScriptableDouble
import io.github.sadellie.sukko.core.model.fakeContext
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ContentScaleSourceEvaluatorTest {

  private val layerContext: LayerContext = fakeContext()
  private val globals: Globals = Globals()

  @Test
  fun `evaluate Crop returns ContentScale Crop`() = runTest {
    val evaluator = ContentScaleSourceEvaluator(ContentScaleSource.Crop, layerContext, globals)
    val result = evaluator.evaluate()
    assertEquals(ContentScale.Crop, result)
  }

  @Test
  fun `evaluate FillBounds returns ContentScale FillBounds`() = runTest {
    val evaluator =
      ContentScaleSourceEvaluator(ContentScaleSource.FillBounds, layerContext, globals)
    val result = evaluator.evaluate()
    assertEquals(ContentScale.FillBounds, result)
  }

  @Test
  fun `evaluate FillHeight returns ContentScale FillHeight`() = runTest {
    val evaluator =
      ContentScaleSourceEvaluator(ContentScaleSource.FillHeight, layerContext, globals)
    val result = evaluator.evaluate()
    assertEquals(ContentScale.FillHeight, result)
  }

  @Test
  fun `evaluate FillWidth returns ContentScale FillWidth`() = runTest {
    val evaluator = ContentScaleSourceEvaluator(ContentScaleSource.FillWidth, layerContext, globals)
    val result = evaluator.evaluate()
    assertEquals(ContentScale.FillWidth, result)
  }

  @Test
  fun `evaluate Fit returns ContentScale Fit`() = runTest {
    val evaluator = ContentScaleSourceEvaluator(ContentScaleSource.Fit, layerContext, globals)
    val result = evaluator.evaluate()
    assertEquals(ContentScale.Fit, result)
  }

  @Test
  fun `evaluate FixedScale returns FixedScale with resolved float value`() = runTest {
    val scaleSource = ScriptableDouble.Fixed(2.5)
    val contentScaleSource = ContentScaleSource.FixedScale(scaleSource)
    val evaluator = ContentScaleSourceEvaluator(contentScaleSource, layerContext, globals)
    val result = evaluator.evaluate()
    assertEquals(FixedScale(2.5f), result)
  }

  @Test
  fun `evaluate Inside returns ContentScale Inside`() = runTest {
    val evaluator = ContentScaleSourceEvaluator(ContentScaleSource.Inside, layerContext, globals)
    val result = evaluator.evaluate()
    assertEquals(ContentScale.Inside, result)
  }

  @Test
  fun `evaluate None returns ContentScale None`() = runTest {
    val evaluator = ContentScaleSourceEvaluator(ContentScaleSource.None, layerContext, globals)
    val result = evaluator.evaluate()
    assertEquals(ContentScale.None, result)
  }
}
