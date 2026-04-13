package io.github.sadellie.sukko.core.data

import co.touchlab.kermit.Logger
import coil3.Bitmap
import coil3.Image
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.Uri
import coil3.annotation.ExperimentalCoilApi
import coil3.asImage
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.network.cachecontrol.CacheControlCacheStrategy
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.ImageRequest
import coil3.toBitmap
import coil3.toUri
import io.github.sadellie.sukko.core.data.ImageProvider.Companion.ALBUM_COVER_URI
import io.github.sadellie.sukko.core.model.basic.ImageUriSource
import okio.Path

interface ImageProvider {
  companion object {
    const val ALBUM_COVER_URI = "sukko://album_cover"
    const val PLAYER_ICON_URI = "sukko://player_icon"
    internal const val TAG = "ImageProvider"

    fun getAlbumCoverImage(imageLoader: ImageLoader) =
      imageLoader.memoryCache?.get(MemoryCache.Key(ALBUM_COVER_URI))?.image

    fun getPlayerIconImage(imageLoader: ImageLoader) =
      imageLoader.memoryCache?.get(MemoryCache.Key(PLAYER_ICON_URI))?.image
  }

  val imageLoader: ImageLoader

  suspend fun updateAlbumCoverFromUri(uri: Uri)

  fun clearAlbumCoverCache() = imageLoader.memoryCache?.remove(MemoryCache.Key(ALBUM_COVER_URI))

  suspend fun getImageFromUri(uri: String): Image?

  fun updateAlbumCoverFromBitmap(bitmap: Bitmap) =
    imageLoader.memoryCache?.set(
      MemoryCache.Key(ALBUM_COVER_URI),
      MemoryCache.Value(bitmap.asImage()),
    )

  fun updatePlayerIcon(bitmap: Bitmap) =
    imageLoader.memoryCache?.set(
      MemoryCache.Key(PLAYER_ICON_URI),
      MemoryCache.Value(bitmap.asImage()),
    )

  suspend fun getBitmap(
    imageUriSource: ImageUriSource,
    filesDirPath: Path,
    scriptableEvaluator: ScriptableEvaluator,
  ): Bitmap? {
    return when (imageUriSource) {
      ImageUriSource.AlbumCover -> getAlbumCoverImage(imageLoader)
      is ImageUriSource.Gallery -> imageUriSource.imageUri?.let { getImageFromUri(it) }
      is ImageUriSource.IconPack -> {
        if (imageUriSource.iconFile == null) return null
        val uri = imageUriSource.iconFile.getFullPath(filesDirPath).toString()
        getImageFromUri(uri)
      }
      is ImageUriSource.Link -> {
        val uri = scriptableEvaluator.evaluateString(imageUriSource.value)
        if (uri.isEmpty()) return null
        getImageFromUri(uri)
      }
      ImageUriSource.PlayerIcon -> getPlayerIconImage(imageLoader)
    }?.toBitmap()
  }

  suspend fun getBitmap(uri: String): Bitmap? = getImageFromUri(uri)?.toBitmap()
}

internal class ImageProviderImpl(
  private val platformContext: PlatformContext,
  private val cacheDir: Path,
) : ImageProvider {

  @OptIn(ExperimentalCoilApi::class)
  override val imageLoader =
    ImageLoader.Builder(platformContext)
      .memoryCache { MemoryCache.Builder().maxSizePercent(platformContext, 0.35).build() }
      .diskCache { DiskCache.Builder().directory(cacheDir).maxSizePercent(0.05).build() }
      .components {
        add(SukkoFetcher.Factory())
        add(KtorNetworkFetcherFactory(cacheStrategy = { CacheControlCacheStrategy() }))
      }
      .build()

  override suspend fun updateAlbumCoverFromUri(uri: Uri) {
    val imageFromUri =
      imageLoader.execute(ImageRequest.Builder(platformContext).data(uri).build()).image
    if (imageFromUri == null) {
      Logger.w(tag = ImageProvider.TAG) {
        "updateAlbumCoverFromUri. Failed to load image from $uri"
      }
      return
    }
    imageLoader.memoryCache?.set(MemoryCache.Key(ALBUM_COVER_URI), MemoryCache.Value(imageFromUri))
  }

  override suspend fun getImageFromUri(uri: String): Image? =
    imageLoader.execute(ImageRequest.Builder(platformContext).data(uri.toUri()).build()).image
}
