package io.github.sadellie.sukko.feature.editor

import androidx.compose.ui.platform.LocalContext
import io.github.sadellie.sukko.core.data.dataModule
import io.github.sadellie.sukko.core.medialistener.NotificationListener
import io.github.sadellie.sukko.core.routes.CommonRoute
import io.github.sadellie.sukko.core.routes.LocalEventBus
import io.github.sadellie.sukko.core.routes.LocalNavigator
import io.github.sadellie.sukko.core.routes.NavigationResult
import io.github.sadellie.sukko.core.routes.ResultEffect
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.includes
import org.koin.core.parameter.parametersOf
import org.koin.dsl.lazyModule
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val editorModule = lazyModule {
  editorModule()
  navigation<CommonRoute.EditorRoute> {
    val navigator = LocalNavigator.current
    val eventBus = LocalEventBus.current
    val viewModel =
      koinViewModel<EditorViewModel>(key = it.appWidgetId.toString()) {
        parametersOf(it.appWidgetId)
      }
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
      onNavigateToSaveAsPreset = { navigator.goTo(CommonRoute.SaveAsPresetRoute(it.appWidgetId)) },
      onNavigateNotificationListener = {
        NotificationListener.openNotificationListenerPermission(context)
      },
      onNavigateToWidgetInfo = { navigator.goTo(CommonRoute.WidgetInfoRoute(it.appWidgetId)) },
      viewModel = viewModel,
    )
  }
  includes(dataModule)
}
