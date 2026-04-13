package io.github.sadellie.sukko.feature.presetselector

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import io.github.sadellie.sukko.core.routes.CommonRoute
import io.github.sadellie.sukko.core.routes.LocalEventBus
import io.github.sadellie.sukko.core.routes.LocalNavigator
import io.github.sadellie.sukko.core.routes.NavigationResult

fun EntryProviderScope<NavKey>.presetSelectorNavigation() =
  entry<CommonRoute.PresetSelectorRoute> {
    val navigator = LocalNavigator.current
    val eventBus = LocalEventBus.current
    PresetSelectorScene(
      onNavigateUp = navigator::goBack,
      onSelect = { presetId, isBuiltIn ->
        eventBus.sendResult(
          NavigationResult.PresetSelectorResult.KEY,
          NavigationResult.PresetSelectorResult(presetId, isBuiltIn),
        )
        navigator.goBack()
      },
    )
  }
