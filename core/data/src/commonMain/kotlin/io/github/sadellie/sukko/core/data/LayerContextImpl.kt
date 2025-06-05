package io.github.sadellie.sukko.core.data

import androidx.compose.ui.text.font.FontFamily
import io.github.sadellie.sukko.core.common.SuspendLazy
import io.github.sadellie.sukko.core.fontfiles.FontFile
import io.github.sadellie.sukko.core.model.LayerContext
import io.github.sadellie.sukko.core.model.data.BatteryInfoProvider
import io.github.sadellie.sukko.core.model.data.DateTimeProvider
import io.github.sadellie.sukko.core.model.data.DynamicColorSchemeProvider
import io.github.sadellie.sukko.core.model.data.MediaInfoProvider
import kotlin.coroutines.CoroutineContext
import okio.Path

/**
 * @property parentCoroutineContext Coroutine context in which [SuspendLazy] will be invoked. Layer
 *   will create a children coroutine context and invoke all [SuspendLazy] values in it.
 */
expect class LayerContextImpl : LayerContext {
  val parentCoroutineContext: CoroutineContext
  override val filesDirPath: Path
  override val batteryInfoProvider: BatteryInfoProvider
  override val mediaInfoProvider: MediaInfoProvider
  override val dynamicColorSchemeProvider: DynamicColorSchemeProvider
  override val dateTimeProvider: DateTimeProvider
  override val deviceModel: String

  override suspend fun loadAndCacheImage(uri: String): String

  override suspend fun loadFontFamily(fontFile: FontFile): FontFamily

  override fun invalidateOnAlarmProviders(): LayerContext

  override fun invalidateMediaInfoProvider(): LayerContext
}

expect class LayerContextProvider() {
  fun provide(parentCoroutineContext: CoroutineContext): LayerContext
}
