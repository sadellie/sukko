package io.github.sadellie.sukko.core.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.Lifecycle
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow

actual val defaultIODispatcher = Dispatchers.Default

@Composable
actual fun <T> StateFlow<T>.collectAsStateWithLifecycleKMP(
  initialValue: T,
  minActiveState: Lifecycle.State,
  context: CoroutineContext,
): State<T> = collectAsState(initial = initialValue, context = context)
