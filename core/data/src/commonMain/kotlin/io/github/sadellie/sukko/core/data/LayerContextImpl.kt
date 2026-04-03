package io.github.sadellie.sukko.core.data

import androidx.compose.ui.text.font.FontFamily
import io.github.sadellie.sukko.core.fontfiles.FontFile
import io.github.sadellie.sukko.core.model.LayerContext
import io.github.sadellie.sukko.core.model.provider.BatteryInfoProvider
import io.github.sadellie.sukko.core.model.provider.DateTimeProvider
import io.github.sadellie.sukko.core.model.provider.DynamicColorSchemeProvider
import io.github.sadellie.sukko.core.model.provider.MediaInfoProvider
import okio.Path

expect class LayerContextImpl : LayerContext {
  override val filesDirPath: Path
  override val batteryInfoProvider: BatteryInfoProvider
  override val mediaInfoProvider: MediaInfoProvider
  override val dynamicColorSchemeProvider: DynamicColorSchemeProvider
  override val dateTimeProvider: DateTimeProvider
  override val deviceModel: String

  override suspend fun loadFontFamily(fontFile: FontFile): FontFamily

  override fun invalidateOnAlarmProviders(): LayerContext

  override fun invalidateMediaInfoProvider(): LayerContext
}

expect class LayerContextProvider() {
  fun provide(): LayerContext
}
