package io.github.sadellie.sukko.core.model

import androidx.compose.ui.text.font.FontFamily
import io.github.sadellie.sukko.core.data.ImageProvider
import io.github.sadellie.sukko.core.fontfiles.FontFile
import io.github.sadellie.sukko.core.model.provider.BatteryInfoProvider
import io.github.sadellie.sukko.core.model.provider.DateTimeProvider
import io.github.sadellie.sukko.core.model.provider.DynamicColorSchemeProvider
import io.github.sadellie.sukko.core.model.provider.MediaInfoProvider
import io.github.sadellie.sukko.core.script.BasicScriptContext
import io.github.sadellie.sukko.core.script.ScriptContext
import okio.Path

abstract class LayerContext {
  val sharedScriptContext: BasicScriptContext by lazy { asScriptContext() }
  val globalValueCache: GlobalValueCache = GlobalValueCache()

  /** Path to root directory with all files, including cache directory */
  abstract val filesDirPath: Path
  abstract val batteryInfoProvider: BatteryInfoProvider
  abstract val mediaInfoProvider: MediaInfoProvider
  abstract val dynamicColorSchemeProvider: DynamicColorSchemeProvider
  abstract val dateTimeProvider: DateTimeProvider
  abstract val deviceModel: String

  abstract suspend fun loadFontFamily(fontFile: FontFile): FontFamily

  /** Invalidate providers that may have outdated data on time tick */
  abstract fun invalidateOnAlarmProviders(): LayerContext

  /** Invalidate [mediaInfoProvider] when media info changes */
  abstract fun invalidateMediaInfoProvider(): LayerContext

  /** Creates a [ScriptContext] with clean [ScriptContext.variableValueMemory] */
  fun scriptContext(): ScriptContext = ScriptContext(sharedScriptContext)
}

// script module is separated, need to map like this
fun LayerContext.asScriptContext(): BasicScriptContext =
  object : BasicScriptContext {
    // getters for lazy values (avoid race)
    override val batteryCapacity
      get() = this@asScriptContext.batteryInfoProvider.capacity

    override val batteryChargeDischargeSeconds
      get() = this@asScriptContext.batteryInfoProvider.chargeDischargeSeconds

    override val batteryStatus
      get() = this@asScriptContext.batteryInfoProvider.status

    override val currentTimestamp
      get() = this@asScriptContext.dateTimeProvider.currentTimestamp

    override val deviceModel
      get() = this@asScriptContext.deviceModel

    override val mediaArtist
      get() = this@asScriptContext.mediaInfoProvider.artist

    override val mediaCoverUri
      get() = ImageProvider.ALBUM_COVER_URI

    override val mediaDuration
      get() = this@asScriptContext.mediaInfoProvider.durationSeconds

    override val mediaPosition
      get() = this@asScriptContext.mediaInfoProvider.positionSeconds

    override val mediaTitle
      get() = this@asScriptContext.mediaInfoProvider.title

    override val playerIcon
      get() = ImageProvider.PLAYER_ICON_URI

    override val playerName
      get() = this@asScriptContext.mediaInfoProvider.playerName

    override val playerState
      get() = this@asScriptContext.mediaInfoProvider.playerState

    override val volumeMusicMin
      get() = this@asScriptContext.mediaInfoProvider.volumeMusicMin

    override val volumeMusic: Int
      get() = this@asScriptContext.mediaInfoProvider.volumeMusic

    override val volumeMusicMax: Int
      get() = this@asScriptContext.mediaInfoProvider.volumeMusicMax

    override fun currentDate(format: String) =
      this@asScriptContext.dateTimeProvider.currentDate(format)

    override fun currentDateWithTimeZone(format: String, timeZoneId: String) =
      this@asScriptContext.dateTimeProvider.currentDateWithTimeZone(format, timeZoneId)

    override fun formatTimestamp(timeStamp: Long, format: String) =
      this@asScriptContext.dateTimeProvider.formatTimestamp(timeStamp, format)

    override fun dynamicColor(m3ColorName: String) =
      this@asScriptContext.dynamicColorSchemeProvider.extractHexFromSystemColorScheme(m3ColorName)

    override suspend fun colorScheme(m3ColorName: String, source: String): String =
      this@asScriptContext.dynamicColorSchemeProvider.extractHexFromImageColorScheme(
        m3ColorName,
        source,
      )
  }
