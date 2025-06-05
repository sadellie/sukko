package io.github.sadellie.sukko

import android.appwidget.AppWidgetManager
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import co.touchlab.kermit.Logger
import coil3.ImageLoader
import io.github.sadellie.sukko.core.common.filesPath
import io.github.sadellie.sukko.feature.editor.EditorRoute
import org.koin.android.ext.android.get

class ReconfigureActivity : AppCompatActivity() {
  private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

  companion object {
    private const val TAG = "ReconfigureActivity"
  }

  @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    enableEdgeToEdge()
    super.onCreate(savedInstanceState)
    Logger.d(TAG) { "Launched ReconfigureActivity" }

    // reconfiguration is optional. if user leaves this activity without changes, still report ok
    setResult(RESULT_OK)
    appWidgetId = extractAppWidgetId()
    if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
      Logger.e(TAG) { "Invalid widget id" }
      finish()
      return
    }

    setContent {
      MainApp(
        onLastRoutePop = this::finish,
        windowsSize = calculateWindowSizeClass(this),
        imageLoader = get<ImageLoader>(),
        filesDirPath = this.filesPath,
        initialRoute = EditorRoute(appWidgetId),
      )
    }
  }

  private fun extractAppWidgetId() =
    intent
      ?.extras
      ?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
      ?: AppWidgetManager.INVALID_APPWIDGET_ID
}
