package io.github.sadellie.sukko.core.medialistener

import android.app.Notification
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationManagerCompat
import co.touchlab.kermit.Logger
import io.github.sadellie.sukko.core.common.defaultIODispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NotificationListener : NotificationListenerService() {
  companion object {
    fun canAccessNotifications(context: Context): Boolean {
      return NotificationManagerCompat.getEnabledListenerPackages(context)
        .contains(context.packageName)
    }

    fun openNotificationListenerPermission(context: Context) {
      context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
    }

    private const val PLAYER_ICON_NAME = "player.icon"
    const val PLAYER_ICON_NAME_FULL = "$PLAYER_ICON_NAME.png"
    private const val TAG = "NotificationListener"
  }

  private val latestPackagePoster = MutableStateFlow<String?>(null)
  private var coroutineScope = CoroutineScope(defaultIODispatcher)
  private var updateUriJob: Job? = null

  override fun onNotificationPosted(sbn: StatusBarNotification?) {
    if (sbn?.notification == null) return
    val isMediaNotification = sbn.notification.extras.containsKey(Notification.EXTRA_MEDIA_SESSION)
    if (!isMediaNotification) return

    if (latestPackagePoster.value == sbn.packageName) {
      Logger.d(TAG) { "onNotificationPosted: Same package: ${sbn.packageName}" }
      return
    }
    latestPackagePoster.update { sbn.packageName }
    Logger.d(TAG) { "onNotificationPosted: New package. Update icon. ${sbn.packageName}" }
    updateUriJob?.cancel()
    updateUriJob =
      coroutineScope.launch {
        val icon = sbn.notification.smallIcon
        if (icon == null) {
          Logger.w(TAG) { "No icons!" }
          return@launch
        }
        val drawable = icon.loadDrawable(applicationContext) as? BitmapDrawable
        if (drawable == null) {
          Logger.w(TAG) { "Drawable is not BitmapDrawable" }
          return@launch
        }
        drawable.bitmap.cache(applicationContext, PLAYER_ICON_NAME)
      }
  }
}
