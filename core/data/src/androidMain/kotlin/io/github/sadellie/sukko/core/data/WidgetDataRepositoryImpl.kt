package io.github.sadellie.sukko.core.data

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import io.github.sadellie.sukko.core.common.MainWidgetAction
import io.github.sadellie.sukko.core.common.filesPath
import io.github.sadellie.sukko.core.database.WidgetDataBased
import io.github.sadellie.sukko.core.database.WidgetDataDao
import io.github.sadellie.sukko.core.medialistener.NotificationListener
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.WidgetData
import io.github.sadellie.sukko.core.model.WidgetSubscriptionInfo
import io.github.sadellie.sukko.core.model.WidgetUpdateException
import io.github.sadellie.sukko.core.model.layer.Layer
import java.io.BufferedOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okio.Path

internal class WidgetDataRepositoryImpl(
  private val dao: WidgetDataDao,
  private val context: Context,
  private val removeImageFromCache: (path: Path) -> Unit,
) : WidgetDataRepository {
  @OptIn(ExperimentalCoroutinesApi::class)
  override fun allWidgetData(decodeExtra: Boolean): Flow<List<WidgetData>> =
    dao.getAll().mapLatest { basedList ->
      basedList.map { widgetDataBased -> widgetDataBased.toDomain(decodeExtra = decodeExtra) }
    }

  override suspend fun loadByAppWidgetId(appWidgetId: Int): WidgetData? =
    dao.getById(appWidgetId)?.toDomain()

  override suspend fun save(
    widgetData: WidgetData,
    evaluatedLayers: List<Layer.Evaluated>,
    previewImageBitmap: ImageBitmap?,
    isForced: Boolean,
  ) {
    if (previewImageBitmap != null) {
      withContext(Dispatchers.IO) {
        val previewFilePath = widgetData.getPreviewPath(context.filesPath)
        previewFilePath.parent?.toFile()?.mkdirs()
        val previewFile = previewFilePath.toFile()
        val previewAndroidBitmap = previewImageBitmap.asAndroidBitmap()
        BufferedOutputStream(previewFile.outputStream()).use {
          previewAndroidBitmap.compress(Bitmap.CompressFormat.PNG, 0, it)
        }
        removeImageFromCache(previewFilePath)
      }
    }
    dao.save(widgetData.toBased())
    updateWidget(widgetData, isForced)
  }

  override suspend fun rename(appWidgetId: Int, newName: String) = dao.rename(appWidgetId, newName)

  override suspend fun delete(appWidgetId: Int) {
    val preset = loadByAppWidgetId(appWidgetId)
    preset?.getDataPath(context.filesPath)?.toFile()?.deleteRecursively()
    dao.deleteById(appWidgetId)
  }

  override suspend fun getPreview(widgetData: WidgetData) =
    widgetData.getPreviewPath(context.filesPath)

  private fun WidgetDataBased.toDomain(decodeExtra: Boolean = true): WidgetData {
    val parsedLayers =
      if (decodeExtra) Json.decodeFromString<List<Layer.Cold>>(layers) else emptyList()
    val parsedGlobals = if (decodeExtra) Json.decodeFromString<Globals>(globals) else Globals()
    return WidgetData(
      appWidgetId = appWidgetId,
      name = name,
      layers = parsedLayers,
      globals = parsedGlobals,
    )
  }

  private fun WidgetData.toBased(): WidgetDataBased {
    val layerAsJson = Json.encodeToString(layers)
    val globalsAsJson = Json.encodeToString(globals)
    return WidgetDataBased(
      appWidgetId = appWidgetId,
      name = name,
      layers = layerAsJson,
      globals = globalsAsJson,
    )
  }

  /**
   * @param forced When true will not throw on missing notification listener and other permission.
   */
  private suspend fun updateWidget(widgetData: WidgetData, forced: Boolean) {
    val widgetSubscriptionInfo = generateWidgetSubscriptionInfo(widgetData)
    if (forced) {
      // disabled checks
      sendUpdateWidgetBroadcast(widgetData.appWidgetId, widgetSubscriptionInfo)
      return
    }

    if (widgetSubscriptionInfo.isMedia && !NotificationListener.canAccessNotifications(context)) {
      throw WidgetUpdateException.MissingNotificationListener()
    }

    // all checks passed
    sendUpdateWidgetBroadcast(widgetData.appWidgetId, widgetSubscriptionInfo)
  }

  private fun sendUpdateWidgetBroadcast(
    appWidgetId: Int,
    widgetSubscriptionInfo: WidgetSubscriptionInfo,
  ) {
    val intent =
      Intent(MainWidgetAction.ACTION_UPDATE_WITH_SUBSCRIPTION)
        .setPackage(context.packageName)
        .putExtra(MainWidgetAction.EXTRA_APPWIDGET_ID, appWidgetId)
        .putExtra(MainWidgetAction.EXTRA_IS_TIME_SUBSCRIBER, widgetSubscriptionInfo.isTime)
        .putExtra(MainWidgetAction.EXTRA_IS_BATTERY_SUBSCRIBER, widgetSubscriptionInfo.isBattery)
        .putExtra(MainWidgetAction.EXTRA_IS_MEDIA_SUBSCRIBER, widgetSubscriptionInfo.isMedia)
    context.sendBroadcast(intent)
  }
}
