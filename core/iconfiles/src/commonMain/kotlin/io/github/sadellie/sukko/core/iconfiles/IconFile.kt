package io.github.sadellie.sukko.core.iconfiles

import io.github.sadellie.sukko.core.common.ASSET_PATH
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import okio.Path

@Serializable
sealed interface IconPack {
  val iconPackId: Long
  val name: String

  fun getFullPath(filesDirPath: Path): Path

  companion object {
    private const val DIR_PATH = "iconPacks"

    fun builtIns(): List<BuiltIn> = listOf(MaterialSymbolsRounded)
  }

  @Serializable
  sealed interface BuiltIn : IconPack {
    override fun getFullPath(filesDirPath: Path) = ASSET_PATH / DIR_PATH / iconPackId.toString()
  }

  @Serializable
  data object MaterialSymbolsRounded : BuiltIn {
    override val name = "Material Symbols Rounded"
    override val iconPackId = -1L
  }

  @Serializable
  data class Custom(override val iconPackId: Long, override val name: String) : IconPack {
    override fun getFullPath(filesDirPath: Path): Path =
      filesDirPath / DIR_PATH / iconPackId.toString()
  }
}

/** @property fileName Name of the icon file, not path. For example: file_1.svg */
@Serializable
data class IconFile(val fileName: String, val iconPack: IconPack) {
  fun getFullPath(filesDirPath: Path): Path = iconPack.getFullPath(filesDirPath) / fileName

  @Transient val name = fileName.removeSuffix(".$EXTENSION")

  companion object {
    const val EXTENSION = "svg"
  }
}
