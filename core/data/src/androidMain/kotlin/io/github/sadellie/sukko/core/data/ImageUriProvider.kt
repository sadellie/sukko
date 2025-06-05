package io.github.sadellie.sukko.core.data

import androidx.core.net.toUri
import co.touchlab.kermit.Logger
import io.github.sadellie.sukko.core.remote.RemoteClient
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.Url
import io.ktor.util.cio.writeChannel
import io.ktor.util.hex
import io.ktor.utils.io.copyAndClose
import java.io.File
import java.security.MessageDigest
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.DurationUnit
import okio.Path
import okio.Path.Companion.toPath

class ImageUriProvider(private val remoteClient: RemoteClient, private val cacheDirPath: Path) {
  companion object {
    private const val TAG = "ImageUriProvider"
  }

  private val maxAgeMs by lazy { 2.hours.toInt(DurationUnit.MILLISECONDS) }

  /**
   * Processes images. If it's a local file, returns same [uri]. If network file, will download,
   * cache and return uri to cached file
   */
  suspend fun loadAndCacheImage(uri: String): String {
    val isNetworkUri =
      uri.startsWith("http://", ignoreCase = true) || uri.startsWith("https://", ignoreCase = true)
    if (!isNetworkUri) return uri
    Logger.d(TAG) { "Network image: $uri" }

    val keyHex = key(Url(uri))
    val localImagePath = cacheDirPath / "${keyHex.toPath()}.cache".toPath()
    val localImageFile = localImagePath.toFile()
    if (!isUpToDate(localImageFile)) {
      loadImageFromNetwork(uri, localImageFile)
    }

    return localImageFile.toUri().toString()
  }

  /**
   * Check if [localImageFile] is fresh enough. Returns false is file is too old.
   *
   * @see maxAgeMs
   */
  fun isUpToDate(localImageFile: File): Boolean {
    if (localImageFile.exists()) return false
    val currentTimeMs = Clock.System.now().epochSeconds * 1_000
    val modifiedTimeMs = localImageFile.lastModified()
    val ageMs = currentTimeMs - modifiedTimeMs
    val isFresh = ageMs < maxAgeMs
    // do not reload if cache is fresh
    Logger.d(TAG) { "isFresh. ageMs: $ageMs" }
    return isFresh
  }

  /** Load image from [uri] and cache it in [localImageFile] */
  private suspend fun loadImageFromNetwork(uri: String, localImageFile: File) {
    try {
      remoteClient.ktorClient.prepareGet(uri).execute { response ->
        Logger.d(TAG) { "Getting image from network" }
        val channel = response.bodyAsChannel()
        channel.copyAndClose(localImageFile.writeChannel())
      }
    } catch (e: Exception) {
      Logger.e(TAG, e) { "Failed to get from remote: $uri" }
    }
  }

  // from ktor FileStorage
  private fun key(url: Url) =
    hex(MessageDigest.getInstance("SHA-256").digest(url.toString().encodeToByteArray()))
}
