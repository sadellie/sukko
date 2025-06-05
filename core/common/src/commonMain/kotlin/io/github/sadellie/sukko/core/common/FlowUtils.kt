package io.github.sadellie.sukko.core.common

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.Lifecycle
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn

expect val defaultIODispatcher: CoroutineDispatcher

fun <T> Flow<T>.stateIn(scope: CoroutineScope, initialValue: T): StateFlow<T> =
  stateIn(scope, SharingStarted.WhileSubscribed(STATE_IN_TIMEOUT_MS), initialValue)

@Suppress("UNCHECKED_CAST", "MagicNumber")
fun <T1, T2, T3, T4, T5, T6, R> combineBig(
  flow: Flow<T1>,
  flow2: Flow<T2>,
  flow3: Flow<T3>,
  flow4: Flow<T4>,
  flow5: Flow<T5>,
  flow6: Flow<T6>,
  transform: suspend (T1, T2, T3, T4, T5, T6) -> R,
): Flow<R> =
  combine(flow, flow2, flow3, flow4, flow5, flow6) { args ->
    transform(
      args[0] as T1,
      args[1] as T2,
      args[2] as T3,
      args[3] as T4,
      args[4] as T5,
      args[5] as T6,
    )
  }

@Composable
expect fun <T> StateFlow<T>.collectAsStateWithLifecycleKMP(
  initialValue: T = this.value,
  minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
  context: CoroutineContext = EmptyCoroutineContext,
): State<T>

private const val STATE_IN_TIMEOUT_MS = 5_000L

@OptIn(FlowPreview::class)
fun TextFieldState.observe() = snapshotFlow { text }.debounce(100.milliseconds)
