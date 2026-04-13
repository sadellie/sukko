package io.github.sadellie.sukko.core.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import io.github.sadellie.sukko.core.data.WidgetSubscriptionsRepository.Companion.BATTERY_SUBSCRIBERS
import io.github.sadellie.sukko.core.data.WidgetSubscriptionsRepository.Companion.MEDIA_SUBSCRIBERS
import io.github.sadellie.sukko.core.data.WidgetSubscriptionsRepository.Companion.TIME_SUBSCRIBERS
import io.github.sadellie.sukko.core.model.WidgetSubscriptionInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class WidgetSubscriptionsRepositoryImpl(context: Context) : WidgetSubscriptionsRepository {
  companion object {
    private const val PREFS_FILE_NAME = "widget_subscriptions"
  }

  private val prefs = context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)

  override fun getSubscribers(type: String) = getSubscribers(prefs, type)

  override suspend fun getSubscriptionInfo(appWidgetId: Int): WidgetSubscriptionInfo =
    withContext(Dispatchers.IO) {
      WidgetSubscriptionInfo(
        isTime = appWidgetId in getSubscribers(TIME_SUBSCRIBERS),
        isMedia = appWidgetId in getSubscribers(MEDIA_SUBSCRIBERS),
        isBattery = appWidgetId in getSubscribers(BATTERY_SUBSCRIBERS),
      )
    }

  override fun clearSubscriptions() = prefs.edit { clear() }

  override fun updateSubscribers(type: String, appWidgetId: Int, add: Boolean) =
    if (add) {
      addToSubscribers(type, appWidgetId)
    } else {
      removeFromSubscribers(type, appWidgetId)
    }

  /** Unsubscribe [appWidgetIds] from all updates */
  override fun removeFromAllSubscribers(appWidgetIds: IntArray) {
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

  private fun addToSubscribers(type: String, appWidgetId: Int) {
    val updatedSubscribers = getSubscribers(prefs, type).plus(appWidgetId).toSetOfStrings()
    prefs.edit { putStringSet(type, updatedSubscribers) }
  }

  private fun removeFromSubscribers(type: String, appWidgetId: Int) {
    val updatedSubscribers = getSubscribers(prefs, type).minus(appWidgetId).toSetOfStrings()
    prefs.edit { putStringSet(type, updatedSubscribers) }
  }

  private fun getSubscribers(prefs: SharedPreferences, key: String): Set<Int> {
    val widgetIds: Set<String> = prefs.getStringSet(key, null) ?: return emptySet()
    val subscribers = widgetIds.mapNotNull { it.toIntOrNull() }.toSet()
    return subscribers
  }

  private fun Set<Int>.toSetOfStrings(): Set<String> = this.map { it.toString() }.toSet()
}
