package io.github.sadellie.sukko.feature.editor

import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import io.github.sadellie.sukko.core.medialistener.NotificationListener
import io.github.sadellie.sukko.core.routes.CommonRoute
import io.github.sadellie.sukko.core.routes.LocalEventBus
import io.github.sadellie.sukko.core.routes.LocalNavigator
import io.github.sadellie.sukko.core.routes.NavigationResult
import io.github.sadellie.sukko.core.routes.ResultEffect

internal actual fun EntryProviderScope<NavKey>.editorRoute() =
  entry<CommonRoute.EditorRoute> { route ->
    val navigator = LocalNavigator.current
    val eventBus = LocalEventBus.current
    val viewModel =
      assistedMetroViewModel<EditorViewModel, EditorViewModel.Factory> { create(route.appWidgetId) }
    ResultEffect<NavigationResult.PresetSelectorResult>(
      eventBus,
      NavigationResult.PresetSelectorResult.KEY,
    ) { result ->
      viewModel.loadFromPreset(result.presetId, result.isBuiltIn)
    }

    val context = LocalContext.current
    EditorScene(
      onNavigateUp = navigator::goBack,
      onNavigateToPresetSelector = { navigator.goTo(CommonRoute.PresetSelectorRoute) },
      onNavigateToSaveAsPreset = {
        navigator.goTo(CommonRoute.SaveAsPresetRoute(route.appWidgetId))
      },
      onNavigateNotificationListener = {
        NotificationListener.openNotificationListenerPermission(context)
      },
      onNavigateToWidgetInfo = { navigator.goTo(CommonRoute.WidgetInfoRoute(route.appWidgetId)) },
      viewModel = viewModel,
    )
  }
