package io.github.sadellie.sukko.core.data

import androidx.compose.ui.text.font.FontFamily
import io.github.sadellie.sukko.core.common.notReady
import io.github.sadellie.sukko.core.fontfiles.FontFile
import io.github.sadellie.sukko.core.model.LayerContext
import io.github.sadellie.sukko.core.model.provider.BatteryInfoProvider
import io.github.sadellie.sukko.core.model.provider.DateTimeProvider
import io.github.sadellie.sukko.core.model.provider.DynamicColorSchemeProvider
import io.github.sadellie.sukko.core.model.provider.MediaInfoProvider
import okio.Path

actual class LayerContextImpl(
  actual override val dateTimeProvider: DateTimeProvider,
) : LayerContext() {
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

  actual override suspend fun loadFontFamily(fontFile: FontFile): FontFamily = notReady

  actual override fun invalidateOnAlarmProviders(): LayerContext = notReady

  actual override fun invalidateMediaInfoProvider(): LayerContext = notReady
}

actual class LayerContextProvider {
  actual fun provide(): LayerContext = notReady
}
