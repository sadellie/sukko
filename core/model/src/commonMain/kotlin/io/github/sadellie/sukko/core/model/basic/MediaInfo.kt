package io.github.sadellie.sukko.core.model.basic

data class MediaInfo(
  val lastUpdate: Long = 0L, // TODO remove?
  val artist: String?,
  val title: String?,
  val coverUri: String?,
  val playerPackageName: String,
  val playerIconUri: String?,
  val durationMs: Long,
  val positionMs: Long,
  val playbackState: Int,
)
