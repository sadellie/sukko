package io.github.sadellie.sukko.feature.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.glance.appwidget.goAsync
import androidx.glance.session.SessionManagerScope
import androidx.glance.session.UnglanceSessionManager
import co.touchlab.kermit.Logger
import io.github.sadellie.sukko.core.common.MainWidgetAction
import io.github.sadellie.sukko.core.common.getAppLaunchIntent
import io.github.sadellie.sukko.core.common.toViewIntent
import io.github.sadellie.sukko.core.data.WidgetDataRepository
import io.github.sadellie.sukko.core.data.WidgetSubscriptionsRepository
import io.github.sadellie.sukko.core.data.WidgetSubscriptionsRepositoryImpl
import io.github.sadellie.sukko.core.medialistener.MediaListener
import io.github.sadellie.sukko.core.medialistener.MediaListenerService
import io.github.sadellie.sukko.core.model.basic.ClickAction
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainWidgetProvider : AppWidgetProvider(), KoinComponent {
  private val coroutineContext: CoroutineContext = Dispatchers.Default
  private val mediaListener by inject<MediaListener>()

  companion object {
    private const val TAG = "MainWidgetProvider"

    /** Send when power state changes. */
    const val ACTION_POWER_UPDATE = "ACTION_POWER_UPDATE"

    /** Send when clicking on a clickable layer */
    const val ACTION_CLICK = "ACTION_CLICK"
    const val EXTRA_ACTION_CLICKS_ARRAY = "EXTRA_ACTION_CLICKS_ARRAY"

    fun sendBroadcast(context: Context, action: String) {
      val intent = Intent(context, MainWidgetProvider::class.java).setAction(action)
      context.sendBroadcast(intent)
    }

    fun pin(context: Context) {
      val appWidgetManager = AppWidgetManager.getInstance(context)
      val componentName = ComponentName(context, MainWidgetProvider::class.java)
      if (!appWidgetManager.isRequestPinAppWidgetSupported) {
        Logger.d(tag = TAG) { "Not allowed to pin app widget" }
        return
      }
      appWidgetManager.requestPinAppWidget(componentName, null, null)
    }
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
      WidgetSubscriptionsRepositoryImpl(context).clearSubscriptions()
      AlarmController.cancelCurrentAlarm(context)
      MediaListenerService.stop(context)
    }
  }

  override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
    context ?: return
    appWidgetIds ?: return
    goAsync(coroutineContext) {
      WidgetSubscriptionsRepositoryImpl(context).removeFromAllSubscribers(appWidgetIds)
      val repo by inject<WidgetDataRepository>()
      appWidgetIds.forEach { repo.delete(it) }
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
        ACTION_CLICK -> receiveClick(context, intent)
      }
    }
  }

  private suspend fun receiveUpdateWithSubscription(context: Context, intent: Intent) {
    val appWidgetId = intent.getIntExtra(MainWidgetAction.EXTRA_APPWIDGET_ID, -1)
    if (appWidgetId == -1) return
    val widgetSubscriptions = WidgetSubscriptionsRepositoryImpl(context)
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
      AlarmController.rescheduleNewAlarm(context)
    }
  }

  private suspend fun receiveMediaMetadataUpdate(context: Context) {
    WidgetSubscriptionsRepositoryImpl(context)
      .getSubscribers(WidgetSubscriptionsRepository.MEDIA_SUBSCRIBERS)
      .forEach { updateMediaInfo(context, it) }
  }

  private suspend fun receiveClick(context: Context, intent: Intent) =
    withContext(Dispatchers.Default) {
      // get list of action and perform calls on them
      val extraActionClicksArray = intent.getStringArrayExtra(EXTRA_ACTION_CLICKS_ARRAY)
      if (extraActionClicksArray == null) {
        Logger.w(tag = TAG) { "EXTRA_ACTION_CLICKS_ARRAY was empty" }
        return@withContext
      }

      for (clickActionJsonString in extraActionClicksArray) {
        try {
          val clickAction = Json.decodeFromString<ClickAction.Evaluated>(clickActionJsonString)
          performClickActionEvent(clickAction, context)
        } catch (e: SerializationException) {
          Logger.d(throwable = e, tag = TAG) { "Failed to decode: $clickActionJsonString" }
        } catch (e: IllegalArgumentException) {
          Logger.d(throwable = e, tag = TAG) { "Failed to decode: $clickActionJsonString" }
        }
      }
    }

  /** Refresh and start/stop services based on need. */
  private fun setupAllListeners(context: Context) {
    if (getAlarmSubscribers(context).isNotEmpty()) {
      AlarmController.rescheduleNewAlarm(context)
    }
    val mediaSubscribers =
      WidgetSubscriptionsRepositoryImpl(context)
        .getSubscribers(WidgetSubscriptionsRepository.MEDIA_SUBSCRIBERS)
    if (mediaSubscribers.isNotEmpty()) {
      // start service only when needed
      MediaListenerService.start(context)
    } else {
      MediaListenerService.stop(context)
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
        startSession(context, AppWidgetSession(appWidgetId))
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

  private fun getAllWidgetIds(context: Context): List<Int> {
    val componentName = ComponentName(context, MainWidgetProvider::class.java)
    val appWidgetManager = AppWidgetManager.getInstance(context)
    val allWidgetIds = appWidgetManager.getAppWidgetIds(componentName).toList()
    return allWidgetIds
  }

  private fun getAlarmSubscribers(context: Context): Set<Int> {
    val widgetSubscriptions = WidgetSubscriptionsRepositoryImpl(context)
    // alarm updates trigger time and battery subscribers
    val timeSubscribers =
      widgetSubscriptions.getSubscribers(WidgetSubscriptionsRepository.TIME_SUBSCRIBERS)
    val batterySubscribers =
      widgetSubscriptions.getSubscribers(WidgetSubscriptionsRepository.BATTERY_SUBSCRIBERS)
    return timeSubscribers + batterySubscribers
  }

  private fun performClickActionEvent(clickAction: ClickAction.Evaluated, context: Context) {
    when (clickAction) {
      is ClickAction.Evaluated.OpenLink -> {
        val intent = clickAction.url.toViewIntent() ?: return
        context.startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
      }
      is ClickAction.LaunchApp -> {
        val packageName = clickAction.packageName ?: return
        val appLaunchIntent = context.getAppLaunchIntent(packageName) ?: return
        context.startActivity(appLaunchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
      }
      is ClickAction.MediaPause -> mediaListener.pause()
      is ClickAction.MediaPlay -> mediaListener.play()
      is ClickAction.MediaSkipToNext -> mediaListener.skipToNext()
      is ClickAction.MediaSkipToPrevious -> mediaListener.skipToPrevious()
      is ClickAction.MediaOpenPlayer -> mediaListener.openPlayer()
    }
  }
}

/**
 * Calculate next millis for next [minute].
 *
 * Example 1:
 * - current time is 14:15
 * - [minute] is 1
 * - will return millis for 14:16
 *
 * Example 2:
 * - current time is 14:15
 * - [minute] is 15
 * - will return millis for 14:30
 */
internal fun nextMinuteStartMillis(currentTimeMillis: Long, minute: Int): Long {
  // lose seconds on purpose
  val currentMinute = currentTimeMillis / 60_000
  val nextMinute = currentMinute + minute
  val nextMillis = nextMinute * 60_000
  return nextMillis
}
