package io.github.sadellie.sukko.core.designsystem

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
val LocalWindowSize: ProvidableCompositionLocal<WindowSizeClass> = compositionLocalOf {
  // Phone in portrait mode: WindowWidthSizeClass.Compact and WindowHeightSizeClass.Medium
  WindowSizeClass.calculateFromSize(DpSize(599.dp, 480.dp))
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun PreviewScreenSizesContainer(content: @Composable () -> Unit) = Preview2 {
  BoxWithConstraints(Modifier.fillMaxSize()) {
    val dpSize = DpSize(this.minWidth, this.minHeight)
    val windowSizeClass = WindowSizeClass.calculateFromSize(dpSize)
    CompositionLocalProvider(LocalWindowSize provides windowSizeClass) { content() }
  }
}

val WindowWidthSizeClass.Companion.expanded: Dp
  get() = 840.dp

val WindowWidthSizeClass.Companion.medium: Dp
  get() = 600.dp

val WindowWidthSizeClass.Companion.compact: Dp
  get() = 400.dp

val WindowHeightSizeClass.Companion.expanded: Dp
  get() = 900.dp

val WindowHeightSizeClass.Companion.medium: Dp
  get() = 480.dp

val WindowHeightSizeClass.Companion.compact: Dp
  get() = 400.dp
