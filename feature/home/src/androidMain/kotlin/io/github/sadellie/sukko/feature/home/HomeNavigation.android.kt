package io.github.sadellie.sukko.feature.home

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import io.github.sadellie.sukko.core.common.uri
import io.github.sadellie.sukko.core.routes.CommonRoute
import io.github.sadellie.sukko.core.routes.LocalNavigator

internal actual fun EntryProviderScope<NavKey>.homeEntry(onAddWidget: () -> Unit) =
  entry<CommonRoute.HomeRoute> {
    val navigator = LocalNavigator.current
    HomeScene(
      navigateToEditor = { navigator.goTo(CommonRoute.EditorRoute(it)) },
      navigateToSettings = { navigator.goTo(CommonRoute.SettingsRoute) },
      navigateToImportPreset = {
        navigator.goTo(CommonRoute.ImportPresetRoute(it.uri().toString()))
      },
      onAddWidget = onAddWidget,
    )
  }
