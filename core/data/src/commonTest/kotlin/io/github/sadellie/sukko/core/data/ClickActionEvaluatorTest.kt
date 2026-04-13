package io.github.sadellie.sukko.core.data

import io.github.sadellie.sukko.core.model.basic.ClickAction
import io.github.sadellie.sukko.core.model.basic.ScriptableString
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ClickActionEvaluatorTest {
  private val evaluator = ClickActionEvaluator(fakeScriptableEvaluator())

  @Test
  fun `evaluate maps OpenLink Cold to Evaluated with resolved URL`() = runTest {
    val url = "https://example.com"
    val coldAction = ClickAction.Cold.OpenLink(id = 1, url = ScriptableString.Fixed(url))
    val result = evaluator.evaluate(listOf(coldAction))
    assertEquals(
      expected = listOf(ClickAction.Evaluated.OpenLink(id = 1, url = url)),
      actual = result,
    )
  }

  @Test
  fun `evaluate maps LaunchApp Cold to Evaluated`() = runTest {
    val coldAction = ClickAction.LaunchApp(id = 2)
    val result = evaluator.evaluate(listOf(coldAction))
    assertEquals(expected = listOf(coldAction), actual = result)
  }

  @Test
  fun `evaluate maps MediaPause Cold to Evaluated`() = runTest {
    val coldAction = ClickAction.MediaPause(id = 3)
    val result = evaluator.evaluate(listOf(coldAction))
    assertEquals(expected = listOf(coldAction), actual = result)
  }

  @Test
  fun `evaluate maps MediaPlay Cold to Evaluated`() = runTest {
    val coldAction = ClickAction.MediaPlay(id = 4)
    val result = evaluator.evaluate(listOf(coldAction))
    assertEquals(expected = listOf(coldAction), actual = result)
  }

  @Test
  fun `evaluate maps MediaSkipToNext Cold to Evaluated`() = runTest {
    val coldAction = ClickAction.MediaSkipToNext(id = 5)
    val result = evaluator.evaluate(listOf(coldAction))
    assertEquals(expected = listOf(coldAction), actual = result)
  }

  @Test
  fun `evaluate maps MediaSkipToPrevious Cold to Evaluated`() = runTest {
    val coldAction = ClickAction.MediaSkipToPrevious(id = 6)
    val result = evaluator.evaluate(listOf(coldAction))
    assertEquals(expected = listOf(coldAction), actual = result)
  }

  @Test
  fun `evaluate maps MediaOpenPlayer Cold to Evaluated`() = runTest {
    val coldAction = ClickAction.MediaOpenPlayer(id = 7)
    val result = evaluator.evaluate(listOf(coldAction))
    assertEquals(expected = listOf(coldAction), actual = result)
  }

  @Test
  fun `evaluate handles multiple actions`() = runTest {
    val url = "https://test.com"
    val actions =
      listOf(
        ClickAction.Cold.OpenLink(id = 1, url = ScriptableString.Fixed(url)),
        ClickAction.LaunchApp(id = 2),
        ClickAction.MediaPause(id = 3),
      )
    val result = evaluator.evaluate(actions)
    assertEquals(
      expected =
        listOf(
          ClickAction.Evaluated.OpenLink(id = 1, url = url),
          ClickAction.LaunchApp(id = 2),
          ClickAction.MediaPause(id = 3),
        ),
      actual = result,
    )
  }
}
