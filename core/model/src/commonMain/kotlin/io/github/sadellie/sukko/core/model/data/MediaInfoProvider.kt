package io.github.sadellie.sukko.core.model.data

/** Provider for currently playing media information */
interface MediaInfoProvider {
  /** Media artist */
  val artist: String?
  /** Media title. For example, name of a song */
  val title: String?
  /** Media duration. Can be any number */
  val durationSeconds: Long?
  /** Media position. Can be any number */
  val positionSeconds: Long?
  /** Media cover uri to locally accessible file */
  val coverUri: String?
  /** Player icon uri to locally accessible file */
  val playerIcon: String?
  /** Player name, app name */
  val playerName: String?
  /** Player status. From PlaybackState */
  val playerState: String?
  /** Minimal music volume */
  val volumeMusicMin: Int
  /** Current music volume */
  val volumeMusic: Int
  /** Maximum music volume */
  val volumeMusicMax: Int
}
