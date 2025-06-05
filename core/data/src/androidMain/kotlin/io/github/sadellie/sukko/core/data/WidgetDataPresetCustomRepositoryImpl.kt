package io.github.sadellie.sukko.core.data

import android.content.Context
import io.github.sadellie.sukko.core.common.filesPath
import io.github.sadellie.sukko.core.database.WidgetDataPresetBased
import io.github.sadellie.sukko.core.database.WidgetDataPresetDao
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.WidgetDataPreset
import io.github.sadellie.sukko.core.model.layer.Layer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.serialization.json.Json
import okio.Path

class WidgetDataPresetCustomRepositoryImpl(
  private val dao: WidgetDataPresetDao,
  private val context: Context,
  private val removeImageFromCache: (path: Path) -> Unit,
) : WidgetDataPresetCustomRepository {
  @OptIn(ExperimentalCoroutinesApi::class)
  override fun allWidgetDataPresets(decodeExtra: Boolean): Flow<List<WidgetDataPreset.Custom>> =
    dao.getAll().mapLatest { basedList ->
      basedList.map { widgetDataPresetBased ->
        widgetDataPresetBased.toDomain(decodeExtra = decodeExtra)
      }
    }

  override suspend fun loadByPresetId(presetId: Long) = dao.getById(presetId)?.toDomain()

  override suspend fun insertNewWithPreview(
    widgetDataPreset: WidgetDataPreset.Custom,
    previewPath: Path?,
  ): Long {
    val presetId = dao.insertNew(widgetDataPreset.copy(presetId = 0).toBased())

    if (previewPath != null) {
      val previewFile = previewPath.toFile()
      val destinationFile =
        widgetDataPreset.copy(presetId = presetId).getPreviewPath(context.filesPath).toFile()
      previewFile.copyTo(target = destinationFile, overwrite = true)
      removeImageFromCache(previewPath)
    }
    return presetId
  }

  override suspend fun rename(presetId: Long, newName: String) = dao.rename(presetId, newName)

  override suspend fun delete(presetId: Long) {
    val preset = loadByPresetId(presetId)
    preset?.getDataPath(context.filesPath)?.toFile()?.deleteRecursively()
    dao.deleteById(presetId)
  }

  private fun WidgetDataPresetBased.toDomain(decodeExtra: Boolean = true): WidgetDataPreset.Custom {
    val parsedLayers =
      if (decodeExtra) Json.decodeFromString<List<Layer.Cold>>(layers) else emptyList()
    val parsedGlobals = if (decodeExtra) Json.decodeFromString<Globals>(globals) else Globals()
    return WidgetDataPreset.Custom(
      presetId = presetId,
      name = name,
      layers = parsedLayers,
      globals = parsedGlobals,
    )
  }

  private fun WidgetDataPreset.Custom.toBased(): WidgetDataPresetBased {
    val layerAsJson = Json.encodeToString(layers)
    val globalsAsJson = Json.encodeToString(globals)
    return WidgetDataPresetBased(
      presetId = presetId,
      name = name,
      layers = layerAsJson,
      globals = globalsAsJson,
    )
  }
}
