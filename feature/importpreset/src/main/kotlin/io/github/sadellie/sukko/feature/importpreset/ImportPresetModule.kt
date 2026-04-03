package io.github.sadellie.sukko.feature.importpreset

import io.github.sadellie.sukko.core.routes.CommonRoute
import io.github.sadellie.sukko.core.routes.LocalNavigator
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.lazyModule
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val importPresetModule = lazyModule {
  navigation<CommonRoute.ImportPresetRoute> {
    ImportPresetScene(
      navigateUp = LocalNavigator.current::goBack,
      importingPresetUri = it.selectedFileUri,
    )
  }
  viewModelOf(::ImportPresetViewModel)
}
