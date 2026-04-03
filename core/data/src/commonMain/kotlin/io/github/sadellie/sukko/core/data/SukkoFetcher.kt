package io.github.sadellie.sukko.core.data

import co.touchlab.kermit.Logger
import coil3.ImageLoader
import coil3.Uri
import coil3.decode.DataSource
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.fetch.ImageFetchResult
import coil3.request.Options

internal class SukkoFetcher(private val data: Uri, private val imageLoader: ImageLoader) : Fetcher {
  override suspend fun fetch(): FetchResult? {
    return try {
      Logger.d(tag = TAG) { "Try get bitmap: $data" }
      val image =
        when (data.toString().lowercase()) {
          ImageProvider.ALBUM_COVER_URI -> ImageProvider.getAlbumCoverImage(imageLoader)
          ImageProvider.PLAYER_ICON_URI -> ImageProvider.getPlayerIconImage(imageLoader)
          else -> null
        }
      if (image == null) {
        Logger.d(tag = TAG) { "No image: $data" }
        return null
      }
      ImageFetchResult(image, isSampled = false, dataSource = DataSource.MEMORY)
    } catch (e: Exception) {
      Logger.e(e, TAG) { "Failed to fetch: $data" }
      null
    }
  }

  class Factory : Fetcher.Factory<Uri> {
    override fun create(data: Uri, options: Options, imageLoader: ImageLoader): Fetcher? {
      Logger.d(tag = TAG) { "Try intercept: $data" }
      if (data.scheme == "sukko") {
        Logger.d(tag = TAG) { "Intercepted: $data" }
        return SukkoFetcher(data, imageLoader)
      }
      return null
    }
  }
}

private const val TAG = "SukkoFetcher"
