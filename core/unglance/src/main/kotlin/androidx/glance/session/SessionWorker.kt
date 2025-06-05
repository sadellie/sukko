package androidx.glance.session

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import co.touchlab.kermit.Logger
import io.github.sadellie.sukko.core.unglance.RenderResult
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Options to configure [SessionWorker] timeouts.
 *
 * @property initialTimeout How long to wait after the first successful composition before timing
 *   out.
 * @property additionalTime If an external event is received and there is less than [additionalTime]
 *   remaining, add [additionalTime] so that there is enough time to respond to the event.
 * @property idleTimeout Timeout within [idleTimeout] if the system is in idle/light idle/low power
 *   standby mode.
 * @property timeSource The time source for measuring progress towards timeouts.
 */
data class TimeoutOptions(
  val initialTimeout: Duration = 25.seconds,
  val additionalTime: Duration = 5.seconds,
  val idleTimeout: Duration = 5.seconds,
  val timeSource: TimeSource = TimeSource.Monotonic,
)

/**
 * [SessionWorker] handles composition for a particular Glanceable.
 *
 * This worker runs the [Session] it acquires from [SessionManager] for the key given in the worker
 * params. The worker then sets up and runs a composition, then provides the resulting UI tree (and
 * those of successive recompositions) to [Session.processRenderResult]. After the initial
 * composition, the worker blocks on [Session.receiveEvents] until [Session.close] is called.
 */
class SessionWorker(
  appContext: Context,
  private val params: WorkerParameters,
  private val sessionManager: SessionManager = UnglanceSessionManager,
  private val timeouts: TimeoutOptions = TimeoutOptions(),
  @Deprecated("Deprecated by super class, replacement in progress, see b/245353737")
  override val coroutineContext: CoroutineDispatcher = Dispatchers.Main,
) : CoroutineWorker(appContext, params) {
  // This constructor is required by WorkManager's default WorkerFactory.
  constructor(
    appContext: Context,
    params: WorkerParameters,
  ) : this(appContext, params, UnglanceSessionManager)

  companion object {
    internal const val TAG = "GlanceSessionWorker"
    internal const val TIMEOUT_EXIT_REASON = "TIMEOUT_EXIT_REASON"
    private const val SESSION_RUN_LIMIT = 3
  }

  private val key =
    inputData.getString(sessionManager.keyParam)
      ?: error("SessionWorker must be started with a key")

  override suspend fun doWork(): Result {
    Logger.d(TAG) { "doWork called" }
    val session =
      sessionManager.runWithLock { getSession(key) }
        ?: if (params.runAttemptCount == 0) {
          error("No session available for key $key")
        } else {
          // If this is a retry because the process was restarted (e.g. on app upgrade
          // or reinstall), the Session object won't be available because it's not
          // persistable.
          Logger.w(TAG) { "SessionWorker attempted restart but Session is not available for $key" }
          return Result.success()
        }

    var nextSession: Session? = session
    var runCount = 0
    while (nextSession != null && runCount < SESSION_RUN_LIMIT) {
      val currentSession = nextSession
      runCount++
      try {
        val result =
          withTimerOrNull(timeouts.timeSource) {
            observeIdleEvents(
              applicationContext,
              onIdle = {
                startTimer(timeouts.idleTimeout)
                Logger.d(TAG) { "Received idle event, session timeout $timeLeft" }
              },
            ) {
              runSession(applicationContext, currentSession, timeouts)
              Result.success()
            }
          }

        if (result != null) {
          Logger.w(TAG) { "result is null" }
          // If there is a result, runSession completed in time, which means that the
          // session was closed externally (widget deleted). In this case we do not care
          // if there were pending events to run.
          return result
        }

        // If the session timed out with pending events, continue looping.
        nextSession = sessionManager.runWithLock { recreateOrClose(currentSession) }
      } finally {
        if (currentSession.hasError) {
          // An error was thrown, make sure to close the session.
          withContext(NonCancellable) {
            sessionManager.runWithLock { closeSession(currentSession.key) }
          }
        }
      }
    }

    return Result.success(Data.Builder().putBoolean(TIMEOUT_EXIT_REASON, true).build())
  }
}

@OptIn(ExperimentalCoroutinesApi::class)
private suspend fun TimerScope.runSession(
  context: Context,
  session: Session,
  timeouts: TimeoutOptions,
) {
  val uiReady = MutableStateFlow(false)

  val resultHolder = MutableSharedFlow<RenderResult>()
  try {
    // flow that generates new states
    launch {
      Logger.d(SessionWorker.TAG) { "Collecting unglance results" }
      try {
        session.provideUnglance(context).collect { captureComposableResult ->
          resultHolder.emit(captureComposableResult)
        }
      } catch (_: CancellationException) {
        // do nothing if we are cancelled.
      } catch (e: Exception) {
        Logger.e(SessionWorker.TAG, e) { "Failed to update view" }
        session.reportCompositionError(context, e)
      }
    }
    // flow that consumes and processes states
    launch {
      resultHolder.collectLatest { renderResult ->
        session.processRenderResult(context, renderResult)
        if (!uiReady.value) {
          uiReady.emit(true)
          startTimer(timeouts.initialTimeout)
        }
        Logger.d(SessionWorker.TAG) { "processRemoteViews done" }
      }
    }
    Logger.d(SessionWorker.TAG) { "starting to receive events" }
    // wait here until uiReady emits true
    uiReady.first { it }
    // receiveEvents will suspend until the session is closed (usually due to widget deletion)
    // or it is cancelled (in case of composition errors or timeout).
    session.receiveEvents(context) {
      // If time is running low, add time to make sure that we have time to respond to this
      // event.
      if (timeLeft < timeouts.additionalTime) addTime(timeouts.additionalTime)
      Logger.d(SessionWorker.TAG) { "Received event $it" }
    }
  } catch (e: TimeoutCancellationException) {
    Logger.e(SessionWorker.TAG, e) { "session closed. timeout" }
  } catch (e: Exception) {
    Logger.e(SessionWorker.TAG, e) { "runSession failed" }
  }
}
