package io.github.sadellie.sukko.core.data

import android.content.Context
import android.os.Build
import io.github.sadellie.sukko.core.common.filesPath
import io.github.sadellie.sukko.core.fontfiles.FontFamilyLoader
import io.github.sadellie.sukko.core.fontfiles.FontFile
import io.github.sadellie.sukko.core.model.LayerContext
import io.github.sadellie.sukko.core.model.provider.BatteryInfoProvider
import io.github.sadellie.sukko.core.model.provider.DateTimeProvider
import io.github.sadellie.sukko.core.model.provider.DynamicColorSchemeProvider
import io.github.sadellie.sukko.core.model.provider.MediaInfoProvider
import okio.Path
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

// lazy values and providers. for example, multiple calls for currentDate will use same instant
// instead of creating a new  one, thus avoiding possible inconsistency within layer evaluation
actual data class LayerContextImpl(
  actual override val batteryInfoProvider: BatteryInfoProvider,
  actual override val dynamicColorSchemeProvider: DynamicColorSchemeProvider,
  actual override val mediaInfoProvider: MediaInfoProvider,
  actual override val dateTimeProvider: DateTimeProvider,
) : LayerContext(), KoinComponent {
  actual override val deviceModel: String = Build.MODEL
  private val androidContext: Context by inject<Context>()
  actual override val filesDirPath: Path = androidContext.filesPath
  private val fontFamilyLoader: FontFamilyLoader by inject()

  actual override suspend fun loadFontFamily(fontFile: FontFile) =
    fontFamilyLoader.loadFromFontFile(fontFile, androidContext.filesPath)

  actual override fun invalidateOnAlarmProviders(): LayerContext =
    copy(
      dateTimeProvider = DateTimeProvider(),
      batteryInfoProvider = BatteryInfoProviderImpl(androidContext),
    )

  actual override fun invalidateMediaInfoProvider(): LayerContext =
    copy(mediaInfoProvider = MediaInfoProviderImpl(androidContext))
}

actual class LayerContextProvider : KoinComponent {
  private val androidContext: Context by inject<Context>()
  private val imageProvider: ImageProvider by inject()

  actual fun provide(): LayerContext {
    return LayerContextImpl(
      batteryInfoProvider = BatteryInfoProviderImpl(androidContext),
      dynamicColorSchemeProvider = DynamicColorSchemeProviderImpl(androidContext, imageProvider),
      mediaInfoProvider = MediaInfoProviderImpl(androidContext),
      dateTimeProvider = DateTimeProvider(),
    )
  }
}
