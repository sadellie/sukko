package io.github.sadellie.sukko.feature.settings

import io.github.sadellie.sukko.core.routes.CommonRoute
import io.github.sadellie.sukko.core.routes.LocalNavigator
import io.github.sadellie.sukko.feature.settings.notificationlistener.NotificationListenerRoute
import io.github.sadellie.sukko.feature.settings.notificationlistener.NotificationListenerScene
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.lazyModule
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val settingsModule = lazyModule {
  navigation<CommonRoute.SettingsRoute> {
    val navigator = LocalNavigator.current
    SettingsScene(
      onNavigateUp = navigator::goBack,
      navigateToNotificationListener = { navigator.goTo(NotificationListenerRoute) },
      onNavigateToIconPackEditor = { navigator.goTo(CommonRoute.IconPacksListEditorRoute) },
      onNavigateToFontFilesEditor = { navigator.goTo(CommonRoute.FontFilesEditorRoute) },
    )
  }
  navigation<NotificationListenerRoute> {
    NotificationListenerScene(onNavigateUp = LocalNavigator.current::goBack)
  }
}
