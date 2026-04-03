package io.github.sadellie.sukko.core.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import io.github.sadellie.sukko.core.model.basic.GlobalValue
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** Thread safe cache of previously evaluated [GlobalValue]s */
class GlobalValueCache {
  suspend fun clear() {
    booleanCache.clear()
    colorCache.clear()
    doubleCache.clear()
    dpCache.clear()
    spCache.clear()
    stringCache.clear()
    textStyleCache.clear()
  }

  // separate caches to avoid bottleneck
  private val booleanCache by lazy { GlobalValueCacheTyped<Boolean>() }
  private val colorCache by lazy { GlobalValueCacheTyped<Color>() }
  private val doubleCache by lazy { GlobalValueCacheTyped<Double>() }
  private val dpCache by lazy { GlobalValueCacheTyped<Dp>() }
  private val spCache by lazy { GlobalValueCacheTyped<TextUnit>() }
  private val stringCache by lazy { GlobalValueCacheTyped<String>() }
  private val textStyleCache by lazy { GlobalValueCacheTyped<TextStyle>() }

  /**
   * @param lock set to false in recursions to prevent self lock
   * @see [kotlin.collections.getOrPut]
   */
  internal suspend inline fun <V> getOrPut(
    key: GlobalValue<*>,
    lock: Boolean = true,
    default: suspend () -> V,
  ): V? {
    val value =
      when (key) {
        is GlobalValue.GlobalBoolean ->
          booleanCache.getOrPutTyped(key.id, lock) { default() as Boolean }
        is GlobalValue.GlobalColor -> colorCache.getOrPutTyped(key.id, lock) { default() as Color }
        is GlobalValue.GlobalDouble ->
          doubleCache.getOrPutTyped(key.id, lock) { default() as Double }
        is GlobalValue.GlobalDp -> dpCache.getOrPutTyped(key.id, lock) { default() as Dp }
        is GlobalValue.GlobalSp -> spCache.getOrPutTyped(key.id, lock) { default() as TextUnit }
        is GlobalValue.GlobalString ->
          stringCache.getOrPutTyped(key.id, lock) { default() as String }
        is GlobalValue.GlobalTextStyle ->
          textStyleCache.getOrPutTyped(key.id, lock) { default() as TextStyle }
      }

    @Suppress("UNCHECKED_CAST")
    return value as? V
  }
}

internal class GlobalValueCacheTyped<T> {
  val mutex by lazy { Mutex() }
  val cache by lazy { hashMapOf<Long, T>() }

  internal suspend fun clear() = mutex.withLock { cache.clear() }

  internal suspend inline fun getOrPutTyped(
    key: Long,
    lock: Boolean = true,
    default: suspend () -> T,
  ) =
    if (lock) {
      mutex.withLock { cache.getOrPut(key) { default() } }
    } else {
      cache.getOrPut(key) { default() }
    }
}
