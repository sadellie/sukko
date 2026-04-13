package io.github.sadellie.sukko.core.medialistener

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSession
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.util.Log
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import co.touchlab.kermit.Logger
import coil3.toCoilUri
import io.github.sadellie.sukko.core.common.getAppLaunchIntent
import io.github.sadellie.sukko.core.data.ImageProvider
import io.github.sadellie.sukko.core.model.basic.MediaInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Modified
 *
 * @author
 *   https://github.com/shub39/Rush/blob/2e47c9acdff5c27b18c24b86bb5ac10ca6a980c8/app/src/main/java/com/shub39/rush/data/listener/MediaListenerImpl.kt
 */
class MediaListenerImpl(
  private val context: Context,
  private val onMetadataUpdate: (context: Context, action: String) -> Unit,
  private val imageProvider: ImageProvider,
) : MediaListener {
  private var msm: MediaSessionManager? = null
  private var nls: ComponentName? = null
  private var activeMediaController: MediaController? = null
  private val internalCallbacks = mutableMapOf<MediaSession.Token, MediaController.Callback>()
  private var initialised = false
  private var coroutineScope = CoroutineScope(Dispatchers.IO)
  private var positionUpdateJob: Job? = null

  val songInfoFlow: MutableStateFlow<MediaInfo?> = MutableStateFlow(null)

  override fun startListening() {
    try {
      if (NotificationListener.canAccessNotifications(context) && !initialised) {
        initialised = true
        msm = context.getSystemService<MediaSessionManager>()
        nls = ComponentName(context, NotificationListener::class.java)
        msm?.let { manager ->
          manager.addOnActiveSessionsChangedListener(listener, nls)
          val activeSessions = manager.getActiveSessions(nls!!)
          val activeSession = activeSessions.find { isActive(it.playbackState) }
          activeMediaController = activeSession ?: activeSessions.firstOrNull()
          listener.onActiveSessionsChanged(activeSessions)
          Logger.d(tag = TAG) { "init $manager" }
        } ?: Logger.w(tag = "MediaListener") { "MediaSessionManager is null" }
      }
    } catch (e: Exception) {
      Logger.e(e, TAG) { "Exception when starting a listener" }
    }
  }

  override fun destroy() {
    msm?.removeOnActiveSessionsChangedListener(listener)
    initialised = false
  }

  override fun pause() {
    activeMediaController?.transportControls?.pause()
  }

  override fun play() {
    activeMediaController?.transportControls?.play()
  }

  override fun seek(timestamp: Long) {
    val controller = activeMediaController
    if (controller == null) {
      Logger.w(tag = TAG) { "Can't seek. activeMediaController is null" }
      return
    }
    controller.transportControls.seekTo(timestamp)
    controller.transportControls.play()
    coroutineScope.launch { sendUpdateToWidget(controller, controller.metadata, force = true) }
  }

  override fun openPlayer() {
    val packageName = activeMediaController?.packageName
    if (packageName == null) {
      Logger.d(tag = TAG) { "openPlayer: packageName is null" }
      return
    }
    val intent = context.getAppLaunchIntent(packageName)
    if (intent == null) {
      Logger.d(tag = TAG) { "openPlayer: intent is null" }
      return
    }
    context.startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
  }

  override fun getMediaInfo() = songInfoFlow.value

  override fun skipToNext() {
    activeMediaController?.transportControls?.skipToNext()
  }

  override fun skipToPrevious() {
    activeMediaController?.transportControls?.skipToPrevious()
  }

  private val listener: MediaSessionManager.OnActiveSessionsChangedListener = { controllers ->
    val newCallbacks = mutableMapOf<MediaSession.Token, MediaController.Callback>()

    controllers?.filterNotNull()?.forEach { controller ->
      Log.d("MediaListener", "Session: $controller (${controller.sessionToken})")

      // Workaround for spotify, dunno if this is the most elegant solution but works :)
      if (controller.packageName.contains("spotify")) {
        coroutineScope.launch {
          delay(2000)
          setActiveMediaSession(controller)
        }
      } else if (isActive(controller.playbackState)) {
        setActiveMediaSession(controller)
      }

      if (internalCallbacks.containsKey(controller.sessionToken)) {
        newCallbacks[controller.sessionToken] = internalCallbacks[controller.sessionToken]!!
      } else {
        val callback =
          object : MediaController.Callback() {
            override fun onPlaybackStateChanged(state: PlaybackState?) {
              onPlaybackStateChanged(controller, state)
            }

            override fun onMetadataChanged(metadata: MediaMetadata?) {
              updateMetadata(controller, metadata)
            }
          }

        controller.registerCallback(callback)
        newCallbacks[controller.sessionToken] = callback
      }
    }

    internalCallbacks.clear()
    internalCallbacks.putAll(newCallbacks)
  }

  private fun isActive(playbackState: PlaybackState?): Boolean {
    if (playbackState == null) return false
    return playbackState.isActive
  }

  private fun onPlaybackStateChanged(controller: MediaController, state: PlaybackState?) {
    if (isActive(state)) {
      setActiveMediaSession(controller)
    } else {
      stopPositionUpdates()
      coroutineScope.launch { sendUpdateToWidget(controller, controller.metadata, force = true) }
    }
  }

  private fun setActiveMediaSession(newActive: MediaController) {
    activeMediaController = newActive
    updateMetadata(newActive, newActive.metadata)
    startPositionUpdates()
  }

  private fun startPositionUpdates() {
    positionUpdateJob?.cancel()
    positionUpdateJob =
      coroutineScope.launch {
        while (isActive) {
          activeMediaController?.let { controller ->
            if (isActive(controller.playbackState)) {
              sendUpdateToWidget(controller, controller.metadata)
              Logger.d(tag = TAG) { "Update position: ${songInfoFlow.value}" }
            }
          }
          delay(POSITION_REFRESH_RATE)
        }
      }
  }

  private fun stopPositionUpdates() {
    positionUpdateJob?.cancel()
    positionUpdateJob = null
  }

  private fun updateMetadata(controller: MediaController, metadata: MediaMetadata?) {
    if (controller.sessionToken != activeMediaController?.sessionToken) return

    if (controller.playbackState?.isActive != true) return
    if (metadata == null) return

    coroutineScope.launch {
      Logger.d(tag = TAG) { "Update updateMetadata" }
      sendUpdateToWidget(controller, metadata, updateArt = true)
    }
  }

  private var lastUpdate = System.currentTimeMillis()

  private suspend fun sendUpdateToWidget(
    controller: MediaController,
    metadata: MediaMetadata?,
    updateArt: Boolean = false,
    force: Boolean = false,
  ) {
    if (!force) {
      val currentTime = System.currentTimeMillis()
      if ((currentTime - lastUpdate) <= PLAYBACK_REFRESH_RATE) {
        Logger.d(tag = TAG) { "Data is fresh enough. Will not update" }
        return
      }
    }
    if (updateArt && metadata != null) {
      updateArtwork(metadata) // todo do not update artwork if uri or bitmap is same
    }
    songInfoFlow.update { _ ->
      MediaInfo(
          artist = metadata?.let { getArtist(it) },
          title = metadata?.let { getTitle(it) },
          playerPackageName = controller.packageName ?: "",
          durationMs = metadata?.getLong(MediaMetadata.METADATA_KEY_DURATION) ?: 0,
          positionMs = controller.playbackState?.position ?: 0L,
          playbackState = controller.playbackState?.state ?: 0,
        )
        .also { Logger.d(tag = TAG) { "New info: $it" } }
    }
    onMetadataUpdate(context, MediaListener.MEDIA_METADATA_UPDATE)
    lastUpdate = System.currentTimeMillis()
  }

  private fun getTitle(metadata: MediaMetadata) =
    metadata.getString(MediaMetadata.METADATA_KEY_TITLE) ?: ""

  private fun getArtist(metadata: MediaMetadata) =
    metadata.getString(MediaMetadata.METADATA_KEY_ARTIST)
      ?: metadata.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST)
      ?: ""

  private suspend fun updateArtwork(metadata: MediaMetadata) =
    withContext(Dispatchers.IO) {
      try {
        val uri =
          metadata.getString(MediaMetadata.METADATA_KEY_ART_URI)
            ?: metadata.getString(MediaMetadata.METADATA_KEY_ALBUM_ART_URI)
        Logger.d(tag = TAG) { "updateArtwork. Uri: $uri" }

        if (uri != null) {
          imageProvider.updateAlbumCoverFromUri(uri.toUri().toCoilUri())
          Logger.d(tag = TAG) { "updateArtwork. Updated from uri: $uri" }
          return@withContext
        }

        val bitmap =
          metadata.getBitmap(MediaMetadata.METADATA_KEY_ART)
            ?: metadata.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART)
        if (bitmap != null) {
          Logger.d(tag = TAG) { "updateArtwork. Updated from bitmap" }
          imageProvider.updateAlbumCoverFromBitmap(bitmap)
        }
        Logger.d(tag = TAG) { "updateArtwork. No artwork available" }
        imageProvider.clearAlbumCoverCache()
      } catch (e: Exception) {
        Logger.e(e, TAG) { "Failed to update artwork" }
        imageProvider.clearAlbumCoverCache()
      }
    }
}

private const val TAG = "MediaListener"
private const val PLAYBACK_REFRESH_RATE = 5_000L
private const val POSITION_REFRESH_RATE = 5_000L
