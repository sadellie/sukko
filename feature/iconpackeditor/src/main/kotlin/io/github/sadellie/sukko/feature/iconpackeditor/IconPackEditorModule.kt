package io.github.sadellie.sukko.feature.iconpackeditor

import androidx.navigation3.runtime.NavKey
import io.github.sadellie.sukko.core.iconfiles.IconPack
import io.github.sadellie.sukko.core.routes.CommonRoute
import io.github.sadellie.sukko.core.routes.LocalNavigator
import kotlinx.serialization.Serializable
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.lazyModule
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val iconPackEditorModule = lazyModule {
  navigation<CommonRoute.IconPacksListEditorRoute> {
    val navigator = LocalNavigator.current
    IconPacksScene(
      onNavigateUp = navigator::goBack,
      navigateToIconPackEditor = { navigator.goTo(IconPackEditorRoute(it)) },
    )
  }
  navigation<IconPackEditorRoute> {
    val navigator = LocalNavigator.current
    IconPackEditorScene(onNavigateUp = navigator::goBack, iconPack = it.iconPack)
  }
  viewModelOf(::IconPacksViewModel)
  viewModelOf(::IconPackEditorViewModel)
}

@Serializable internal data class IconPackEditorRoute(val iconPack: IconPack) : NavKey
