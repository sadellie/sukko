package io.github.sadellie.sukko.core.script

import io.github.sadellie.sukko.core.script.simplify.SimplificationRule
import io.github.sadellie.sukko.core.script.simplify.SimplificationStep
import io.github.sadellie.sukko.core.script.simplify.SimplificationType
import io.github.sadellie.sukko.core.script.token.tokenize
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest

object FakeBasicScriptContext : BasicScriptContext {
  override val batteryCapacity = 72
  override val batteryChargeDischargeSeconds = 123
  override val mediaArtist = "artist1"
  override val mediaTitle = "title1"
  override val mediaDuration = 10_000L
  override val mediaPosition = 469L
  override val mediaCoverUri = "file://uri/to/file"
  override val playerName = "player 1"
  override val playerIcon = "file://some/path"
  override val playerState = "PLAYING"
  override val deviceModel = "Potato"
  override val currentTimestamp = 123456L
  override val batteryStatus = "BATTERY_STATUS_CHARGING"
  override val volumeMusicMin = 0
  override val volumeMusic = 7
  override val volumeMusicMax = 15

  override fun currentDate(format: String) = "23:59"

  override fun currentDateWithTimeZone(format: String, timeZoneId: String) = "$format-$timeZoneId"

  override fun formatTimestamp(timeStamp: Long, format: String) = "$format $timeStamp"

  override fun dynamicColor(m3ColorName: String) = "VALUE_$m3ColorName"

  override suspend fun colorScheme(m3ColorName: String, source: String) =
    "${m3ColorName}_COLOR_FROM_$source"
}

internal fun assertSimplifySingleRule(
  rule: SimplificationRule,
  simplificationType: SimplificationType,
  context: ScriptContext = ScriptContext(FakeBasicScriptContext),
  input: String,
  expected: ASTNode?,
) = runTest {
  val inputTree = buildTreeAndCollapse(input)
  val actual = rule.simplify(context, inputTree)

  val expectedStep =
    expected?.let { SimplificationStep(simplifiedASTNode = expected, type = simplificationType) }
  assertEquals(expectedStep, actual)
}

internal fun buildTreeAndCollapse(input: String): ASTNode {
  val tokens = tokenize(input)
  return ASTNode.buildTreesAndCollapse(tokens, ScriptContext(FakeBasicScriptContext)).first()
}
