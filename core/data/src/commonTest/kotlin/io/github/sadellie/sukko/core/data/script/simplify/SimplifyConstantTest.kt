package io.github.sadellie.sukko.core.data.script.simplify

import io.github.sadellie.sukko.core.data.script.ASTNode
import io.github.sadellie.sukko.core.data.script.NumberNode
import io.github.sadellie.sukko.core.data.script.TextNode
import io.github.sadellie.sukko.core.data.script.assertSimplifySingleRule
import kotlin.test.Test

class SimplifyConstantTest {
  @Test fun simplify_batteryLevel() = assertSimplify("batteryLevel", NumberNode(50))

  @Test
  fun simplify_batteryStatus() =
    assertSimplify("batteryStatus", TextNode("BATTERY_STATUS_CHARGING"))

  @Test fun simplify_mediaArtist() = assertSimplify("mediaArtist", TextNode("Artist"))

  @Test fun simplify_mediaTitle() = assertSimplify("mediaTitle", TextNode("Title"))

  @Test fun simplify_mediaDuration() = assertSimplify("mediaDuration", NumberNode(300.0))

  @Test fun simplify_mediaPosition() = assertSimplify("mediaPosition", NumberNode(469.0))

  @Test fun simplify_mediaCover() = assertSimplify("mediaCover", TextNode("sukko://album_cover"))

  @Test fun simplify_playerName() = assertSimplify("playerName", TextNode("Player name"))

  @Test fun simplify_deviceModel() = assertSimplify("deviceModel", TextNode("Potato 16S"))

  @Test fun simplify_batteryFullEmpty() = assertSimplify("batteryFullEmpty", NumberNode(50))

  @Test
  fun simplify_currentTimestamp() = assertSimplify("currentTimestamp", NumberNode(1778661766.0))

  @Test fun simplify_playerIcon() = assertSimplify("playerIcon", TextNode("sukko://player_icon"))

  @Test fun simplify_playerState() = assertSimplify("playerState", TextNode("PLAYING"))

  @Test fun simplify_volumeMusicMin() = assertSimplify("volumeMusicMin", NumberNode(0))

  @Test fun simplify_volumeMusic() = assertSimplify("volumeMusic", NumberNode(7))

  @Test fun simplify_volumeMusicMax() = assertSimplify("volumeMusicMax", NumberNode(10))

  private fun assertSimplify(input: String, expected: ASTNode?) {
    assertSimplifySingleRule(
      rule = SimplifyConstant,
      simplificationType = SimplificationType.EVAL_CONSTANT,
      input = input,
      expected = expected,
    )
  }
}
