package io.github.sadellie.sukko.core.data

import io.github.sadellie.sukko.core.iconfiles.IconFile
import io.github.sadellie.sukko.core.iconfiles.IconPack
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.flow.Flow

interface IconPackCustomRepository {
  fun getAll(): Flow<List<IconPack.Custom>>

  fun getIconFiles(iconPack: IconPack): Flow<List<IconFile>>

  suspend fun deleteIconPack(iconPack: IconPack.Custom)

  suspend fun renameIconPack(iconPack: IconPack.Custom, newName: String)

  suspend fun deleteIconFile(iconFile: IconFile)

  suspend fun renameIconFile(iconFile: IconFile, newName: String)

  suspend fun addNewIconPack(iconPack: IconPack.Custom): IconPack.Custom

  suspend fun importIconFiles(iconPack: IconPack.Custom, iconFiles: List<PlatformFile>)

  suspend fun getIconFilesFromIconPack(iconPack: IconPack): List<IconFile>
}
