package io.github.sadellie.sukko.core.common

/** Schema of all saved data, both serializable and in tables. */
const val SCHEMA_VERSION = 1

/** Extension of exported presets */
const val EXPORT_EXTENSION = "sukko"

object MainWidgetAction {
  /** Update MainWidgetProvider and it's subscriptions. Use EXTRA_APPWIDGET_ID to set id */
  const val ACTION_UPDATE_WITH_SUBSCRIPTION =
    "io.github.sadellie.sukko.ACTION_UPDATE_WITH_SUBSCRIPTION"
  const val EXTRA_APPWIDGET_ID = "EXTRA_APPWIDGET_ID"
  const val EXTRA_IS_TIME_SUBSCRIBER = "EXTRA_IS_TIME_SUBSCRIBER"
  const val EXTRA_IS_BATTERY_SUBSCRIBER = "EXTRA_IS_BATTERY_SUBSCRIBER"
  const val EXTRA_IS_MEDIA_SUBSCRIBER = "EXTRA_IS_MEDIA_SUBSCRIBER"
  /** Send when clicking on a clickable layer */
  const val ACTION_CLICK = "ACTION_CLICK"
  const val EXTRA_ACTION_CLICKS_ARRAY = "EXTRA_ACTION_CLICKS_ARRAY"
}
