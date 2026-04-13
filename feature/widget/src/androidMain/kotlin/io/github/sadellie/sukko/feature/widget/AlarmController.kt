package io.github.sadellie.sukko.feature.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import kotlin.time.Clock

internal object AlarmController {
  /** Send each alarm iteration. */
  const val ACTION_ALARM_UPDATE = "ACTION_ALARM_UPDATE"
  private const val ALARM_MANAGER_REQUEST_CODE = 1
  // how many minutes between alarms
  private const val ALARM_DELAY_MINUTE = 1

  fun rescheduleNewAlarm(context: Context, widgetProviderIntent: Intent) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    cancelCurrentAlarm(context, widgetProviderIntent)
    val currentTimeMillis = Clock.System.now().toEpochMilliseconds()
    val nextMinuteMillis = nextMinuteStartMillis(currentTimeMillis, ALARM_DELAY_MINUTE)
    val pendingIntent = getPendingAlarmIntent(context, widgetProviderIntent)
    alarmManager.setExact(AlarmManager.RTC, nextMinuteMillis, pendingIntent)
  }

  fun cancelCurrentAlarm(context: Context, widgetProviderIntent: Intent) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val pendingIntent = getPendingAlarmIntent(context, widgetProviderIntent)
    alarmManager.cancel(pendingIntent)
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

  private fun getPendingAlarmIntent(context: Context, widgetProviderIntent: Intent): PendingIntent {
    val intent = widgetProviderIntent.setAction(ACTION_ALARM_UPDATE)
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
