package io.github.sadellie.sukko.core.data

import android.content.Context
import android.media.AudioManager
import android.media.session.PlaybackState
import io.github.sadellie.sukko.core.common.getAppLabel
import io.github.sadellie.sukko.core.medialistener.MediaListener

internal class MediaInfoProviderImpl(context: Context, private val mediaListener: MediaListener) :
  MediaInfoProvider {
  private val mediaInfo by lazy { mediaListener.getMediaInfo() }
  private val audioManager: AudioManager by lazy {
    context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
  }
  override val artist: String? by lazy { mediaInfo?.artist }
  override val title: String? by lazy { mediaInfo?.title }
  override val durationSeconds: Long? by lazy { mediaInfo?.durationMs?.div(MS_IN_SECONDS) }
  override val positionSeconds: Long? by lazy { mediaInfo?.positionMs?.div(MS_IN_SECONDS) }
  override val playerName: String? by lazy {
    // this is here and not in media listener to invoke as late as possible
    val packageName = mediaInfo?.playerPackageName ?: return@lazy null
    context.getAppLabel(packageName)
  }
  override val playerState: String? by lazy {
    mediaInfo?.playbackState?.let { playbackStateAsString(it) }
  }
  override val volumeMusicMin by lazy { audioManager.getStreamMinVolume(AudioManager.STREAM_MUSIC) }
  override val volumeMusic by lazy { audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) }
  override val volumeMusicMax by lazy { audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) }
}

private fun playbackStateAsString(state: Int): String =
  when (state) {
    PlaybackState.STATE_NONE -> "NONE"
    PlaybackState.STATE_STOPPED -> "STOPPED"
    PlaybackState.STATE_PAUSED -> "PAUSED"
    PlaybackState.STATE_PLAYING -> "PLAYING"
    PlaybackState.STATE_FAST_FORWARDING -> "FAST_FORWARDING"
    PlaybackState.STATE_REWINDING -> "REWINDING"
    PlaybackState.STATE_BUFFERING -> "BUFFERING"
    PlaybackState.STATE_ERROR -> "ERROR"
    PlaybackState.STATE_SKIPPING_TO_PREVIOUS -> "SKIPPING_TO_PREVIOUS"
    PlaybackState.STATE_SKIPPING_TO_NEXT -> "SKIPPING_TO_NEXT"
    PlaybackState.STATE_SKIPPING_TO_QUEUE_ITEM -> "SKIPPING_TO_QUEUE_ITEM"
    else -> "NONE"
  }

private const val MS_IN_SECONDS = 1_000L
