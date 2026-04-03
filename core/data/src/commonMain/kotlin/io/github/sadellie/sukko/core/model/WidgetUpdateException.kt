package io.github.sadellie.sukko.core.model

sealed class WidgetUpdateException : Exception() {
  class MissingNotificationListener : WidgetUpdateException()
}
