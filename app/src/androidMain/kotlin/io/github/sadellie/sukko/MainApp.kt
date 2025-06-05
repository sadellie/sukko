package io.github.sadellie.sukko

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import coil3.ImageLoader
import io.github.sadellie.sukko.core.designsystem.LocalFilesDirPath
import io.github.sadellie.sukko.core.designsystem.LocalImageLoader
import io.github.sadellie.sukko.core.designsystem.LocalWindowSize
import io.github.sadellie.sukko.core.routes.NavigationResult
import io.github.sadellie.sukko.feature.editor.EditorRoute
import io.github.sadellie.sukko.feature.editor.EditorScene
import io.github.sadellie.sukko.feature.editor.EditorViewModel
import io.github.sadellie.sukko.feature.fontseditor.FontFilesEditorRoute
import io.github.sadellie.sukko.feature.fontseditor.FontsEditorScene
import io.github.sadellie.sukko.feature.home.HomeRoute
import io.github.sadellie.sukko.feature.home.HomeScene
import io.github.sadellie.sukko.feature.icopackeditor.IconPackEditor
import io.github.sadellie.sukko.feature.icopackeditor.IconPackEditorScene
import io.github.sadellie.sukko.feature.icopackeditor.IconPacksListEditorRoute
import io.github.sadellie.sukko.feature.icopackeditor.IconPacksScene
import io.github.sadellie.sukko.feature.importpreset.ImportPresetRoute
import io.github.sadellie.sukko.feature.importpreset.ImportPresetScene
import io.github.sadellie.sukko.feature.presetselector.PresetSelectorRoute
import io.github.sadellie.sukko.feature.presetselector.PresetSelectorScene
import io.github.sadellie.sukko.feature.saveaspreset.SaveAsPresetRoute
import io.github.sadellie.sukko.feature.saveaspreset.SaveAsPresetScene
import io.github.sadellie.sukko.feature.settings.SettingsRoute
import io.github.sadellie.sukko.feature.settings.SettingsScene
import io.github.sadellie.sukko.feature.settings.notificationlistener.NotificationListenerRoute
import io.github.sadellie.sukko.feature.settings.notificationlistener.NotificationListenerScene
import io.github.sadellie.themmo.Themmo
import io.github.sadellie.themmo.rememberThemmoController
import okio.Path
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun MainApp(
  onLastRoutePop: () -> Unit,
  windowsSize: WindowSizeClass,
  imageLoader: ImageLoader,
  filesDirPath: Path,
  initialRoute: NavKey,
) {
  CompositionLocalProvider(
    LocalWindowSize provides windowsSize,
    LocalImageLoader provides imageLoader,
    LocalFilesDirPath provides filesDirPath,
  ) {
    val themmoController = rememberThemmoController()
    Themmo(themmoController = themmoController) {
      MainAppNav(initialRoute = initialRoute, onLastRoutePop = onLastRoutePop)
    }
  }
}

@Composable
private fun MainAppNav(initialRoute: NavKey, onLastRoutePop: () -> Unit) {
  val backStack = rememberNavBackStack(initialRoute)
  fun onBack() = if (backStack.size > 1) backStack.removeLastOrNull() else onLastRoutePop()
  var navigationResult by remember { mutableStateOf<NavigationResult?>(null) }
  NavDisplay(
    modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainer),
    backStack = backStack,
    onBack = ::onBack,
    entryDecorators =
      listOf(
        rememberSaveableStateHolderNavEntryDecorator(),
        rememberViewModelStoreNavEntryDecorator(),
      ),
    entryProvider =
      entryProvider {
        entry<HomeRoute> {
          HomeScene(
            navigateToEditor = { backStack.add(EditorRoute(it)) },
            navigateToSettings = { backStack.add(SettingsRoute) },
            navigateToImportPreset = { backStack.add(ImportPresetRoute(it)) },
          )
        }
        entry<EditorRoute> { key ->
          val viewModel =
            koinViewModel<EditorViewModel>(key = key.appWidgetId.toString()) {
              parametersOf(key.appWidgetId)
            }
          LaunchedEffect(navigationResult) {
            when (val result = navigationResult) {
              is NavigationResult.PresetSelectorResult -> {
                viewModel.loadFromPreset(result.presetId, result.isBuiltIn)
                navigationResult = null
              }
              null -> Unit
            }
          }
          EditorScene(
            onNavigateUp = ::onBack,
            onNavigateToSaveAsPreset = { backStack.add(SaveAsPresetRoute(key.appWidgetId)) },
            onNavigateToPresetSelector = { backStack.add(PresetSelectorRoute) },
            viewModel = viewModel,
          )
        }
        entry<PresetSelectorRoute> {
          PresetSelectorScene(
            onNavigateUp = ::onBack,
            onSelect = { presetId, isBuiltIn ->
              navigationResult = NavigationResult.PresetSelectorResult(presetId, isBuiltIn)
              onBack()
            },
          )
        }
        entry<SettingsRoute> {
          SettingsScene(
            onNavigateUp = ::onBack,
            onNavigateToIconPackEditor = { backStack.add(IconPacksListEditorRoute) },
            onNavigateToFontFilesEditor = { backStack.add(FontFilesEditorRoute) },
            navigateToNotificationListener = { backStack.add(NotificationListenerRoute) },
          )
        }
        entry<SaveAsPresetRoute> {
          SaveAsPresetScene(onNavigateUp = ::onBack, appWidgetId = it.appWidgetId)
        }
        entry<ImportPresetRoute> {
          ImportPresetScene(navigateUp = ::onBack, importingPresetUri = it.selectedFileUri)
        }
        entry<IconPacksListEditorRoute> {
          IconPacksScene(
            onNavigateUp = ::onBack,
            navigateToIconPackEditor = { backStack.add(IconPackEditor(it)) },
          )
        }
        entry<IconPackEditor> {
          IconPackEditorScene(onNavigateUp = ::onBack, iconPack = it.iconPack)
        }
        entry<FontFilesEditorRoute> { FontsEditorScene(onNavigateUp = ::onBack) }
        entry<NotificationListenerRoute> { NotificationListenerScene(onNavigateUp = ::onBack) }
      },
  )
}
