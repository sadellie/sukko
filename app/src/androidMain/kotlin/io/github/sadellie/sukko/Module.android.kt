package io.github.sadellie.sukko

import coil3.ImageLoader
import coil3.memory.MemoryCache
import io.github.sadellie.sukko.core.data.IconPackCustomRepository
import io.github.sadellie.sukko.core.data.IconPackCustomRepositoryImpl
import io.github.sadellie.sukko.core.data.InstalledAppsProvider
import io.github.sadellie.sukko.core.data.InstalledAppsProviderImpl
import io.github.sadellie.sukko.core.data.WidgetDataPresetCustomRepository
import io.github.sadellie.sukko.core.data.WidgetDataPresetCustomRepositoryImpl
import io.github.sadellie.sukko.core.data.WidgetDataRepository
import io.github.sadellie.sukko.core.data.WidgetDataRepositoryImpl
import io.github.sadellie.sukko.core.database.SukkoDatabase
import io.github.sadellie.sukko.core.widget.WidgetInfoRepository
import io.github.sadellie.sukko.core.widget.WidgetInfoRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module

fun Module.allDataModules() {
  widgetInfoRepository()
  widgetDataRepository()
  widgetDataPresetRepository()
  iconPackRepository()
  installedAppsRepository()
}

actual fun Module.widgetInfoRepository(): KoinDefinition<WidgetInfoRepository> =
  factory<WidgetInfoRepository> { WidgetInfoRepositoryImpl(context = androidContext()) }

actual fun Module.widgetDataRepository(): KoinDefinition<WidgetDataRepository> =
  factory<WidgetDataRepository> {
    WidgetDataRepositoryImpl(
      dao = get<SukkoDatabase>().widgetDataDao(),
      context = androidContext(),
      removeImageFromCache = { imagePath ->
        get<ImageLoader>().memoryCache?.remove(MemoryCache.Key(imagePath.toString()))
      },
    )
  }

actual fun Module.widgetDataPresetRepository(): KoinDefinition<WidgetDataPresetCustomRepository> =
  factory<WidgetDataPresetCustomRepository> {
    WidgetDataPresetCustomRepositoryImpl(
      dao = get<SukkoDatabase>().widgetDataPresetDao(),
      context = androidContext(),
      removeImageFromCache = { imagePath ->
        get<ImageLoader>().memoryCache?.remove(MemoryCache.Key(imagePath.toString()))
      },
    )
  }

actual fun Module.iconPackRepository(): KoinDefinition<IconPackCustomRepository> =
  factory<IconPackCustomRepository> {
    IconPackCustomRepositoryImpl(
      dao = get<SukkoDatabase>().iconPackDao(),
      context = androidContext(),
      removeImageFromCache = { imagePath ->
        get<ImageLoader>().memoryCache?.remove(MemoryCache.Key(imagePath.toString()))
      },
    )
  }

actual fun Module.installedAppsRepository(): KoinDefinition<InstalledAppsProvider> =
  factory<InstalledAppsProvider> { InstalledAppsProviderImpl(context = androidContext()) }
