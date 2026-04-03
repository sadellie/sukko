package io.github.sadellie.sukko.core.data

import android.content.Context
import android.content.Intent
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import io.github.sadellie.sukko.core.model.InstalledApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class InstalledAppsProviderImpl(private val context: Context) : InstalledAppsProvider {
  override suspend fun getAllApps(): List<InstalledApp> =
    withContext(Dispatchers.Default) {
      val packageManager = context.packageManager
      val mainIntent =
        Intent(Intent.ACTION_MAIN, null).apply { addCategory(Intent.CATEGORY_LAUNCHER) }

      val intentActivities = packageManager.queryIntentActivities(mainIntent, 0)
      val allApps = mutableSetOf<InstalledApp>()
      for (resolveInfo in intentActivities) {
        val label = resolveInfo.loadLabel(packageManager).toString()
        val packageId = resolveInfo.activityInfo?.packageName ?: continue
        val icon = resolveInfo.loadIcon(packageManager).toBitmap().asImageBitmap()
        allApps.add(InstalledApp(label, packageId, icon))
      }

      val result = allApps.distinctBy { it.packageId }.sortedBy { it.label }
      return@withContext result
    }
}
