package io.github.sadellie.sukko.feature.widgetinfo

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import io.github.sadellie.sukko.core.routes.CommonRoute
import io.github.sadellie.sukko.core.routes.LocalNavigator

fun EntryProviderScope<NavKey>.widgetInfoNavigation() =
  entry<CommonRoute.WidgetInfoRoute> {
    WidgetInfoScene(onNavigateUp = LocalNavigator.current::goBack, appWidgetId = it.appWidgetId)
  }
