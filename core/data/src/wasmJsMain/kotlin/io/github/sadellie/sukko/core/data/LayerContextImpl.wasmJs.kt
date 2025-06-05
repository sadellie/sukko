package io.github.sadellie.sukko.core.data

import androidx.compose.ui.text.font.FontFamily
import io.github.sadellie.sukko.core.common.notReady
import io.github.sadellie.sukko.core.fontfiles.FontFile
import io.github.sadellie.sukko.core.model.LayerContext
import io.github.sadellie.sukko.core.model.data.BatteryInfoProvider
import io.github.sadellie.sukko.core.model.data.DateTimeProvider
import io.github.sadellie.sukko.core.model.data.DynamicColorSchemeProvider
import io.github.sadellie.sukko.core.model.data.MediaInfoProvider
import kotlin.coroutines.CoroutineContext
import okio.Path

actual class LayerContextImpl(actual override val dateTimeProvider: DateTimeProvider) :
  LayerContext() {
  actual override val filesDirPath: Path
    get() = notReady

  actual override val batteryInfoProvider: BatteryInfoProvider
    get() = notReady

  actual override val mediaInfoProvider: MediaInfoProvider
    get() = notReady

  actual override val dynamicColorSchemeProvider: DynamicColorSchemeProvider
    get() = notReady

  actual override val deviceModel: String
    get() = notReady

  actual override suspend fun loadAndCacheImage(uri: String): String = notReady

  actual override suspend fun loadFontFamily(fontFile: FontFile): FontFamily = notReady

  actual override fun invalidateOnAlarmProviders(): LayerContext = notReady

  actual override fun invalidateMediaInfoProvider(): LayerContext = notReady

  actual val parentCoroutineContext: CoroutineContext
    get() = notReady
}

actual class LayerContextProvider {
  actual fun provide(parentCoroutineContext: CoroutineContext): LayerContext = notReady
}
