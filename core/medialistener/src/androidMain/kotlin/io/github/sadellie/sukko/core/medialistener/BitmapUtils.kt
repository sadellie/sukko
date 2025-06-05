package io.github.sadellie.sukko.core.medialistener

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import co.touchlab.kermit.Logger
import io.github.sadellie.sukko.core.common.cachePath
import java.io.BufferedOutputStream

/** Cache bitmap in temp folder and return URI to file as string. */
internal fun Bitmap.cache(context: Context, fileName: String): String {
  val tempFile = (context.cachePath / "$fileName.png").toFile()
  tempFile.parentFile?.mkdirs()
  val tempFileUri = Uri.fromFile(tempFile).toString()
  Logger.d("BitmapUtils") { "cache update: $tempFileUri" }
  BufferedOutputStream(tempFile.outputStream()).use {
    this@cache.compress(Bitmap.CompressFormat.PNG, 0, it)
  }
  return tempFileUri
}
