package io.github.sadellie.sukko

import android.appwidget.AppWidgetManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import co.touchlab.kermit.Logger
import io.github.sadellie.sukko.core.routes.CommonRoute

class ReconfigureActivity : AppCompatActivity() {
  private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

  companion object {
    private const val TAG = "ReconfigureActivity"
  }

  @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    setResult(RESULT_OK)
    appWidgetId = extractAppWidgetId()
    // reconfiguration is optional. if user leaves this activity without changes, still report ok
    if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
      Logger.e(tag = TAG) { "Invalid widget id" }
      finish()
      return
    }
    create(initialRoute = CommonRoute.EditorRoute(appWidgetId)) {
      super.onCreate(savedInstanceState)
    }
  }

  private fun extractAppWidgetId() =
    intent
      ?.extras
      ?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
      ?: AppWidgetManager.INVALID_APPWIDGET_ID
}
