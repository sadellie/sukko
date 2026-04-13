package io.github.sadellie.sukko.core.data

import androidx.datastore.core.Storage
import androidx.datastore.core.okio.WebLocalStorage
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferencesSerializer

internal class WidgetDataStoreManagerImpl() : WidgetDataStoreManager() {
  override fun createStorage(widgetId: Int): Storage<Preferences> =
    WebLocalStorage(PreferencesSerializer, datastoreFileName(widgetId))
}
