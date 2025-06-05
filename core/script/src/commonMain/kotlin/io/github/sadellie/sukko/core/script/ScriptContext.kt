package io.github.sadellie.sukko.core.script

interface BasicScriptContext {
  val batteryCapacity: Int
  val batteryChargeDischargeSeconds: Int
  val batteryStatus: String
  val currentTimestamp: Long
  val deviceModel: String
  val mediaArtist: String?
  val mediaCoverUri: String?
  val mediaDuration: Long?
  val mediaPosition: Long?
  val mediaTitle: String?
  val playerIcon: String?
  val playerState: String?
  val playerName: String?
  val volumeMusicMin: Int
  val volumeMusic: Int
  val volumeMusicMax: Int

  fun currentDate(format: String): String

  fun currentDateWithTimeZone(format: String, timeZoneId: String): String

  fun formatTimestamp(timeStamp: Long, format: String): String

  fun dynamicColor(m3ColorName: String): String

  suspend fun colorScheme(m3ColorName: String, source: String): String
}

class ScriptContext(private val scriptContext: BasicScriptContext) :
  BasicScriptContext by scriptContext {
  internal val variableValueMemory: HashMap<VariableNode, ASTNode> = hashMapOf()
}
