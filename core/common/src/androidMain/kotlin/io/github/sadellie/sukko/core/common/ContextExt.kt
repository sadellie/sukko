package io.github.sadellie.sukko.core.common

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import co.touchlab.kermit.Logger
import java.io.File
import okio.Path
import okio.Path.Companion.toPath

val Context.filesPath: Path
  get() = this.filesDir.absolutePath.toPath()

/** [path] path that starts with "file://android_assets/..." */
fun Context.listInFilesAssets(path: Path): List<File> {
  val pathInAssets = path.relativeTo("file://android_asset/".toPath())
  return this.assets.list(pathInAssets.toString())?.map { File(it) } ?: emptyList()
}

fun Context.getAppLaunchIntent(packageName: String) =
  packageManager.getLaunchIntentForPackage(packageName)
    ?: "market://details?id=${packageName}".toViewIntent()

fun Context.getAppLabel(packageName: String): String? {
  val packageManager = this.packageManager
  val playerName =
    try {
      val applicationInfo =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
          packageManager.getApplicationInfo(packageName, PackageManager.ApplicationInfoFlags.of(0))
        } else {
          packageManager.getApplicationInfo(packageName, 0)
        }
      packageManager.getApplicationLabel(applicationInfo).toString()
    } catch (e: PackageManager.NameNotFoundException) {
      Logger.e(throwable = e, tag = TAG) { "getPlayerName: Failed to get player name" }
      null
    }

  return playerName
}

fun String.toViewIntent(): Intent? {
  val uri =
    try {
      Uri.parse(this)
    } catch (e: Exception) {
      Logger.e(throwable = e, tag = "toViewIntent") { "Failed to get uri for $this" }
      null
    }
  return if (uri == null) null else Intent(Intent.ACTION_VIEW).setData(uri)
}

private const val TAG = "ContextExt"
