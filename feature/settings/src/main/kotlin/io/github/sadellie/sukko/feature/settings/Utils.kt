package io.github.sadellie.sukko.feature.settings

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import io.github.sadellie.sukko.core.medialistener.NotificationListener
import kotlinx.coroutines.delay

@Composable
internal fun isNotificationListenerEnabled(): State<Boolean> {
  val context = LocalContext.current
  return produceState(NotificationListener.canAccessNotifications(context)) {
    while (true) {
      value = NotificationListener.canAccessNotifications(context)
      delay(NOTIFICATION_LISTENER_UPDATE_RATE)
    }
  }
}

internal const val PRIVACY_POLICY_URL = "https://sadellie.github.io/sukko/privacy/"

/** Open given link in browser */
fun openLink(context: Context, url: String) {
  context.startActivity(
    Intent(Intent.ACTION_VIEW).setData(url.toUri()).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
  )
}

private const val NOTIFICATION_LISTENER_UPDATE_RATE = 2_000L
