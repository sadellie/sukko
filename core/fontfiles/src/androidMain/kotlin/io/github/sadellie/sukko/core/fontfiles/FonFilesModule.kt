package io.github.sadellie.sukko.core.fontfiles

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.lazyModule

val fontFilesModule = lazyModule {
  factory<FontFileCustomRepository> { FontFileCustomRepositoryImpl(context = androidContext()) }
  single<FontFamilyLoader> { FontFamilyLoader() }
}
