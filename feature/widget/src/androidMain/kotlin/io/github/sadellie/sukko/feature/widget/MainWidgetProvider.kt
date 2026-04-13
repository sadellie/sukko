package io.github.sadellie.sukko.feature.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.glance.appwidget.goAsync
import androidx.glance.session.SessionManagerScope
import androidx.glance.session.UnglanceSessionManager
import co.touchlab.kermit.Logger
import coil3.ImageLoader
import io.github.sadellie.sukko.core.common.MainWidgetAction
import io.github.sadellie.sukko.core.data.LayerEvaluator
import io.github.sadellie.sukko.core.data.ScriptableEvaluator
import io.github.sadellie.sukko.core.data.WidgetDataRepository
import io.github.sadellie.sukko.core.data.WidgetSubscriptionsRepository
import io.github.sadellie.sukko.core.medialistener.MediaListener
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

abstract class MainWidgetProvider : AppWidgetProvider() {
  private val coroutineContext: CoroutineContext = Dispatchers.Default

  abstract fun getMediaListener(context: Context): MediaListener

  abstract fun getImageLoader(context: Context): ImageLoader

  abstract fun getWidgetDataRepository(context: Context): WidgetDataRepository

  abstract fun startMediaListenerService(context: Context)

  abstract fun stopMediaListenerService(context: Context)

  abstract fun getWidgetSubscriptionsRepository(context: Context): WidgetSubscriptionsRepository

  abstract fun getLayerEvaluatorFactory(context: Context): LayerEvaluator.LayerEvaluatorFactory

  abstract fun getScriptableEvaluatorFactory(
    context: Context
  ): ScriptableEvaluator.ScriptableEvaluatorFactory

  companion object {
    private const val TAG = "MainWidgetProvider"

    /** Send when power state changes. */
    const val ACTION_POWER_UPDATE = "ACTION_POWER_UPDATE"
  }

  override fun onEnabled(context: Context?) {
    context ?: return
    Logger.d(tag = TAG) { "onEnabled called" }
    goAsync(coroutineContext) {
      setupAllListeners(context)
      val allWidgetIds = getAllWidgetIds(context)
      allWidgetIds.forEach { updateWidget(context, it) }
    }
  }

  override fun onDisabled(context: Context?) {
    context ?: return
    goAsync(coroutineContext) {
      getWidgetSubscriptionsRepository(context).clearSubscriptions()
      AlarmController.cancelCurrentAlarm(context, getWidgetProviderIntent(context))
      stopMediaListenerService(context)
    }
  }

  override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
    context ?: return
    appWidgetIds ?: return
    goAsync(coroutineContext) {
      getWidgetSubscriptionsRepository(context).removeFromAllSubscribers(appWidgetIds)
      appWidgetIds.forEach { getWidgetDataRepository(context).delete(it) }
      setupAllListeners(context)
    }
  }

  override fun onUpdate(
    context: Context?,
    appWidgetManager: AppWidgetManager?,
    appWidgetIds: IntArray?,
  ) {
    context ?: return
    appWidgetManager ?: return
    appWidgetIds ?: return
    Logger.d(tag = TAG) { "onUpdate called" }
    goAsync(coroutineContext) {
      setupAllListeners(context)
      appWidgetIds.forEach { updateWidget(context, it) }
    }
  }

  override fun onAppWidgetOptionsChanged(
    context: Context?,
    appWidgetManager: AppWidgetManager?,
    appWidgetId: Int,
    newOptions: Bundle?,
  ) {
    context ?: return
    appWidgetManager ?: return
    newOptions ?: return
    goAsync(coroutineContext) { updateWidgetOptions(context, appWidgetId, newOptions) }
  }

  override fun onReceive(context: Context?, intent: Intent?) {
    super.onReceive(context, intent)
    context ?: return
    intent ?: return
    goAsync(coroutineContext) {
      Logger.d(tag = TAG) { "onReceive: $intent" }
      // handling custom actions, built-ins are in their respective overrides
      when (intent.action) {
        MainWidgetAction.ACTION_UPDATE_WITH_SUBSCRIPTION ->
          receiveUpdateWithSubscription(context, intent)
        ACTION_POWER_UPDATE -> {} // reset alarm manager to update delay
        AlarmController.ACTION_ALARM_UPDATE -> receiveAlarmUpdate(context)
        MediaListener.MEDIA_METADATA_UPDATE -> receiveMediaMetadataUpdate(context)
        MainWidgetAction.ACTION_CLICK -> receiveClick(context, intent)
      }
    }
  }

  private suspend fun receiveUpdateWithSubscription(context: Context, intent: Intent) {
    val appWidgetId = intent.getIntExtra(MainWidgetAction.EXTRA_APPWIDGET_ID, -1)
    if (appWidgetId == -1) return
    val widgetSubscriptions = getWidgetSubscriptionsRepository(context)
    widgetSubscriptions.updateSubscribers(
      type = WidgetSubscriptionsRepository.TIME_SUBSCRIBERS,
      appWidgetId = appWidgetId,
      add = intent.getBooleanExtra(MainWidgetAction.EXTRA_IS_TIME_SUBSCRIBER, false),
    )
    widgetSubscriptions.updateSubscribers(
      type = WidgetSubscriptionsRepository.BATTERY_SUBSCRIBERS,
      appWidgetId = appWidgetId,
      add = intent.getBooleanExtra(MainWidgetAction.EXTRA_IS_BATTERY_SUBSCRIBER, false),
    )
    widgetSubscriptions.updateSubscribers(
      type = WidgetSubscriptionsRepository.MEDIA_SUBSCRIBERS,
      appWidgetId = appWidgetId,
      add = intent.getBooleanExtra(MainWidgetAction.EXTRA_IS_MEDIA_SUBSCRIBER, false),
    )
    updateWidget(context, appWidgetId)
    setupAllListeners(context)
  }

  private suspend fun receiveAlarmUpdate(context: Context) {
    // alarm updates trigger time and battery subscribers
    val alarmSubscribers = getAlarmSubscribers(context)
    if (alarmSubscribers.isNotEmpty()) {
      alarmSubscribers.forEach { updateFromAlarm(context, it) }
      AlarmController.rescheduleNewAlarm(context, getWidgetProviderIntent(context))
    }
  }

  private suspend fun receiveMediaMetadataUpdate(context: Context) {
    getWidgetSubscriptionsRepository(context)
      .getSubscribers(WidgetSubscriptionsRepository.MEDIA_SUBSCRIBERS)
      .forEach { updateMediaInfo(context, it) }
  }

  private suspend fun receiveClick(context: Context, intent: Intent) {
    // get list of action and perform calls on them
    val extraActionClicksArray =
      intent.getStringArrayExtra(MainWidgetAction.EXTRA_ACTION_CLICKS_ARRAY)
    val appWidgetId = intent.getIntExtra(MainWidgetAction.EXTRA_APPWIDGET_ID, -1)
    if (appWidgetId == -1) {
      Logger.w(tag = TAG) { "EXTRA_APPWIDGET_ID was empty" }
      return
    }
    if (extraActionClicksArray == null) {
      Logger.w(tag = TAG) { "EXTRA_ACTION_CLICKS_ARRAY was empty" }
      return
    }
    getOrCreateAppWidgetSession(context, appWidgetId) { session, _ ->
      session.updateClickActions(extraActionClicksArray)
    }
  }

  /** Refresh and start/stop services based on need. */
  private fun setupAllListeners(context: Context) {
    if (getAlarmSubscribers(context).isNotEmpty()) {
      AlarmController.rescheduleNewAlarm(context, getWidgetProviderIntent(context))
    }
    val mediaSubscribers =
      getWidgetSubscriptionsRepository(context)
        .getSubscribers(WidgetSubscriptionsRepository.MEDIA_SUBSCRIBERS)
    if (mediaSubscribers.isNotEmpty()) {
      // start service only when needed
      startMediaListenerService(context)
    } else {
      stopMediaListenerService(context)
    }
  }

  private suspend fun <T> getOrCreateAppWidgetSession(
    context: Context,
    appWidgetId: Int,
    block: suspend SessionManagerScope.(AppWidgetSession, Boolean) -> T,
  ): T =
    UnglanceSessionManager.runWithLock {
      val wasRunning = isSessionRunning(context, appWidgetId.toString())
      if (!wasRunning) {
        startSession(
          context = context,
          session =
            AppWidgetSession(
              appWidgetId = appWidgetId,
              widgetDataRepository = getWidgetDataRepository(context),
              mediaListener = getMediaListener(context),
              imageLoader = getImageLoader(context),
              widgetProviderIntent = getWidgetProviderIntent(context),
              scriptableEvaluatorFactory = getScriptableEvaluatorFactory(context),
              layerEvaluatorFactory = getLayerEvaluatorFactory(context),
            ),
        )
      }
      val session = getSession(appWidgetId.toString()) as AppWidgetSession
      return@runWithLock block(session, wasRunning)
    }

  private suspend fun updateWidget(context: Context, appWidgetId: Int) {
    getOrCreateAppWidgetSession(context, appWidgetId) { session, _ -> session.updateWidget() }
  }

  private suspend fun updateFromAlarm(context: Context, appWidgetId: Int) {
    getOrCreateAppWidgetSession(context, appWidgetId) { session, _ -> session.updateFromAlarm() }
  }

  private suspend fun updateMediaInfo(context: Context, appWidgetId: Int) {
    getOrCreateAppWidgetSession(context, appWidgetId) { session, _ -> session.updateMediaInfo() }
  }

  private suspend fun updateWidgetOptions(context: Context, appWidgetId: Int, newOptions: Bundle) {
    getOrCreateAppWidgetSession(context, appWidgetId) { session, _ ->
      session.updateWidgetOptions(newOptions)
    }
  }

  abstract fun getAllWidgetIds(context: Context): List<Int>

  abstract fun getWidgetProviderIntent(context: Context): Intent

  private fun getAlarmSubscribers(context: Context): Set<Int> {
    val widgetSubscriptions = getWidgetSubscriptionsRepository(context)
    // alarm updates trigger time and battery subscribers
    val timeSubscribers =
      widgetSubscriptions.getSubscribers(WidgetSubscriptionsRepository.TIME_SUBSCRIBERS)
    val batterySubscribers =
      widgetSubscriptions.getSubscribers(WidgetSubscriptionsRepository.BATTERY_SUBSCRIBERS)
    return timeSubscribers + batterySubscribers
  }
}
