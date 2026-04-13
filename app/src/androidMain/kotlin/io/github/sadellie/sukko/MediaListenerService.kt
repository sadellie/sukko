package io.github.sadellie.sukko

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import co.touchlab.kermit.Logger
import io.github.sadellie.sukko.core.medialistener.NotificationListener

class MediaListenerService : Service() {
  override fun onBind(intent: Intent?): IBinder? = null

  override fun onCreate() {
    super.onCreate()
    Logger.d(tag = TAG) { "onCreate service" }
    try {
      getApplicationGraph(this.applicationContext).mediaListener.startListening()
    } catch (e: Exception) {
      Logger.e(throwable = e, tag = TAG) { "Failed to start media listener" }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    getApplicationGraph(this.applicationContext).mediaListener.destroy()
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
