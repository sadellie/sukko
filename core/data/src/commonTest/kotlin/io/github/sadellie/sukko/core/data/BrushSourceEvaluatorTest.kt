package io.github.sadellie.sukko.core.data

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import io.github.sadellie.sukko.core.model.basic.BrushSource
import io.github.sadellie.sukko.core.model.basic.ScriptableColor
import io.github.sadellie.sukko.core.model.basic.ScriptableDouble
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BrushSourceEvaluatorTest {
  private val evaluator = BrushSourceEvaluator(fakeScriptableEvaluator())

  @Test
  fun `evaluate SolidColor returns SolidColor brush`() = runTest {
    val brushSource = BrushSource.SolidColor(ScriptableColor.FixedCustom(Color.Red))
    val result = evaluator.evaluate(brushSource)
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
    val result = evaluator.evaluate(brushSource)
    val expected = Brush.horizontalGradient(0f to Color.Red, 1f to Color.Blue)
    assertEquals(expected, result)
  }

  @Test
  fun `evaluate LinearGradient vertical creates vertical gradient`() = runTest {
    val colors =
      listOf(
        0f to ScriptableColor.FixedCustom(Color.Red),
        1f to ScriptableColor.FixedCustom(Color.Blue),
      )
    val brushSource = BrushSource.LinearGradient(colors, horizontal = false)
    val result = evaluator.evaluate(brushSource)
    val expected = Brush.verticalGradient(0f to Color.Red, 1f to Color.Blue)
    assertEquals(expected, result)
  }

  @Test
  fun `evaluate LinearGradient requires at least 2 colors`() = runTest {
    val colors = listOf(0f to ScriptableColor.FixedCustom(Color.Red))
    val brushSource = BrushSource.LinearGradient(colors, horizontal = true)
    assertFailsWith<IllegalArgumentException> { evaluator.evaluate(brushSource) }
  }

  @Test
  fun `evaluate RadialGradient creates radial gradient with correct radius`() = runTest {
    val colors =
      listOf(ScriptableColor.FixedCustom(Color.Red), ScriptableColor.FixedCustom(Color.Blue))
    val radius = ScriptableDouble.Fixed(50.0)
    val brushSource = BrushSource.RadialGradient(colors, radius)
    val result = evaluator.evaluate(brushSource)
    val expected = Brush.radialGradient(colors = listOf(Color.Red, Color.Blue), radius = 50f)
    assertEquals(expected, result)
  }
}
