package io.github.sadellie.sukko

import android.content.Context
import io.github.sadellie.sukko.core.data.ImageProvider
import io.github.sadellie.sukko.core.medialistener.NotificationListener

class NotificationListenerImpl : NotificationListener() {
  override fun getImageProvider(context: Context): ImageProvider =
    getApplicationGraph(context).imageProvider
}
