package io.github.sadellie.sukko.feature.settings

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import io.github.sadellie.sukko.core.routes.CommonRoute
import io.github.sadellie.sukko.core.routes.LocalNavigator
import io.github.sadellie.sukko.feature.settings.notificationlistener.NotificationListenerRoute
import io.github.sadellie.sukko.feature.settings.notificationlistener.NotificationListenerScene

fun EntryProviderScope<NavKey>.settingsNavigation(openNotificationListenerPermission: () -> Unit) {
  entry<CommonRoute.SettingsRoute> {
    val navigator = LocalNavigator.current
    SettingsScene(
      onNavigateUp = navigator::goBack,
      navigateToNotificationListener = { navigator.goTo(NotificationListenerRoute) },
      onNavigateToIconPackEditor = { navigator.goTo(CommonRoute.IconPacksListEditorRoute) },
      onNavigateToFontFilesEditor = { navigator.goTo(CommonRoute.FontFilesEditorRoute) },
    )
  }
  entry<NotificationListenerRoute> {
    NotificationListenerScene(
      onNavigateUp = LocalNavigator.current::goBack,
      openNotificationListenerPermission = openNotificationListenerPermission,
    )
  }
}
