package io.github.sadellie.sukko.feature.presetselector

import io.github.sadellie.sukko.core.routes.CommonRoute
import io.github.sadellie.sukko.core.routes.LocalEventBus
import io.github.sadellie.sukko.core.routes.LocalNavigator
import io.github.sadellie.sukko.core.routes.NavigationResult
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.lazyModule
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val presetSelectorModule = lazyModule {
  navigation<CommonRoute.PresetSelectorRoute> {
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
  viewModelOf(::PresetSelectorViewModel)
}
