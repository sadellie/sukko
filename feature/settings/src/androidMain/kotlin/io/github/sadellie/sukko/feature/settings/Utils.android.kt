package io.github.sadellie.sukko.feature.settings

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import co.touchlab.kermit.Logger
import io.github.sadellie.sukko.core.medialistener.NotificationListener
import kotlinx.coroutines.delay

private class LinkOpenerImpl(private val context: Context) : LinkOpener {
  override fun launch(url: String) {
    try {
      context.startActivity(
        Intent(Intent.ACTION_VIEW).setData(url.toUri()).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      )
    } catch (e: Exception) {
      Logger.e(e, "LinkOpener") { "Failed to open link: $url" }
    }
  }
}

@Composable
actual fun rememberLinkOpener(): LinkOpener {
  val context = LocalContext.current
  return remember(context) { LinkOpenerImpl(context) }
}

@Composable
internal actual fun isNotificationListenerEnabled(): State<Boolean> {
  val context = LocalContext.current
  return produceState(NotificationListener.canAccessNotifications(context)) {
    while (true) {
      value = NotificationListener.canAccessNotifications(context)
      delay(NOTIFICATION_LISTENER_UPDATE_RATE)
    }
  }
}

private const val NOTIFICATION_LISTENER_UPDATE_RATE = 2_000L
