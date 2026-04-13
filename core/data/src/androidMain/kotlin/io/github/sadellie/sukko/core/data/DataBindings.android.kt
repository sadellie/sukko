package io.github.sadellie.sukko.core.data

import android.content.Context
import coil3.ImageLoader
import coil3.memory.MemoryCache
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import io.github.sadellie.sukko.core.data.script.DeviceInfoProviderImpl
import io.github.sadellie.sukko.core.data.script.docs.DocsRepository
import io.github.sadellie.sukko.core.data.script.docs.DocsRepositoryImpl
import io.github.sadellie.sukko.core.database.IconPackDao
import io.github.sadellie.sukko.core.database.WidgetDataDao
import io.github.sadellie.sukko.core.database.WidgetDataPresetDao
import io.github.sadellie.sukko.core.medialistener.MediaListener
import okio.Path.Companion.toOkioPath

@BindingContainer
actual class DataBindings {
  @Provides
  fun provideBatteryInfoProvider(context: Context): BatteryInfoProvider =
    BatteryInfoProviderImpl(context)

  @Provides
  fun provideDynamicColorSchemeProvider(
    context: Context,
    imageProvider: ImageProvider,
  ): DynamicColorSchemeProvider = DynamicColorSchemeProviderImpl(context, imageProvider)

  @Provides
  fun provideMediaInfoProvider(context: Context, mediaListener: MediaListener): MediaInfoProvider =
    MediaInfoProviderImpl(context, mediaListener)

  @Provides fun provideDocsRepository(): DocsRepository = DocsRepositoryImpl()

  @Provides
  fun provideWidgetDataRepository(
    widgetDataDao: WidgetDataDao,
    context: Context,
    imageLoader: ImageLoader,
    widgetDataStoreManager: WidgetDataStoreManager,
  ): WidgetDataRepository =
    WidgetDataRepositoryImpl(
      dao = widgetDataDao,
      context = context,
      removeImageFromCache = { imagePath ->
        imageLoader.memoryCache?.remove(MemoryCache.Key(imagePath.toString()))
      },
      widgetDataStoreManager = widgetDataStoreManager,
    )

  @Provides
  fun provideWidgetDataPresetCustomRepository(
    widgetDataPresetDao: WidgetDataPresetDao,
    context: Context,
    imageLoader: ImageLoader,
  ): WidgetDataPresetCustomRepository =
    WidgetDataPresetCustomRepositoryImpl(
      dao = widgetDataPresetDao,
      context = context,
      imageLoader = imageLoader,
    )

  @Provides
  fun provideIconPackCustomRepository(
    iconPackDao: IconPackDao,
    context: Context,
    imageLoader: ImageLoader,
  ): IconPackCustomRepository =
    IconPackCustomRepositoryImpl(dao = iconPackDao, context = context, imageLoader = imageLoader)

  @Provides
  fun provideInstalledAppsProvider(context: Context): InstalledAppsProvider =
    InstalledAppsProviderImpl(context = context)

  @SingleIn(AppScope::class)
  @Provides
  fun provideImageProvider(context: Context): ImageProvider =
    ImageProviderImpl(
      platformContext = context,
      cacheDir = context.cacheDir.resolve("image_cache").toOkioPath(),
    )

  @Provides
  fun provideWidgetInfoRepository(context: Context): WidgetInfoRepository =
    WidgetInfoRepositoryImpl(context = context)

  @SingleIn(AppScope::class)
  @Provides
  fun provideImageLoader(imageProvider: ImageProvider): ImageLoader = imageProvider.imageLoader

  @Provides
  fun provideWidgetSubscriptionsRepository(context: Context): WidgetSubscriptionsRepository =
    WidgetSubscriptionsRepositoryImpl(context)

  @SingleIn(AppScope::class)
  @Provides
  fun provideWidgetDataStoreManager(context: Context): WidgetDataStoreManager =
    WidgetDataStoreManagerImpl(context)

  @SingleIn(AppScope::class)
  @Provides
  fun provideDeviceInfoProvider(): DeviceInfoProvider = DeviceInfoProviderImpl()

  @Provides fun provideDateTimeProvider(): DateTimeProvider = DateTimeProviderImpl()
}
