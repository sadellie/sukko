package io.github.sadellie.sukko.feature.widgetinfo

import io.github.sadellie.sukko.core.routes.CommonRoute
import io.github.sadellie.sukko.core.routes.LocalNavigator
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
internal fun Module.widgetInfoModule() {
  navigation<CommonRoute.WidgetInfoRoute> {
    WidgetInfoScene(onNavigateUp = LocalNavigator.current::goBack, appWidgetId = it.appWidgetId)
  }
  viewModelOf(::WidgetInfoViewModel)
}
