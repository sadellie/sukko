package io.github.sadellie.sukko.core.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.core.content.edit
import androidx.glance.appwidget.goAsync
import androidx.glance.session.SessionManagerScope
import androidx.glance.session.UnglanceSessionManager
import co.touchlab.kermit.Logger
import io.github.sadellie.sukko.core.common.getAppLaunchIntent
import io.github.sadellie.sukko.core.common.toViewIntent
import io.github.sadellie.sukko.core.data.WidgetDataRepository
import io.github.sadellie.sukko.core.medialistener.MediaListener
import io.github.sadellie.sukko.core.medialistener.MediaListenerService
import io.github.sadellie.sukko.core.model.basic.ClickAction
import io.github.sadellie.sukko.core.widget.MainWidgetProvider.Companion.EXTRA_APPWIDGET_ID
import kotlin.coroutines.CoroutineContext
import kotlin.time.Clock
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

    /** Update this widget and it's subscriptions. Use [EXTRA_APPWIDGET_ID] to set id */
    const val ACTION_UPDATE_WITH_SUBSCRIPTION = "ACTION_UPDATE_WITH_SUBSCRIPTION"
    const val EXTRA_APPWIDGET_ID = "EXTRA_APPWIDGET_ID"
    const val EXTRA_IS_TIME_SUBSCRIBER = "EXTRA_IS_TIME_SUBSCRIBER"
    const val EXTRA_IS_BATTERY_SUBSCRIBER = "EXTRA_IS_BATTERY_SUBSCRIBER"
    const val EXTRA_IS_MEDIA_SUBSCRIBER = "EXTRA_IS_MEDIA_SUBSCRIBER"

    fun sendBroadcast(context: Context, action: String) {
      val intent = Intent(context, MainWidgetProvider::class.java).setAction(action)
      context.sendBroadcast(intent)
    }

    fun pin(context: Context) {
      val appWidgetManager = AppWidgetManager.getInstance(context)
      val componentName = ComponentName(context, MainWidgetProvider::class.java)
      if (!appWidgetManager.isRequestPinAppWidgetSupported) {
        Logger.d(TAG) { "Not allowed to pin app widget" }
        return
      }
      appWidgetManager.requestPinAppWidget(componentName, null, null)
    }
  }

  override fun onEnabled(context: Context?) {
    super.onEnabled(context)
    context ?: return
    Logger.d(TAG) { "onEnabled called" }
    goAsync(coroutineContext) {
      setupAllListeners(context)
      val allWidgetIds = getAllWidgetIds(context)
      allWidgetIds.forEach { updateWidget(context, it) }
    }
  }

  override fun onDisabled(context: Context?) {
    super.onDisabled(context)
    context ?: return
    goAsync(coroutineContext) {
      WidgetSubscriptions.clearSubscriptions(context)
      AlarmController.cancelCurrentAlarm(context)
      MediaListenerService.stop(context)
    }
  }

  override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
    super.onDeleted(context, appWidgetIds)
    context ?: return
    appWidgetIds ?: return
    goAsync(coroutineContext) {
      WidgetSubscriptions.removeFromAllSubscribers(context, appWidgetIds)
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
    super.onUpdate(context, appWidgetManager, appWidgetIds)
    context ?: return
    appWidgetManager ?: return
    appWidgetIds ?: return
    Logger.d(TAG) { "onUpdate called" }
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
    super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
    context ?: return
    appWidgetManager ?: return
    goAsync(coroutineContext) { updateWidgetOptions(context, appWidgetId) }
  }

  override fun onReceive(context: Context?, intent: Intent?) {
    super.onReceive(context, intent)
    context ?: return
    intent ?: return
    goAsync(coroutineContext) {
      Logger.d(TAG) { "onReceive: $intent" }
      // handling custom actions, built-ins are in their respective overrides
      when (intent.action) {
        ACTION_UPDATE_WITH_SUBSCRIPTION -> receiveUpdateWithSubscription(context, intent)
        ACTION_POWER_UPDATE -> {} // reset alarm manager to update delay
        AlarmController.ACTION_ALARM_UPDATE -> receiveAlarmUpdate(context)
        MediaListener.MEDIA_METADATA_UPDATE -> receiveMediaMetadataUpdate(context)
        ACTION_CLICK -> receiveClick(context, intent)
      }
    }
  }

  private suspend fun receiveUpdateWithSubscription(context: Context, intent: Intent) {
    val appWidgetId = intent.getIntExtra(EXTRA_APPWIDGET_ID, -1)
    if (appWidgetId == -1) return
    WidgetSubscriptions.updateSubscribers(
      context = context,
      type = WidgetSubscriptions.TIME_SUBSCRIBERS,
      appWidgetId = appWidgetId,
      add = intent.getBooleanExtra(EXTRA_IS_TIME_SUBSCRIBER, false),
    )
    WidgetSubscriptions.updateSubscribers(
      context = context,
      type = WidgetSubscriptions.BATTERY_SUBSCRIBERS,
      appWidgetId = appWidgetId,
      add = intent.getBooleanExtra(EXTRA_IS_BATTERY_SUBSCRIBER, false),
    )
    WidgetSubscriptions.updateSubscribers(
      context = context,
      type = WidgetSubscriptions.MEDIA_SUBSCRIBERS,
      appWidgetId = appWidgetId,
      add = intent.getBooleanExtra(EXTRA_IS_MEDIA_SUBSCRIBER, false),
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
    WidgetSubscriptions.getSubscribers(context, WidgetSubscriptions.MEDIA_SUBSCRIBERS).forEach {
      updateMediaInfo(context, it)
    }
  }

  private suspend fun receiveClick(context: Context, intent: Intent) =
    withContext(Dispatchers.Default) {
      // get list of action and perform calls on them
      val extraActionClicksArray = intent.getStringArrayExtra(EXTRA_ACTION_CLICKS_ARRAY)
      if (extraActionClicksArray == null) {
        Logger.w(TAG) { "EXTRA_ACTION_CLICKS_ARRAY was empty" }
        return@withContext
      }

      for (clickActionJsonString in extraActionClicksArray) {
        try {
          val clickAction = Json.decodeFromString<ClickAction.Evaluated>(clickActionJsonString)
          performClickActionEvent(clickAction, context)
        } catch (e: SerializationException) {
          Logger.d(TAG, e) { "Failed to decode: $clickActionJsonString" }
        } catch (e: IllegalArgumentException) {
          Logger.d(TAG, e) { "Failed to decode: $clickActionJsonString" }
        }
      }
    }

  /** Refresh and start/stop services based on need. */
  private fun setupAllListeners(context: Context) {
    if (getAlarmSubscribers(context).isNotEmpty()) {
      AlarmController.rescheduleNewAlarm(context)
    }
    val mediaSubscribers =
      WidgetSubscriptions.getSubscribers(context, WidgetSubscriptions.MEDIA_SUBSCRIBERS)
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

  private suspend fun updateWidgetOptions(context: Context, appWidgetId: Int) {
    getOrCreateAppWidgetSession(context, appWidgetId) { session, _ ->
      session.updateWidgetOptions()
    }
  }

  private fun getAllWidgetIds(context: Context): List<Int> {
    val componentName = ComponentName(context, MainWidgetProvider::class.java)
    val appWidgetManager = AppWidgetManager.getInstance(context)
    val allWidgetIds = appWidgetManager.getAppWidgetIds(componentName).toList()
    return allWidgetIds
  }

  private fun getAlarmSubscribers(context: Context): Set<Int> {
    // alarm updates trigger time and battery subscribers
    val timeSubscribers =
      WidgetSubscriptions.getSubscribers(context, WidgetSubscriptions.TIME_SUBSCRIBERS)
    val batterySubscribers =
      WidgetSubscriptions.getSubscribers(context, WidgetSubscriptions.BATTERY_SUBSCRIBERS)
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

private object AlarmController {
  /** Send each alarm iteration. */
  const val ACTION_ALARM_UPDATE = "ACTION_ALARM_UPDATE"
  private const val ALARM_MANAGER_REQUEST_CODE = 1
  // how many minutes between alarms
  private const val ALARM_DELAY_MINUTE = 1

  fun rescheduleNewAlarm(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    cancelCurrentAlarm(context)
    val currentTimeMillis = Clock.System.now().toEpochMilliseconds()
    val nextMinuteMillis = nextMinuteStartMillis(currentTimeMillis, ALARM_DELAY_MINUTE)
    val pendingIntent = getPendingAlarmIntent(context)
    alarmManager.setExact(AlarmManager.RTC, nextMinuteMillis, pendingIntent)
  }

  fun cancelCurrentAlarm(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val pendingIntent = getPendingAlarmIntent(context)
    alarmManager.cancel(pendingIntent)
  }

  private fun getPendingAlarmIntent(context: Context): PendingIntent {
    val intent = Intent(context, MainWidgetProvider::class.java).setAction(ACTION_ALARM_UPDATE)
    val pendingIntent =
      PendingIntent.getBroadcast(
        context.applicationContext,
        ALARM_MANAGER_REQUEST_CODE,
        intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
      )
    return pendingIntent
  }
}

private object WidgetSubscriptions {
  private const val PREFS_FILE_NAME = "widget_subscriptions"

  /** time and battery updates */
  const val TIME_SUBSCRIBERS = "TIME_SUBSCRIBERS"
  const val MEDIA_SUBSCRIBERS = "MEDIA_SUBSCRIBERS"
  const val BATTERY_SUBSCRIBERS = "BATTERY_SUBSCRIBERS"

  fun getSubscribers(context: Context, type: String) = getSubscribers(prefs(context), type)

  fun clearSubscriptions(context: Context) = prefs(context).edit { clear() }

  fun updateSubscribers(context: Context, type: String, appWidgetId: Int, add: Boolean) =
    if (add) {
      addToSubscribers(context, type, appWidgetId)
    } else {
      removeFromSubscribers(context, type, appWidgetId)
    }

  /** Unsubscribe [appWidgetIds] from all updates */
  fun removeFromAllSubscribers(context: Context, appWidgetIds: IntArray) {
    val prefs = prefs(context)
    val appWidgetIdsSet = appWidgetIds.toSet()
    val time = getSubscribers(prefs, TIME_SUBSCRIBERS).minus(appWidgetIdsSet).toSetOfStrings()
    val battery = getSubscribers(prefs, BATTERY_SUBSCRIBERS).minus(appWidgetIdsSet).toSetOfStrings()
    val media = getSubscribers(prefs, MEDIA_SUBSCRIBERS).minus(appWidgetIdsSet).toSetOfStrings()
    prefs.edit {
      putStringSet(TIME_SUBSCRIBERS, time)
      putStringSet(BATTERY_SUBSCRIBERS, battery)
      putStringSet(MEDIA_SUBSCRIBERS, media)
    }
  }

  private fun addToSubscribers(context: Context, type: String, appWidgetId: Int) {
    val prefs = prefs(context)
    val updatedSubscribers = getSubscribers(prefs, type).plus(appWidgetId).toSetOfStrings()
    prefs.edit { putStringSet(type, updatedSubscribers) }
  }

  private fun removeFromSubscribers(context: Context, type: String, appWidgetId: Int) {
    val prefs = prefs(context)
    val updatedSubscribers = getSubscribers(prefs, type).minus(appWidgetId).toSetOfStrings()
    prefs.edit { putStringSet(type, updatedSubscribers) }
  }

  private fun getSubscribers(prefs: SharedPreferences, key: String): Set<Int> {
    val subscribers = prefs.getStringSet(key, emptySet())?.mapNotNull { it.toIntOrNull() }?.toSet()
    return subscribers ?: emptySet()
  }

  private fun prefs(context: Context) =
    context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)

  private fun Set<Int>.toSetOfStrings(): Set<String> = this.map { it.toString() }.toSet()
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
