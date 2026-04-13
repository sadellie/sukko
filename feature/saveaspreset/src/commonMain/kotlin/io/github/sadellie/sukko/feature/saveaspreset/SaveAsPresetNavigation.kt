package io.github.sadellie.sukko.feature.saveaspreset

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import io.github.sadellie.sukko.core.routes.CommonRoute
import io.github.sadellie.sukko.core.routes.LocalNavigator

fun EntryProviderScope<NavKey>.saveAsPresetNavigation() =
  entry<CommonRoute.SaveAsPresetRoute> {
    SaveAsPresetScene(onNavigateUp = LocalNavigator.current::goBack, it.appWidgetId)
  }
