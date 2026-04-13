package io.github.sadellie.sukko.feature.iconpackeditor

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import io.github.sadellie.sukko.core.iconfiles.IconPack
import io.github.sadellie.sukko.core.routes.CommonRoute
import io.github.sadellie.sukko.core.routes.LocalNavigator
import kotlinx.serialization.Serializable

fun EntryProviderScope<NavKey>.iconPackEditorNavigation() {
  entry<CommonRoute.IconPacksListEditorRoute> {
    val navigator = LocalNavigator.current
    IconPacksScene(
      onNavigateUp = navigator::goBack,
      navigateToIconPackEditor = { navigator.goTo(IconPackEditorRoute(it)) },
    )
  }
  entry<IconPackEditorRoute> {
    val navigator = LocalNavigator.current
    IconPackEditorScene(onNavigateUp = navigator::goBack, iconPack = it.iconPack)
  }
}

@Serializable internal data class IconPackEditorRoute(val iconPack: IconPack) : NavKey
