package io.github.sadellie.sukko.core.data

import io.github.sadellie.sukko.core.model.WidgetSubscriptionInfo

interface WidgetSubscriptionsRepository {
  companion object {
    /** time and battery updates */
    const val TIME_SUBSCRIBERS = "TIME_SUBSCRIBERS"
    const val MEDIA_SUBSCRIBERS = "MEDIA_SUBSCRIBERS"
    const val BATTERY_SUBSCRIBERS = "BATTERY_SUBSCRIBERS"
  }

  fun getSubscribers(type: String): Set<Int>

  suspend fun getSubscriptionInfo(appWidgetId: Int): WidgetSubscriptionInfo

  fun clearSubscriptions()

  fun updateSubscribers(type: String, appWidgetId: Int, add: Boolean)

  fun removeFromAllSubscribers(appWidgetIds: IntArray)
}
