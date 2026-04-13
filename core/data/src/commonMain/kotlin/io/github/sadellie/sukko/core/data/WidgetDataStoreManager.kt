package io.github.sadellie.sukko.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.core.Storage
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import io.github.sadellie.sukko.core.common.defaultIODispatcher
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** Thread safe manager to access singleton widget datastore objects */
abstract class WidgetDataStoreManager {
  /** Mutex to access cache */
  val cacheMutex = Mutex()

  /** Use [cacheMutex] for any operation on cache */
  val datastoreCache = mutableMapOf<Int, DataStore<Preferences>>()

  /** Finds existing or creates a new datastore for widget with given [widgetId] */
  suspend fun getDatastore(widgetId: Int): DataStore<Preferences> =
    cacheMutex.withLock { datastoreCache.getOrPut(widgetId) { createDatastore(widgetId) } }

  private fun createDatastore(widgetId: Int): DataStore<Preferences> =
    DataStore.Builder(
        storage = createStorage(widgetId),
        context = defaultIODispatcher + SupervisorJob(),
      )
      .setCorruptionHandler(ReplaceFileCorruptionHandler { emptyPreferences() })
      .build()

  internal abstract fun createStorage(widgetId: Int): Storage<Preferences>

  internal fun datastoreFileName(widgetId: Int) = "widget_datastore_$widgetId"
}
