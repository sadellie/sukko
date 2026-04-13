package io.github.sadellie.sukko.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first

/**
 * A storage for current values of [io.github.sadellie.sukko.core.model.basic.GlobalValue]. Caches
 * datastore values for subsequent GET method calls (not SAVE methods).
 */
interface GlobalCurrentValueStore {
  /**
   * Retrieves the current string value for the
   * [io.github.sadellie.sukko.core.model.basic.GlobalValue] identified by [id].
   *
   * @param id The unique identifier of the global value.
   * @return The string value if found; otherwise, `null`.
   */
  suspend fun getCurrentStringValue(id: Long): String?

  /**
   * Retrieves the current boolean value for the
   * [io.github.sadellie.sukko.core.model.basic.GlobalValue] identified by [id].
   *
   * @param id The unique identifier of the global value.
   * @return The boolean value if found; otherwise, `null`.
   */
  suspend fun getCurrentBooleanValue(id: Long): Boolean?

  /**
   * Retrieves the current double value for the
   * [io.github.sadellie.sukko.core.model.basic.GlobalValue] identified by [id].
   *
   * @param id The unique identifier of the global value.
   * @return The double value if found; otherwise, `null`.
   */
  suspend fun getCurrentDoubleValue(id: Long): Double?

  /**
   * Saves the given string value for the [io.github.sadellie.sukko.core.model.basic.GlobalValue]
   * identified by [id]. Does not update the cache.
   *
   * @param id The unique identifier of the global value.
   * @param value The string value to save.
   */
  suspend fun saveStringValue(id: Long, value: String)

  /**
   * Saves the given boolean value for the [io.github.sadellie.sukko.core.model.basic.GlobalValue]
   * identified by [id]. Does not update the cache.
   *
   * @param id The unique identifier of the global value.
   * @param value The boolean value to save.
   */
  suspend fun saveBooleanValue(id: Long, value: Boolean)

  /**
   * Saves the given double value for the [io.github.sadellie.sukko.core.model.basic.GlobalValue]
   * identified by [id]. Does not update the cache.
   *
   * @param id The unique identifier of the global value.
   * @param value The double value to save.
   */
  suspend fun saveDoubleValue(id: Long, value: Double)

  /** Clear cached values by recreating this object */
  fun clearCache(): GlobalCurrentValueStore
}

class GlobalCurrentValueStoreImpl(
  private val widgetId: Int,
  private val widgetDataStoreManager: WidgetDataStoreManager,
) : GlobalCurrentValueStore {
  private var cachedPrefs: Preferences? = null
  private var cachedDataStore: DataStore<Preferences>? = null

  override fun clearCache() = GlobalCurrentValueStoreImpl(widgetId, widgetDataStoreManager)

  override suspend fun getCurrentStringValue(id: Long): String? =
    extractValue(preferencesKeyForString(id))

  override suspend fun getCurrentBooleanValue(id: Long): Boolean? =
    extractValue(preferencesKeyForBoolean(id))

  override suspend fun getCurrentDoubleValue(id: Long): Double? =
    extractValue(preferencesKeyForDouble(id))

  override suspend fun saveStringValue(id: Long, value: String) {
    try {
      val dataStore = cachedDataStore ?: loadDataStore()
      dataStore.edit { prefs -> prefs[preferencesKeyForString(id)] = value }
      Logger.d(tag = TAG) { "Saved string value for id $id" }
    } catch (e: IOException) {
      Logger.e(e, tag = TAG) { "Failed to save string value for id $id" }
    }
  }

  override suspend fun saveBooleanValue(id: Long, value: Boolean) {
    try {
      val dataStore = cachedDataStore ?: loadDataStore()
      dataStore.edit { prefs -> prefs[preferencesKeyForBoolean(id)] = value }
      Logger.d(tag = TAG) { "Saved boolean value for id $id" }
    } catch (e: IOException) {
      Logger.e(e, tag = TAG) { "Failed to save boolean value for id $id" }
    }
  }

  override suspend fun saveDoubleValue(id: Long, value: Double) {
    try {
      val dataStore = cachedDataStore ?: loadDataStore()
      dataStore.edit { prefs -> prefs[preferencesKeyForDouble(id)] = value }
      Logger.d(tag = TAG) { "Saved double value for id $id" }
    } catch (e: IOException) {
      Logger.e(e, tag = TAG) { "Failed to save double value for id $id" }
    }
  }

  private fun preferencesKeyForString(id: Long) = stringPreferencesKey("global_string_$id")

  private fun preferencesKeyForBoolean(id: Long) = booleanPreferencesKey("global_boolean_$id")

  private fun preferencesKeyForDouble(id: Long) = doublePreferencesKey("global_double_$id")

  private suspend fun <T> extractValue(key: Preferences.Key<T>): T? {
    if (cachedPrefs == null) loadCache()
    return cachedPrefs?.get(key)
  }

  private suspend fun loadCache() {
    val dataStore = cachedDataStore ?: loadDataStore()
    cachedPrefs =
      dataStore.data
        .catch { exception ->
          Logger.e(exception, tag = TAG) { "Failed to load data" }
          if (exception is IOException) {
            emit(emptyPreferences())
          } else {
            throw exception
          }
        }
        .first()
  }

  private suspend fun loadDataStore(): DataStore<Preferences> {
    val dataStore = widgetDataStoreManager.getDatastore(widgetId)
    cachedDataStore = dataStore
    return dataStore
  }
}

private const val TAG = "GlobalCurrentValueStore"
