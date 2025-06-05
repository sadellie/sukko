package io.github.sadellie.sukko.core.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import io.github.sadellie.sukko.core.fontfiles.FontFile
import io.github.sadellie.sukko.core.model.basic.M3Color
import io.github.sadellie.sukko.core.model.data.BatteryInfoProvider
import io.github.sadellie.sukko.core.model.data.DateTimeProvider
import io.github.sadellie.sukko.core.model.data.DynamicColorSchemeProvider
import io.github.sadellie.sukko.core.model.data.MediaInfoProvider
import io.github.sadellie.sukko.core.model.layer.Layer
import io.github.sadellie.sukko.core.model.layer.evaluateEnabled
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import okio.Path

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
internal suspend fun TestScope.observeEvaluation(
  coldLayers: List<Layer.Cold>,
  expectedLayers: List<Layer.Evaluated>,
  globals: Globals = Globals(),
  layerContext: LayerContext = fakeContext(),
) {
  val actualEvaluatedLayers = Channel<List<Layer.Evaluated>>()
  this.backgroundScope.launch {
    coldLayers
      .evaluateEnabled(layerContext, globals)
      .onEach { this@observeEvaluation.advanceUntilIdle() }
      .debounce(10.seconds)
      .collectLatest { actualEvaluatedLayers.send(it) }
  }
  assertEquals(expectedLayers, actualEvaluatedLayers.receive())
}

internal fun fakeContext() =
  object : LayerContext() {
    override val filesDirPath: Path
      get() = noImpl

    override val batteryInfoProvider =
      object : BatteryInfoProvider {
        override val capacity = -1
        override val status: String = "test battery status"
        override val chargeDischargeSeconds = -1
      }
    override val mediaInfoProvider: MediaInfoProvider
      get() = noImpl

    override val dynamicColorSchemeProvider =
      object : DynamicColorSchemeProvider {
        override fun getColorFromSystemColorScheme(m3Color: M3Color): Color = Color.Unspecified

        override fun extractHexFromSystemColorScheme(m3ColorName: String) = noImpl

        override suspend fun extractHexFromImageColorScheme(m3ColorName: String, imageUri: String) =
          noImpl
      }
    override val dateTimeProvider: DateTimeProvider
      get() = noImpl

    override val deviceModel: String = ""

    override suspend fun loadAndCacheImage(uri: String) = noImpl

    override suspend fun loadFontFamily(fontFile: FontFile) = FontFamily.Default

    override fun invalidateOnAlarmProviders() = noImpl

    override fun invalidateMediaInfoProvider() = noImpl
  }

private val noImpl: Nothing
  get() = error("Not implemented")
