package io.github.sadellie.sukko

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.ComposeViewport
import io.github.sadellie.sukko.core.designsystem.LocalWindowSize
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3WindowSizeClassApi::class)
fun main() {
  ComposeViewport(document.body!!) {
    val containerSize = LocalWindowInfo.current.containerSize
    val density = LocalDensity.current
    val windowSizeClass =
      remember(containerSize, density) {
        with(density) {
          WindowSizeClass.calculateFromSize(
            DpSize(containerSize.width.toDp(), containerSize.height.toDp())
          )
        }
      }
    CompositionLocalProvider(LocalWindowSize provides windowSizeClass) {
      Column {
        Text("Main app: $windowSizeClass")
        Button(onClick = { callLogs() }, shapes = ButtonDefaults.shapes()) { Text("Call logs") }
      }
    }
  }
}

external fun callLogs()
