package io.github.sadellie.sukko.core.designsystem

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import coil3.ImageLoader

val LocalImageLoader: ProvidableCompositionLocal<ImageLoader> = staticCompositionLocalOf {
  error("No local provided")
}
