package io.github.sadellie.sukko.core.data

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.basic.BrushSource
import io.github.sadellie.sukko.core.model.basic.ScriptableColor
import io.github.sadellie.sukko.core.model.basic.ScriptableDouble
import io.github.sadellie.sukko.core.model.fakeContext
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BrushSourceEvaluatorTest {

  private val layerContext = fakeContext()
  private val globals = Globals()

  @Test
  fun `evaluate SolidColor returns SolidColor brush`() = runTest {
    val brushSource = BrushSource.SolidColor(ScriptableColor.FixedCustom(Color.Red))
    val evaluator = BrushSourceEvaluator(brushSource, layerContext, globals)
    val result = evaluator.evaluate()
    assertEquals(SolidColor(Color.Red), result)
  }

  @Test
  fun `evaluate LinearGradient horizontal creates horizontal gradient`() = runTest {
    val colors =
      listOf(
        0f to ScriptableColor.FixedCustom(Color.Red),
        1f to ScriptableColor.FixedCustom(Color.Blue),
      )
    val brushSource = BrushSource.LinearGradient(colors, horizontal = true)
    val evaluator = BrushSourceEvaluator(brushSource, layerContext, globals)
    val result = evaluator.evaluate()
    val expected = Brush.horizontalGradient(0f to Color.Red, 1f to Color.Blue)
    assertEquals(expected, result)
    // Note: Cannot directly compare gradient type, so we verify colors and assume horizontal
  }

  @Test
  fun `evaluate LinearGradient vertical creates vertical gradient`() = runTest {
    val colors =
      listOf(
        0f to ScriptableColor.FixedCustom(Color.Red),
        1f to ScriptableColor.FixedCustom(Color.Blue),
      )
    val brushSource = BrushSource.LinearGradient(colors, horizontal = false)
    val evaluator = BrushSourceEvaluator(brushSource, layerContext, globals)
    val result = evaluator.evaluate()
    val expected = Brush.verticalGradient(0f to Color.Red, 1f to Color.Blue)
    assertEquals(expected, result)
  }

  @Test
  fun `evaluate LinearGradient requires at least 2 colors`() = runTest {
    val colors = listOf(0f to ScriptableColor.FixedCustom(Color.Red))
    val brushSource = BrushSource.LinearGradient(colors, horizontal = true)
    val evaluator = BrushSourceEvaluator(brushSource, layerContext, globals)
    assertFailsWith<IllegalArgumentException> { evaluator.evaluate() }
  }

  @Test
  fun `evaluate RadialGradient creates radial gradient with correct radius`() = runTest {
    val colors =
      listOf(ScriptableColor.FixedCustom(Color.Red), ScriptableColor.FixedCustom(Color.Blue))
    val radius = ScriptableDouble.Fixed(50.0)
    val brushSource = BrushSource.RadialGradient(colors, radius)
    val evaluator = BrushSourceEvaluator(brushSource, layerContext, globals)
    val result = evaluator.evaluate()
    val expected = Brush.radialGradient(colors = listOf(Color.Red, Color.Blue), radius = 50f)
    assertEquals(expected, result)
  }
}
