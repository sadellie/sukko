package io.github.sadellie.sukko.core.medialistener

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import co.touchlab.kermit.Logger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MediaListenerService : Service(), KoinComponent {
  override fun onBind(intent: Intent?): IBinder? = null

  override fun onCreate() {
    super.onCreate()
    Logger.d(tag = TAG) { "onCreate service" }
    try {
      val mediaListener by inject<MediaListener>()
      mediaListener.startListening()
    } catch (e: Exception) {
      Logger.e(throwable = e, tag = TAG) { "Failed to start media listener" }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    val mediaListener by inject<MediaListener>()
    mediaListener.destroy()
  }

  companion object {
    fun start(context: Context) {
      if (!NotificationListener.canAccessNotifications(context)) {
        Logger.e(tag = TAG) { "Can not start service: notification listener is not allowed" }
        return
      }
      val intent = getServiceIntent(context)
      context.startService(intent)
      Logger.d(tag = TAG) { "Media listener started" }
    }

    fun stop(context: Context) {
      val intent = getServiceIntent(context)
      context.stopService(intent)
    }

    private fun getServiceIntent(context: Context) =
      Intent(context.applicationContext, MediaListenerService::class.java)

    private const val TAG = "MediaListenerService"
  }
}
