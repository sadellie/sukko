package io.github.sadellie.sukko.core.common

import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

// https://github.com/LouisCAD/Splitties/blob/49e2ee566730aaeb14b4fa9e395a677c3f214dba/modules/coroutines/src/commonMain/kotlin/splitties/coroutines/SuspendLazy.kt

fun <T> suspendBlockingLazy(
  dispatcher: CoroutineDispatcher = Dispatchers.Default,
  initializer: () -> T,
): SuspendLazy<T> = SuspendLazyBlockingImpl(dispatcher, initializer)

fun <T> CoroutineScope.suspendLazy(
  context: CoroutineContext = EmptyCoroutineContext,
  initializer: suspend CoroutineScope.() -> T,
): SuspendLazy<T> = SuspendLazySuspendingImpl(this, context, initializer)

interface SuspendLazy<out T> {
  suspend operator fun invoke(): T
}

private class SuspendLazyBlockingImpl<out T>(
  private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
  initializer: () -> T,
) : SuspendLazy<T> {
  private val lazyValue = lazy(initializer)

  override suspend operator fun invoke(): T =
    with(lazyValue) { if (isInitialized()) value else withContext(dispatcher) { value } }
}

private class SuspendLazySuspendingImpl<out T>(
  coroutineScope: CoroutineScope,
  context: CoroutineContext,
  initializer: suspend CoroutineScope.() -> T,
) : SuspendLazy<T> {
  private val deferred =
    coroutineScope.async(context, start = CoroutineStart.LAZY, block = initializer)

  override suspend operator fun invoke(): T = deferred.await()
}
