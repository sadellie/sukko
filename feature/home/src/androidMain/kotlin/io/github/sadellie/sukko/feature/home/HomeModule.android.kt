package io.github.sadellie.sukko.feature.home

import androidx.compose.ui.platform.LocalContext
import io.github.sadellie.sukko.core.common.uri
import io.github.sadellie.sukko.core.routes.CommonRoute
import io.github.sadellie.sukko.core.routes.LocalNavigator
import io.github.sadellie.sukko.feature.widget.MainWidgetProvider
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.lazyModule
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val homeModule = lazyModule {
  navigation<CommonRoute.HomeRoute> {
    val navigator = LocalNavigator.current
    val context = LocalContext.current
    HomeScene(
      navigateToEditor = { navigator.goTo(CommonRoute.EditorRoute(it)) },
      navigateToSettings = { navigator.goTo(CommonRoute.SettingsRoute) },
      navigateToImportPreset = {
        navigator.goTo(CommonRoute.ImportPresetRoute(it.uri().toString()))
      },
      onAddWidget = { MainWidgetProvider.pin(context) },
    )
  }
  viewModelOf(::WidgetsViewModel)
  viewModelOf(::PresetsViewModel)
}
