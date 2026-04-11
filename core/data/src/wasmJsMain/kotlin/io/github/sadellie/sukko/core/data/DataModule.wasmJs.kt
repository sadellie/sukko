package io.github.sadellie.sukko.core.data

import coil3.ImageLoader
import coil3.PlatformContext
import io.github.sadellie.sukko.core.remote.remoteModule
import io.github.sadellie.sukko.core.script.docs.DocsRepository
import io.github.sadellie.sukko.core.script.docs.DocsRepositoryImpl
import okio.Path.Companion.toPath
import org.koin.dsl.module

val dataModule = module {
  includes(remoteModule)
  factory<DocsRepository> { DocsRepositoryImpl() }
  factory<WidgetDataRepository> { WidgetDataRepositoryImpl() }
  factory<WidgetInfoRepository> { WidgetInfoRepositoryImpl() }
  single<ImageProvider> {
    val context = PlatformContext.INSTANCE
    ImageProviderImpl(platformContext = context, cacheDir = "image_cache".toPath())
  }
  single<ImageLoader> { get<ImageProvider>().imageLoader }
}
