package io.github.sadellie.sukko.feature.widget

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.lazyModule

val widgetModule = lazyModule {
  factory<WidgetInfoRepository> { WidgetInfoRepositoryImpl(context = androidContext()) }
}
