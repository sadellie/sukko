package io.github.sadellie.sukko.core.common

import android.os.FileObserver
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

fun <T> fileObserverFlow(file: File, onEventCallback: suspend (event: Int, path: String?) -> T) =
  callbackFlow {
      val listener =
        object : FileObserver(file, CREATE or DELETE or MOVED_TO) {
          override fun onEvent(event: Int, path: String?) {
            this@callbackFlow.launch {
              val value = onEventCallback(event, path)
              trySend(value)
            }
          }

          override fun startWatching() {
            super.startWatching()
            onEvent(0, null)
          }
        }
      listener.startWatching()
      awaitClose { listener.stopWatching() }
    }
    .distinctUntilChanged()
    .flowOn(Dispatchers.IO)
