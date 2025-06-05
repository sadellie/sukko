package io.github.sadellie.sukko.core.model

import io.github.sadellie.sukko.core.iconfiles.IconPack
import kotlin.uuid.ExperimentalUuidApi
import okio.Path

@OptIn(ExperimentalUuidApi::class)
data class ImportingWidgetDataPreset(
  val widgetDataPreset: WidgetDataPreset.Custom,
  val fullPreviewPath: Path?,
  val importingIconPacks: List<ImportingIconPack>,
  val importingFontFiles: List<ImportingFontFile>,
)

data class ImportingIconPack(
  val importingId: Long,
  val importingName: String,
  val action: ImportingIconPackAction,
)

data class ImportingFontFile(val importingName: String, val import: Boolean)

sealed interface ImportingIconPackAction {
  data object CreateNew : ImportingIconPackAction

  data class Merge(val destinationIconPack: IconPack.Custom) : ImportingIconPackAction

  companion object {
    fun actions(customIconPacks: List<IconPack.Custom>): List<ImportingIconPackAction> =
      listOf(CreateNew) + customIconPacks.map { Merge(it) }
  }
}
