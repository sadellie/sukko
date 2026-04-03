package io.github.sadellie.sukko.feature.widget

import org.koin.dsl.module

val widgetModule = module { factory<WidgetInfoRepository> { WidgetInfoRepositoryImpl() } }
