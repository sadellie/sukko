package io.github.sadellie.sukko.feature.home

import io.github.sadellie.sukko.core.routes.CommonRoute
import io.github.sadellie.sukko.core.routes.LocalNavigator
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val homeModule = module {
  navigation<CommonRoute.HomeRoute> {
    val navigator = LocalNavigator.current
    HomeScene(
      navigateToEditor = { navigator.goTo(CommonRoute.EditorRoute(it)) },
      navigateToSettings = { navigator.goTo(CommonRoute.SettingsRoute) },
      navigateToImportPreset = {},
      onAddWidget = {},
    )
  }
  viewModelOf(::WidgetsViewModel)
}
