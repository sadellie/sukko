package io.github.sadellie.sukko.feature.fontseditor

import io.github.sadellie.sukko.core.fontfiles.fontFilesModule
import io.github.sadellie.sukko.core.routes.CommonRoute
import io.github.sadellie.sukko.core.routes.LocalNavigator
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.module.includes
import org.koin.dsl.lazyModule
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val fontsEditorModule = lazyModule {
  navigation<CommonRoute.FontFilesEditorRoute> {
    FontsEditorScene(onNavigateUp = LocalNavigator.current::goBack)
  }
  includes(fontFilesModule)
  viewModelOf(::FontsEditorViewModel)
}
