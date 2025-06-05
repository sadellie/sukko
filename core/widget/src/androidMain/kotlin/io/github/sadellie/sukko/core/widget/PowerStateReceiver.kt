package io.github.sadellie.sukko.core.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import co.touchlab.kermit.Logger

/** When device changes power state, ask widget to reset alarm manager */
class PowerStateReceiver : BroadcastReceiver() {
  companion object {
    private const val TAG = "PowerStateReceiver"
  }

  override fun onReceive(context: Context?, intent: Intent?) {
    intent ?: return
    context ?: return
    when (intent.action) {
      Intent.ACTION_POWER_CONNECTED,
      Intent.ACTION_POWER_DISCONNECTED,
      PowerManager.ACTION_POWER_SAVE_MODE_CHANGED -> {
        val widgetIntent =
          Intent(context, MainWidgetProvider::class.java)
            .setAction(MainWidgetProvider.ACTION_POWER_UPDATE)
        context.applicationContext.sendBroadcast(widgetIntent)
      }
      else -> Logger.d(TAG) { "Unexpected action: ${intent.action}" }
    }
  }
}
