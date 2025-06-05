package io.github.sadellie.sukko.core.data

import io.github.sadellie.sukko.core.model.WidgetDataPreset
import kotlinx.coroutines.flow.Flow
import okio.Path

interface WidgetDataPresetCustomRepository {
  fun allWidgetDataPresets(decodeExtra: Boolean = true): Flow<List<WidgetDataPreset.Custom>>

  suspend fun loadByPresetId(presetId: Long): WidgetDataPreset.Custom?

  /** Create new preset. Preview image will be copied from [previewPath]. */
  suspend fun insertNewWithPreview(
    widgetDataPreset: WidgetDataPreset.Custom,
    previewPath: Path?,
  ): Long

  suspend fun rename(presetId: Long, newName: String)

  suspend fun delete(presetId: Long)
}
