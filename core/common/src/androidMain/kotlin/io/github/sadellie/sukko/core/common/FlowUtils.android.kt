package io.github.sadellie.sukko.core.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow

actual val defaultIODispatcher = Dispatchers.IO

@Composable
actual fun <T> StateFlow<T>.collectAsStateWithLifecycleKMP(
  initialValue: T,
  minActiveState: Lifecycle.State,
  context: CoroutineContext,
): State<T> =
  collectAsStateWithLifecycle(
    initialValue = initialValue,
    minActiveState = minActiveState,
    context = context,
  )
