package io.github.sadellie.sukko.feature.saveaspreset

import io.github.sadellie.sukko.core.routes.CommonRoute
import io.github.sadellie.sukko.core.routes.LocalNavigator
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.lazyModule
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val saveAsPresetModule = lazyModule {
  navigation<CommonRoute.SaveAsPresetRoute> {
    SaveAsPresetScene(onNavigateUp = LocalNavigator.current::goBack, it.appWidgetId)
  }
  viewModelOf(::SaveAsPresetViewModel)
}
