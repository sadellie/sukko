package io.github.sadellie.sukko.core.routes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.flow.receiveAsFlow

val LocalEventBus = staticCompositionLocalOf { ResultEventBus() }

/**
 * An Effect to provide a result even between different screens
 *
 * The trailing lambda provides the result from a flow of results.
 *
 * @param resultEventBus the ResultEventBus to retrieve the result from. The default value is read
 *   from the `LocalResultEventBus` composition local.
 * @param resultKey the key that should be associated with this effect
 * @param onResult the callback to invoke when a result is received
 * @author https://github.com/android/nav3-recipes
 */
@Composable
inline fun <reified T> ResultEffect(
  resultEventBus: ResultEventBus,
  resultKey: String = T::class.toString(),
  crossinline onResult: suspend (T) -> Unit,
) {
  LaunchedEffect(resultKey, resultEventBus.channelMap[resultKey]) {
    resultEventBus.getResultFlow<T>(resultKey)?.collect { result -> onResult.invoke(result as T) }
  }
}

/**
 * An EventBus for passing results between multiple sets of screens.
 *
 * It provides a solution for event based results.
 */
class ResultEventBus {
  /** Map from the result key to a channel of results. */
  val channelMap: MutableMap<String, Channel<Any?>> = mutableMapOf()

  /** Provides a flow for the given resultKey. */
  inline fun <reified T> getResultFlow(resultKey: String = T::class.toString()) =
    channelMap[resultKey]?.receiveAsFlow()

  /** Sends a result into the channel associated with the given resultKey. */
  inline fun <reified T> sendResult(resultKey: String = T::class.toString(), result: T) {
    if (!channelMap.contains(resultKey)) {
      channelMap[resultKey] =
        Channel(capacity = BUFFERED, onBufferOverflow = BufferOverflow.SUSPEND)
    }
    channelMap[resultKey]?.trySend(result)
  }
}
