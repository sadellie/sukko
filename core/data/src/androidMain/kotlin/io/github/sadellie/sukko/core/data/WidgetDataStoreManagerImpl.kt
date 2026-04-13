package io.github.sadellie.sukko.core.data

import android.content.Context
import androidx.datastore.core.FileStorage
import androidx.datastore.preferences.core.PreferencesFileSerializer
import androidx.datastore.preferences.preferencesDataStoreFile

internal class WidgetDataStoreManagerImpl(private val context: Context) : WidgetDataStoreManager() {
  override fun createStorage(widgetId: Int) =
    FileStorage(
      serializer = PreferencesFileSerializer,
      produceFile = { context.preferencesDataStoreFile(datastoreFileName(widgetId)) },
    )
}
