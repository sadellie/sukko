package io.github.sadellie.sukko.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import co.touchlab.kermit.Logger
import kotlinx.browser.window

private class LinkOpenerImpl : LinkOpener {
  override fun launch(url: String) {
    try {
      window.open(url)
    } catch (e: Exception) {
      Logger.e(e, "LinkOpener") { "Failed to open link: $url" }
    }
  }
}

@Composable actual fun rememberLinkOpener(): LinkOpener = remember { LinkOpenerImpl() }

@Composable
internal actual fun isNotificationListenerEnabled(): State<Boolean> =
  produceState(false) { value = false }
