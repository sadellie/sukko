package io.github.sadellie.sukko.core.model.basic

data class MediaInfo(
  val artist: String?,
  val title: String?,
  val playerPackageName: String,
  val durationMs: Long,
  val positionMs: Long,
  val playbackState: Int,
)
