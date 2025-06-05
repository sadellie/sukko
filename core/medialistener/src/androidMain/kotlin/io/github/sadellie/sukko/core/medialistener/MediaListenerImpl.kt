package io.github.sadellie.sukko.core.medialistener

import android.content.ComponentName
import android.content.Context
import android.content.Context.MEDIA_SESSION_SERVICE
import android.content.Intent
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import androidx.annotation.OptIn
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.BitmapLoader
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSourceBitmapLoader
import androidx.media3.session.CacheBitmapLoader
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
import co.touchlab.kermit.Logger
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import io.github.sadellie.sukko.core.common.cachePath
import io.github.sadellie.sukko.core.common.defaultIODispatcher
import io.github.sadellie.sukko.core.common.getAppLaunchIntent
import io.github.sadellie.sukko.core.model.basic.MediaInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import android.media.session.MediaController as MediaControllerLegacy

class MediaListenerImpl(
  private val context: Context,
  private val onMetadataUpdate: (context: Context, action: String) -> Unit,
) : MediaListener {
  // private var activeMediaController: MediaController? = null
  private var initialised = false
  private var coroutineScope = CoroutineScope(defaultIODispatcher)
  private var activePlayer: Player? = null
  private var activePlayerPackageName: String? = null
  private val mediaInfoFlow = MutableStateFlow<MediaInfo?>(null)
  private val mediaSessionManager: MediaSessionManager =
    context.getSystemService(MEDIA_SESSION_SERVICE) as MediaSessionManager

  override fun getMediaInfo(): MediaInfo? = mediaInfoFlow.value

  override fun startListening() {
    if (initialised) {
      Logger.w(TAG) { "Called startListening on already initialised listener" }
      return
    }
    if (!NotificationListener.canAccessNotifications(context)) {
      Logger.e(TAG) { "No notifications access" }
      return
    }
    val notificationListener = ComponentName(context, NotificationListener::class.java)
    mediaSessionManager.addOnActiveSessionsChangedListener(sessionListener, notificationListener)
    // set initial active sessions
    val activeSessions = mediaSessionManager.getActiveSessions(notificationListener)
    sessionListener.onActiveSessionsChanged(activeSessions)

    initialised = true
  }

  override fun destroy() {
    if (!initialised) return
    mediaSessionManager.removeOnActiveSessionsChangedListener(sessionListener)
    initialised = false
  }

  override fun pause() {
    if (activePlayer?.isCommandAvailable(Player.COMMAND_PLAY_PAUSE) == true) {
      activePlayer?.pause()
    }
  }

  override fun play() {
    if (activePlayer?.isCommandAvailable(Player.COMMAND_PLAY_PAUSE) == true) {
      activePlayer?.play()
    }
  }

  override fun seek(timestamp: Long) {
    if (activePlayer?.isCommandAvailable(Player.COMMAND_SEEK_IN_CURRENT_MEDIA_ITEM) == true) {
      activePlayer?.seekTo(timestamp)
    }
  }

  override fun skipToNext() {
    if (activePlayer?.isCommandAvailable(Player.COMMAND_SEEK_TO_NEXT) == true) {
      activePlayer?.seekToNext()
    }
  }

  override fun skipToPrevious() {
    if (activePlayer?.isCommandAvailable(Player.COMMAND_SEEK_TO_PREVIOUS) == true) {
      activePlayer?.seekToPrevious()
    }
  }

  override fun openPlayer() {
    val packageName = activePlayerPackageName
    if (packageName == null) {
      Logger.d(TAG) { "openPlayer: packageName is null" }
      return
    }
    val intent = context.getAppLaunchIntent(packageName)
    if (intent == null) {
      Logger.d(TAG) { "openPlayer: intent is null" }
      return
    }
    context.startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
  }

  private val sessionListener =
    MediaSessionManager.OnActiveSessionsChangedListener {
      val newControllers = it ?: emptyList()
      Logger.d(TAG) {
        "onAvailableSessionCommandsChanged called: ${newControllers.map { controller -> controller.packageName }}"
      }
      newControllers.forEach { newController ->
        val token = SessionToken.createSessionToken(context, newController.sessionToken).get()
        val browserListener = object : MediaBrowser.Listener {}
        val browserFuture =
          MediaBrowser.Builder(context, token).setListener(browserListener).buildAsync()

        val playerListener = playerListener(newController)
        Futures.addCallback(
          browserFuture,
          object : FutureCallback<MediaBrowser> {
            override fun onSuccess(browser: MediaBrowser) {
              browser.addListener(playerListener)
            }

            override fun onFailure(t: Throwable) = Logger.e(TAG, t) { "Failed to connect media browser" }
          },
          ContextCompat.getMainExecutor(context.applicationContext),
        )
      }
    }

  @OptIn(UnstableApi::class)
  private fun playerListener(controller: MediaControllerLegacy) =
    object : Player.Listener {
      private val bitmapLoader = CacheBitmapLoader(DataSourceBitmapLoader(context))
      private var durationMs: Long = 0

      override fun onEvents(player: Player, events: Player.Events) {
        durationMs = player.contentDuration
        if (Player.EVENT_IS_PLAYING_CHANGED in events && player.isPlaying) {
          activePlayer = player
          activePlayerPackageName = controller.packageName
        }
      }

      override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
        coroutineScope.launch {
          mediaInfoFlow.update {
            val newMediaInfo = newMediaInfo(mediaMetadata)
            Logger.d(TAG) { "onMediaMetadataChanged: $newMediaInfo" }
            newMediaInfo
          }
          onMetadataUpdate(context, MediaListener.MEDIA_METADATA_UPDATE)
        }
      }

      private var observePlaybackStateJob: Job? = null

      override fun onIsPlayingChanged(isPlaying: Boolean) {
        if (!isPlaying) {
          observePlaybackStateJob?.cancel()
          observePlaybackStateJob = null
          Logger.d(TAG) { "Not playing anymore, stop observing" }
          onMetadataUpdate(context, MediaListener.MEDIA_METADATA_UPDATE)
          return
        }
        if (observePlaybackStateJob != null) {
          Logger.d(TAG) { "Already observing" }
          return
        }
        observePlaybackStateJob =
          coroutineScope.launch {
            while (isPlaying) {
              mediaInfoFlow.update { mediaInfo ->
                val newMediaInfo = mediaInfo?.withUpdatedPlaybackPosition()
                Logger.d(TAG) { "onIsPlayingChanged: $newMediaInfo" }
                newMediaInfo
              }
              onMetadataUpdate(context, MediaListener.MEDIA_METADATA_UPDATE)
              delay(PLAYBACK_REFRESH_RATE)
            }
          }
      }

      private suspend fun newMediaInfo(mediaMetadata: MediaMetadata) =
        MediaInfo(
          artist = mediaMetadata.artist?.toString(),
          title = mediaMetadata.title?.toString(),
          // TODO separate listener for cover. store bitmap and access it with content provider
          coverUri = mediaMetadata.extractCoverUri(context, bitmapLoader),
          playerPackageName = controller.packageName,
          playerIconUri =
            (context.cachePath / NotificationListener.PLAYER_ICON_NAME_FULL)
              .toFile()
              .toURI()
              .toString(),
          durationMs = durationMs,
          positionMs = controller.playbackState?.position ?: 0,
          playbackState = controller.playbackState?.state ?: PlaybackState.STATE_NONE,
        )

      private fun MediaInfo.withUpdatedPlaybackPosition() =
        this.copy(durationMs = durationMs, positionMs = controller.playbackState?.position ?: 0)
    }
}

private suspend fun <T> ListenableFuture<T>.await(): T =
  suspendCancellableCoroutine { continuation ->
    Futures.addCallback(
      this,
      object : FutureCallback<T> {
        override fun onSuccess(result: T) {
          continuation.resume(result)
        }

        override fun onFailure(t: Throwable) {
          continuation.resumeWithException(t)
        }
      },
      // this will use callers thread
      MoreExecutors.directExecutor(),
    )
  }

private const val TAG = "MediaListener"

private const val PLAYBACK_REFRESH_RATE = 5_000L

/**
 * Extract uri to cover bitmap. Caches bitmaps internally if metadata provides bitmap instead of uri
 * to bitmap.
 */
@OptIn(UnstableApi::class)
private suspend fun MediaMetadata.extractCoverUri(
  context: Context,
  bitmapLoader: BitmapLoader,
): String? {
  val bitmap = bitmapLoader.loadBitmapFromMetadata(this)?.await()
  if (bitmap == null) {
    Logger.e(TAG) { "extractCoverUri: Not bitmap or uri" }
    return null
  }
  Logger.d(TAG) { "Found cover bitmap" }
  val coverFileName = "media.cover"
  val cachedBitmap = bitmap.cache(context, coverFileName)
  return cachedBitmap
}
