package androidx.glance.session

import android.content.Context
import io.github.sadellie.sukko.core.unglance.RenderResult
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.Flow

/**
 * [Session] is implemented by Glance surfaces in order to provide content for the composition and
 * process the results of recomposition.
 */
abstract class Session(val key: String) {
  // _isOpen/isOpen is used to check whether this Session's event channel is still open and
  // accepting events (close has not been called). It may be checked or set from different
  // threads, so we use an AtomicBoolean so that the value is updated atomically.
  @OptIn(ExperimentalAtomicApi::class) private val _isOpen = AtomicBoolean(true)
  internal val isOpen: Boolean
    get() = _isOpen.get()

  // This is set to indicate if this session has an error, i.e. reportCompositionError has been
  // called at least once.
  private val _hasError = AtomicBoolean(false)
  internal val hasError: Boolean
    get() = _hasError.get()

  private val eventChannel = Channel<Any>(Channel.UNLIMITED)

  /** Evaluates, renders and emits results. This method is called from worker */
  abstract suspend fun provideUnglance(context: Context): Flow<RenderResult>

  /** Process an event that was sent to this session. */
  abstract suspend fun processEvent(context: Context, event: Any)

  abstract suspend fun processRenderResult(context: Context, renderResult: RenderResult)

  /**
   * Enqueues an [event] to be processed by the session.
   *
   * These requests may be processed by calling [receiveEvents]. Session implementations should wrap
   * sendEvent with public methods to send the event types that their Session supports.
   *
   * If this session is managed by [SessionManager], [sendEvent] should only be called while holding
   * the SessionManager lock.
   */
  protected suspend fun sendEvent(event: Any) {
    eventChannel.send(event)
  }

  /**
   * Process incoming events, additionally running [block] for each event that is received.
   *
   * This function suspends until [close] is called.
   */
  suspend fun receiveEvents(context: Context, block: (Any) -> Unit) {
    try {
      for (event in eventChannel) {
        block(event)
        processEvent(context, event)
      }
    } catch (_: ClosedReceiveChannelException) {}
  }

  /**
   * Close the session. Any events sent before [close] will be processed unless the Worker for this
   * session is cancelled.
   *
   * If this session is managed by [SessionManager], [close] should only be called while holding the
   * SessionManager lock.
   */
  fun close() {
    eventChannel.close()
    _isOpen.set(false)
    onClosed()
  }

  /** Called after the session is closed. Can be used by implementers to clean up any resources. */
  open fun onClosed() {}

  /**
   * Called when there is an error in the composition. The session will be closed immediately after
   * this.
   */
  abstract suspend fun onCompositionError(context: Context, throwable: Throwable)

  /** Called to report an error while running the composition. */
  suspend fun reportCompositionError(context: Context, throwable: Throwable) {
    _hasError.set(true)
    onCompositionError(context, throwable)
  }

  /*
   * Returns any pending events.
   */
  fun receiveAllPendingEvents(): List<Any> {
    return eventChannel.receiveAllNonBlocking()
  }

  /** Create a new Session with [events]. */
  abstract suspend fun recreateWithEvents(events: List<Any>): Session
}

private fun <T> Channel<T>.receiveAllNonBlocking(): List<T> {
  val items = mutableListOf<T>()
  do {
    val result = tryReceive()
    result.getOrNull()?.let { items.add(it) }
    // If result is not successful, then the channel is either empty or we've received the
    // close token.
  } while (result.isSuccess)
  return items
}
