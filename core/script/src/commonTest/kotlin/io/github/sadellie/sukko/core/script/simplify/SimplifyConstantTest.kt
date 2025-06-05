package io.github.sadellie.sukko.core.script.simplify

import io.github.sadellie.sukko.core.script.ASTNode
import io.github.sadellie.sukko.core.script.NumberNode
import io.github.sadellie.sukko.core.script.TextNode
import io.github.sadellie.sukko.core.script.assertSimplifySingleRule
import kotlin.test.Test

class SimplifyConstantTest {
  @Test fun simplify_batteryLevel() = assertSimplify("batteryLevel", NumberNode(72))

  @Test
  fun simplify_batteryStatus() =
    assertSimplify("batteryStatus", TextNode("BATTERY_STATUS_CHARGING"))

  @Test fun simplify_mediaArtist() = assertSimplify("mediaArtist", TextNode("artist1"))

  @Test fun simplify_mediaTitle() = assertSimplify("mediaTitle", TextNode("title1"))

  @Test fun simplify_mediaDuration() = assertSimplify("mediaDuration", NumberNode(10_000L))

  @Test fun simplify_mediaPosition() = assertSimplify("mediaPosition", NumberNode(469))

  @Test fun simplify_mediaCover() = assertSimplify("mediaCover", TextNode("file://uri/to/file"))

  @Test fun simplify_playerName() = assertSimplify("playerName", TextNode("player 1"))

  @Test fun simplify_deviceModel() = assertSimplify("deviceModel", TextNode("Potato"))

  @Test fun simplify_batteryFullEmpty() = assertSimplify("batteryFullEmpty", NumberNode(123))

  @Test fun simplify_currentTimestamp() = assertSimplify("currentTimestamp", NumberNode(123456L))

  @Test fun simplify_playerIcon() = assertSimplify("playerIcon", TextNode("file://some/path"))

  @Test fun simplify_playerState() = assertSimplify("playerState", TextNode("PLAYING"))

  @Test fun simplify_volumeMusicMin() = assertSimplify("volumeMusicMin", NumberNode(0))

  @Test fun simplify_volumeMusic() = assertSimplify("volumeMusic", NumberNode(7))

  @Test fun simplify_volumeMusicMax() = assertSimplify("volumeMusicMax", NumberNode(15))

  private fun assertSimplify(input: String, expected: ASTNode?) {
    assertSimplifySingleRule(
      rule = SimplifyConstant,
      simplificationType = SimplificationType.EVAL_CONSTANT,
      input = input,
      expected = expected,
    )
  }
}
