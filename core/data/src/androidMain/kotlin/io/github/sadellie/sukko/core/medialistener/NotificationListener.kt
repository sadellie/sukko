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
import io.github.sadellie.sukko.core.data.ImageProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NotificationListener : NotificationListenerService(), KoinComponent {
  companion object {
    fun canAccessNotifications(context: Context): Boolean {
      return NotificationManagerCompat.getEnabledListenerPackages(context)
        .contains(context.packageName)
    }

    fun openNotificationListenerPermission(context: Context) {
      context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
    }

    private const val TAG = "NotificationListener"
  }

  private val latestPackagePoster = MutableStateFlow<String?>(null)
  private var coroutineScope = CoroutineScope(defaultIODispatcher)
  private var updateUriJob: Job? = null
  private val imageProvider: ImageProvider by inject()

  override fun onNotificationPosted(sbn: StatusBarNotification?) {
    if (sbn?.notification == null) return
    val isMediaNotification = sbn.notification.extras.containsKey(Notification.EXTRA_MEDIA_SESSION)
    if (!isMediaNotification) return

    if (latestPackagePoster.value == sbn.packageName) {
      Logger.d(tag = TAG) { "onNotificationPosted: Same package: ${sbn.packageName}" }
      return
    }
    latestPackagePoster.update { sbn.packageName }
    Logger.d(tag = TAG) { "onNotificationPosted: New package. Update icon. ${sbn.packageName}" }
    updateUriJob?.cancel()
    updateUriJob =
      coroutineScope.launch {
        val icon = sbn.notification.smallIcon
        if (icon == null) {
          Logger.w(tag = TAG) { "No icons!" }
          return@launch
        }
        val drawable = icon.loadDrawable(applicationContext) as? BitmapDrawable
        if (drawable == null) {
          Logger.w(tag = TAG) { "Drawable is not BitmapDrawable" }
          return@launch
        }
        imageProvider.updatePlayerIcon(drawable.bitmap)
      }
  }
}
