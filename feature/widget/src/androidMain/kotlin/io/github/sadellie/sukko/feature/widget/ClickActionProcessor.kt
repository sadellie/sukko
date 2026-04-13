package io.github.sadellie.sukko.feature.widget

import android.content.Context
import android.content.Intent
import co.touchlab.kermit.Logger
import io.github.sadellie.sukko.core.common.getAppLaunchIntent
import io.github.sadellie.sukko.core.common.toViewIntent
import io.github.sadellie.sukko.core.data.ScriptableEvaluator
import io.github.sadellie.sukko.core.data.WidgetDataRepository
import io.github.sadellie.sukko.core.medialistener.MediaListener
import io.github.sadellie.sukko.core.model.basic.ClickAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class ClickActionProcessor(
  private val mediaListener: MediaListener,
  private val widgetDataRepository: WidgetDataRepository,
  private val appWidgetId: Int,
  private val scriptableEvaluatorFactory: ScriptableEvaluator.ScriptableEvaluatorFactory,
) {
  /**
   * @param afterScripCallback Called after script action is done processing (data in widget needs
   *   to invalidated, i.e, reload widget entirely)
   */
  suspend fun process(
    clickActionsJsons: Array<String>,
    context: Context,
    afterScripCallback: suspend () -> Unit,
  ) {
    if (clickActionsJsons.isEmpty()) {
      Logger.w(tag = TAG) { "clickActionsJsons were empty" }
      return
    }
    clickActionsJsons.forEach { clickActionJson ->
      try {
        when (val clickAction = decodeClickAction(clickActionJson)) {
          is ClickAction.Evaluated.OpenLink -> processOpenLink(clickAction, context)
          is ClickAction.LaunchApp -> processLaunchApp(clickAction, context)
          is ClickAction.MediaOpenPlayer -> mediaListener.openPlayer()
          is ClickAction.MediaPause -> mediaListener.pause()
          is ClickAction.MediaPlay -> mediaListener.play()
          is ClickAction.MediaSkipToNext -> mediaListener.skipToNext()
          is ClickAction.MediaSkipToPrevious -> mediaListener.skipToPrevious()
          is ClickAction.RunScript -> processRunScript(clickAction, afterScripCallback)
        }
      } catch (e: Exception) {
        Logger.e(e, TAG) { "Failed to process: $clickActionJson" }
      }
    }
  }

  private fun processOpenLink(clickAction: ClickAction.Evaluated.OpenLink, context: Context) {
    val intent = clickAction.url.toViewIntent() ?: return
    context.startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
  }

  private fun processLaunchApp(clickAction: ClickAction.LaunchApp, context: Context) {
    val packageName = clickAction.packageName ?: return
    val appLaunchIntent = context.getAppLaunchIntent(packageName) ?: return
    context.startActivity(appLaunchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
  }

  private suspend fun processRunScript(
    clickAction: ClickAction.RunScript,
    afterScripCallback: suspend () -> Unit,
  ) {
    val widgetData =
      widgetDataRepository.loadByAppWidgetId(appWidgetId) ?: error("Widget not found $appWidgetId")
    val scriptableEvaluator =
      scriptableEvaluatorFactory.create(globals = widgetData.globals, widgetId = appWidgetId)
    scriptableEvaluator.evaluateScriptWithFormattedResult(
      script = clickAction.script,
      readOnly = false,
      enableGlobalOverridesAPI = true,
    )
    afterScripCallback()
  }

  private suspend fun decodeClickAction(clickActionJson: String) =
    withContext(Dispatchers.Default) {
      Json.decodeFromString<ClickAction.Evaluated>(clickActionJson)
    }
}

private const val TAG = "ClickActionProcessor"
