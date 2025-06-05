package io.github.sadellie.sukko.core.designsystem

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import okio.Path

val LocalFilesDirPath: ProvidableCompositionLocal<Path> = staticCompositionLocalOf {
  error("No local provided")
}
