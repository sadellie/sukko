package io.github.sadellie.sukko.core.common

import android.net.Uri
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.context
import io.github.vinceglb.filekit.dialogs.toAndroidUri

fun PlatformFile.uri(): Uri {
  // from filekit 0.10.0
  val authority = "${FileKit.context.packageName}.filekit.fileprovider"
  return this.toAndroidUri(authority)
}
