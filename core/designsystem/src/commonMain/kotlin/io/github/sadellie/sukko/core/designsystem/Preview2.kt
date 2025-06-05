package io.github.sadellie.sukko.core.designsystem

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.toArgb
import coil3.ColorImage
import coil3.ImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import coil3.compose.LocalPlatformContext
import okio.Path.Companion.toPath

@OptIn(ExperimentalCoilApi::class)
@Composable
fun Preview2(content: @Composable () -> Unit) {
  val placeholderColor = MaterialTheme.colorScheme.tertiary
  val previewHandler = AsyncImagePreviewHandler { ColorImage(placeholderColor.toArgb()) }

  CompositionLocalProvider(
    LocalAsyncImagePreviewHandler provides previewHandler,
    LocalImageLoader provides ImageLoader.Builder(LocalPlatformContext.current).build(),
    LocalFilesDirPath provides "".toPath(),
    LocalContentColor provides MaterialTheme.colorScheme.onSurface,
  ) {
    Surface(content = content)
  }
}
