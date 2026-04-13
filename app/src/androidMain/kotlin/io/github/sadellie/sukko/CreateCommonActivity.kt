package io.github.sadellie.sukko

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import co.touchlab.kermit.Logger
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import io.github.sadellie.sukko.core.common.filesPath
import io.github.sadellie.sukko.core.medialistener.NotificationListener
import io.github.sadellie.sukko.core.routes.ui.MainApp
import io.github.sadellie.sukko.feature.editor.editorNavigation
import io.github.sadellie.sukko.feature.fontseditor.fontsEditorNavigation
import io.github.sadellie.sukko.feature.home.homeNavigation
import io.github.sadellie.sukko.feature.iconpackeditor.iconPackEditorNavigation
import io.github.sadellie.sukko.feature.importpreset.importPresetNavigation
import io.github.sadellie.sukko.feature.presetselector.presetSelectorNavigation
import io.github.sadellie.sukko.feature.saveaspreset.saveAsPresetNavigation
import io.github.sadellie.sukko.feature.settings.settingsNavigation
import io.github.sadellie.sukko.feature.widgetinfo.widgetInfoNavigation

/**
 * @param block Called at the start of this method. Use it to call
 *   super.[ComponentActivity.onCreate]
 */
internal fun ComponentActivity.create(initialRoute: NavKey, block: () -> Unit) {
  enableEdgeToEdge()
  block()
  val appGraph = getApplicationGraph(application)
  setContent {
    CompositionLocalProvider(LocalMetroViewModelFactory provides appGraph.metroViewModelFactory) {
      MainApp(
        onLastRoutePop = { this.finish() },
        windowsSize = calculateWindowSizeClass(this),
        imageLoader = appGraph.imageLoader,
        filesDirPath = this.filesPath,
        backStack = rememberNavBackStack(initialRoute),
      ) {
        homeNavigation(
          onAddWidget = {
            val appWidgetManager = AppWidgetManager.getInstance(this@create)
            val componentName = ComponentName(this@create, MainWidgetProviderImpl::class.java)
            if (!appWidgetManager.isRequestPinAppWidgetSupported) {
              Logger.d(tag = "HomeNavigation") { "Not allowed to pin app widget" }
            } else {
              appWidgetManager.requestPinAppWidget(componentName, null, null)
            }
          }
        )
        editorNavigation()
        widgetInfoNavigation()
        saveAsPresetNavigation()
        iconPackEditorNavigation()
        importPresetNavigation()
        fontsEditorNavigation()
        presetSelectorNavigation()
        settingsNavigation(
          openNotificationListenerPermission = {
            NotificationListener.openNotificationListenerPermission(this@create)
          }
        )
      }
    }
  }
}
