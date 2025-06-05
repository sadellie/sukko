package io.github.sadellie.sukko.core.medialistener

import io.github.sadellie.sukko.core.model.basic.MediaInfo

interface MediaListener {
  companion object {
    const val MEDIA_METADATA_UPDATE = "MEDIA_METADATA_UPDATE"
  }

  fun startListening()

  fun destroy()

  fun pause()

  fun play()

  fun seek(timestamp: Long)

  fun skipToNext()

  fun skipToPrevious()

  fun openPlayer()

  fun getMediaInfo(): MediaInfo?
}
