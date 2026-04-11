package io.github.sadellie.sukko.core.data

import coil3.ImageLoader
import coil3.memory.MemoryCache
import io.github.sadellie.sukko.core.database.IconPackDao
import io.github.sadellie.sukko.core.database.WidgetDataDao
import io.github.sadellie.sukko.core.database.WidgetDataPresetDao
import io.github.sadellie.sukko.core.database.databaseModule
import io.github.sadellie.sukko.core.remote.remoteModule
import io.github.sadellie.sukko.core.script.docs.DocsRepository
import io.github.sadellie.sukko.core.script.docs.DocsRepositoryImpl
import okio.Path.Companion.toOkioPath
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.includes
import org.koin.dsl.lazyModule

val dataModule = lazyModule {
  includes(databaseModule)
  includes(remoteModule)
  factory<DocsRepository> { DocsRepositoryImpl() }
  factory<WidgetDataRepository> {
    WidgetDataRepositoryImpl(
      dao = get<WidgetDataDao>(),
      context = androidContext(),
      removeImageFromCache = { imagePath ->
        get<ImageLoader>().memoryCache?.remove(MemoryCache.Key(imagePath.toString()))
      },
    )
  }
  factory<WidgetDataPresetCustomRepository> {
    WidgetDataPresetCustomRepositoryImpl(
      dao = get<WidgetDataPresetDao>(),
      context = androidContext(),
      removeImageFromCache = { imagePath ->
        get<ImageLoader>().memoryCache?.remove(MemoryCache.Key(imagePath.toString()))
      },
    )
  }
  factory<IconPackCustomRepository> {
    IconPackCustomRepositoryImpl(
      dao = get<IconPackDao>(),
      context = androidContext(),
      removeImageFromCache = { imagePath ->
        get<ImageLoader>().memoryCache?.remove(MemoryCache.Key(imagePath.toString()))
      },
    )
  }
  factory<InstalledAppsProvider> { InstalledAppsProviderImpl(context = androidContext()) }
  single<ImageProvider> {
    val context = androidContext()
    ImageProviderImpl(
      platformContext = context,
      cacheDir = context.cacheDir.resolve("image_cache").toOkioPath(),
    )
  }
  factory<WidgetInfoRepository> { WidgetInfoRepositoryImpl(context = androidContext()) }
  single<ImageLoader> { get<ImageProvider>().imageLoader }
  factory<WidgetSubscriptionsRepository> { WidgetSubscriptionsRepositoryImpl(androidContext()) }
}
