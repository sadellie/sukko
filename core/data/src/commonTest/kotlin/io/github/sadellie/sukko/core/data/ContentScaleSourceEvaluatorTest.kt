package io.github.sadellie.sukko.core.data

import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.FixedScale
import io.github.sadellie.sukko.core.model.basic.ContentScaleSource
import io.github.sadellie.sukko.core.model.basic.ScriptableDouble
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ContentScaleSourceEvaluatorTest {
  private val evaluator = ContentScaleSourceEvaluator(fakeScriptableEvaluator())

  @Test
  fun `evaluate Crop returns ContentScale Crop`() = runTest {
    val result = evaluator.evaluate(ContentScaleSource.Crop)
    assertEquals(ContentScale.Crop, result)
  }

  @Test
  fun `evaluate FillBounds returns ContentScale FillBounds`() = runTest {
    val result = evaluator.evaluate(ContentScaleSource.FillBounds)
    assertEquals(ContentScale.FillBounds, result)
  }

  @Test
  fun `evaluate FillHeight returns ContentScale FillHeight`() = runTest {
    val result = evaluator.evaluate(ContentScaleSource.FillHeight)
    assertEquals(ContentScale.FillHeight, result)
  }

  @Test
  fun `evaluate FillWidth returns ContentScale FillWidth`() = runTest {
    val result = evaluator.evaluate(ContentScaleSource.FillWidth)
    assertEquals(ContentScale.FillWidth, result)
  }

  @Test
  fun `evaluate Fit returns ContentScale Fit`() = runTest {
    val result = evaluator.evaluate(ContentScaleSource.Fit)
    assertEquals(ContentScale.Fit, result)
  }

  @Test
  fun `evaluate FixedScale returns FixedScale with resolved float value`() = runTest {
    val scaleSource = ScriptableDouble.Fixed(2.5)
    val contentScaleSource = ContentScaleSource.FixedScale(scaleSource)
    val result = evaluator.evaluate(contentScaleSource)
    assertEquals(FixedScale(2.5f), result)
  }

  @Test
  fun `evaluate Inside returns ContentScale Inside`() = runTest {
    val result = evaluator.evaluate(ContentScaleSource.Inside)
    assertEquals(ContentScale.Inside, result)
  }

  @Test
  fun `evaluate None returns ContentScale None`() = runTest {
    val result = evaluator.evaluate(ContentScaleSource.None)
    assertEquals(ContentScale.None, result)
  }
}
