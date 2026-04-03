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
