package io.github.sadellie.sukko.core.data

import androidx.compose.ui.graphics.Color
import coil3.Image
import coil3.ImageLoader
import coil3.Uri
import io.github.sadellie.sukko.core.fontfiles.FontFamilyLoader
import io.github.sadellie.sukko.core.model.GlobalValueCache
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.basic.M3Color
import io.github.sadellie.sukko.core.model.layer.Layer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import okio.Path
import okio.Path.Companion.toPath
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
internal suspend fun TestScope.observeEvaluation(
  coldLayers: List<Layer.Cold>,
  expectedLayers: List<Layer.Evaluated>,
  globals: Globals = Globals(),
  layerEvaluator: LayerEvaluator =
    LayerEvaluator(
      coldLayers,
      fakeImageProvider(),
      fakeScriptableEvaluator(globals),
      fakeFilesDirPath,
      fakeFontFamilyLoader(),
      fakeTextStyleSourceEvaluator(GlobalValueCache(), fakeScriptableEvaluator(globals), globals),
    ),
) {
  val actualEvaluatedLayers = Channel<List<Layer.Evaluated>>()
  this.backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
    layerEvaluator
      .evaluateEnabled()
      .onEach {
        println("evaluated layers: $it")
        this@observeEvaluation.advanceUntilIdle()
      }
      .debounce(10.seconds)
      .collectLatest { if (it == expectedLayers) actualEvaluatedLayers.send(it) }
  }
  assertEquals(expectedLayers, actualEvaluatedLayers.receive())
}

internal fun fakeImageProvider() =
  object : ImageProvider {
    override val imageLoader: ImageLoader
      get() = noImpl

    override suspend fun updateAlbumCoverFromUri(uri: Uri) = noImpl

    override suspend fun getImageFromUri(uri: String): Image = noImpl
  }

internal fun fakeScriptableEvaluator(globals: Globals = Globals()): ScriptableEvaluator =
  ScriptableEvaluator(
    globals = globals,
    globalValueCache = GlobalValueCache(),
    scriptableEvaluatorContext =
      ScriptableEvaluatorContext(
        dynamicColorSchemeProvider = fakeDynamicColorSchemeProvider(),
        batteryInfoProvider = fakeBatteryInfoProvider(),
        dateTimeProvider = fakeDateTimeProvider(),
        deviceInfoProvider = fakeDeviceInfoProvider(),
        mediaInfoProvider = fakeMediaInfoProvider(),
      ),
    globalCurrentValueStore = fakeGlobalCurrentValueStore(),
  )

internal fun fakeDynamicColorSchemeProvider(): DynamicColorSchemeProvider =
  object : DynamicColorSchemeProvider {
    override fun getColorFromSystemColorScheme(m3Color: M3Color): Color = Color.Unspecified

    override fun extractHexFromSystemColorScheme(m3ColorName: String) = "VALUE_$m3ColorName"

    override suspend fun extractHexFromImageColorScheme(m3ColorName: String, imageUri: String) =
      "${m3ColorName}_COLOR_FROM_$imageUri"
  }

internal fun fakeBatteryInfoProvider() =
  object : BatteryInfoProvider {
    override val capacity = 50
    override val status: String = "BATTERY_STATUS_CHARGING"
    override val chargeDischargeSeconds = 50
  }

internal fun fakeMediaInfoProvider() =
  object : MediaInfoProvider {
    override val artist = "Artist"

    override val title = "Title"

    override val durationSeconds = 300L

    override val positionSeconds = 469L

    override val playerName = "Player name"

    override val playerState = "PLAYING"

    override val volumeMusicMin = 0

    override val volumeMusic = 7

    override val volumeMusicMax = 10
  }

internal fun fakeDateTimeProvider() =
  object : DateTimeProvider {
    override val instant: Instant = Instant.fromEpochSeconds(1778661766)
  }

internal fun fakeFontFamilyLoader(): FontFamilyLoader = FontFamilyLoader()

internal fun fakeDeviceInfoProvider(): DeviceInfoProvider =
  object : DeviceInfoProvider {
    override val model = "Potato 16S"
  }

internal fun fakeGlobalCurrentValueStore() =
  object : GlobalCurrentValueStore {
    private val strings: MutableMap<Long, String> = mutableMapOf()
    private val doubles: MutableMap<Long, Double> = mutableMapOf()
    private val booleans: MutableMap<Long, Boolean> = mutableMapOf()

    override suspend fun getCurrentStringValue(id: Long) = strings[id]

    override suspend fun getCurrentBooleanValue(id: Long) = booleans[id]

    override suspend fun getCurrentDoubleValue(id: Long) = doubles[id]

    override suspend fun saveStringValue(id: Long, value: String) = strings.set(id, value)

    override suspend fun saveBooleanValue(id: Long, value: Boolean) = booleans.set(id, value)

    override suspend fun saveDoubleValue(id: Long, value: Double) = doubles.set(id, value)

    override fun clearCache(): GlobalCurrentValueStore = noImpl
  }

internal fun fakeTextStyleSourceEvaluator(
  globalValueCache: GlobalValueCache,
  scriptableEvaluator: ScriptableEvaluator,
  globals: Globals,
): TextStyleSourceEvaluator =
  TextStyleSourceEvaluator(
    fakeFilesDirPath,
    fakeFontFamilyLoader(),
    globalValueCache,
    scriptableEvaluator = scriptableEvaluator,
    globals = globals,
  )

internal val fakeFilesDirPath: Path = "".toPath()

internal val noImpl: Nothing
  get() = error("Not implemented")
