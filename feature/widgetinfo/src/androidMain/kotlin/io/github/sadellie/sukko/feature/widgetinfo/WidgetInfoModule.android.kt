package io.github.sadellie.sukko.feature.widgetinfo

import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.lazyModule

@OptIn(KoinExperimentalAPI::class) val widgetInfoModule = lazyModule { widgetInfoModule() }
