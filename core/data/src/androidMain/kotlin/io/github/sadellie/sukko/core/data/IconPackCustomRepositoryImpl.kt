package io.github.sadellie.sukko.core.data

import android.content.Context
import io.github.sadellie.sukko.core.common.fileObserverFlow
import io.github.sadellie.sukko.core.common.filesPath
import io.github.sadellie.sukko.core.common.listInFilesAssets
import io.github.sadellie.sukko.core.common.uri
import io.github.sadellie.sukko.core.database.IconPackBased
import io.github.sadellie.sukko.core.database.IconPackDao
import io.github.sadellie.sukko.core.iconfiles.IconFile
import io.github.sadellie.sukko.core.iconfiles.IconPack
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.name
import kotlin.io.path.ExperimentalPathApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import okio.Path
import okio.Path.Companion.toPath

class IconPackCustomRepositoryImpl(
  private val dao: IconPackDao,
  private val context: Context,
  private val removeImageFromCache: (path: Path) -> Unit,
) : IconPackCustomRepository {
  @OptIn(ExperimentalCoroutinesApi::class)
  override fun getAll(): Flow<List<IconPack.Custom>> =
    dao.getAll().mapLatest { basedIconPacks ->
      basedIconPacks.map { iconPackBased -> iconPackBased.toDomain() }
    }

  override fun getIconFiles(iconPack: IconPack): Flow<List<IconFile>> {
    val fullIconPackPath = iconPack.getFullPath(context.filesPath)
    val iconPackDirectory = fullIconPackPath.toFile()
    return fileObserverFlow(iconPackDirectory) { _, _ -> getIconFilesFromIconPack(iconPack) }
      .onStart { if (!iconPackDirectory.exists()) iconPackDirectory.mkdirs() }
  }

  @OptIn(ExperimentalPathApi::class)
  override suspend fun deleteIconPack(iconPack: IconPack.Custom): Unit =
    withContext(Dispatchers.IO) {
      dao.delete(iconPack.iconPackId)
      val fullIconPackPath = iconPack.getFullPath(context.filesPath)
      fullIconPackPath.toFile().deleteRecursively()
    }

  override suspend fun renameIconPack(iconPack: IconPack.Custom, newName: String) =
    dao.rename(iconPack.iconPackId, newName)

  override suspend fun deleteIconFile(iconFile: IconFile) {
    withContext(Dispatchers.IO) { iconFile.getFullPath(context.filesPath).toFile().delete() }
  }

  override suspend fun renameIconFile(iconFile: IconFile, newName: String) {
    withContext(Dispatchers.IO) {
      val iconFileFullPath = iconFile.getFullPath(context.filesPath)
      val newPathParent = iconFileFullPath.parent
      val newPath: Path = if (newPathParent == null) newName.toPath() else newPathParent / newName
      val destination = newPath.toFile()
      iconFileFullPath.toFile().renameTo(destination)
    }
  }

  override suspend fun addNewIconPack(iconPack: IconPack.Custom): IconPack.Custom {
    val rowId = dao.insertNew(iconPack.toBased())
    return iconPack.copy(iconPackId = rowId)
  }

  override suspend fun getIconFilesFromIconPack(iconPack: IconPack): List<IconFile> =
    withContext(Dispatchers.IO) {
      val files =
        when (iconPack) {
          is IconPack.Custom ->
            iconPack.getFullPath(context.filesPath).toFile().listFiles()?.toList()
          IconPack.MaterialSymbolsRounded ->
            context.listInFilesAssets(iconPack.getFullPath(context.filesPath))
        }
      val iconFiles = files?.map { file -> IconFile(fileName = file.name, iconPack = iconPack) }

      return@withContext iconFiles ?: emptyList()
    }

  override suspend fun importIconFiles(iconPack: IconPack.Custom, iconFiles: List<PlatformFile>) =
    withContext(Dispatchers.IO) {
      val iconPackFullPath = iconPack.getFullPath(context.filesPath)
      iconPackFullPath.toFile().mkdirs()
      iconFiles.forEach { platformFile ->
        context.applicationContext.contentResolver.openInputStream(platformFile.uri()).use {
          inputStream ->
          val destinationPath = iconPackFullPath / platformFile.name
          val destinationFile = destinationPath.toFile()
          if (destinationFile.exists()) throw FileAlreadyExistsException(destinationFile)
          destinationFile.createNewFile()
          val destinationOutputStream = destinationFile.outputStream()
          inputStream?.copyTo(destinationOutputStream)
          removeImageFromCache(destinationPath)
        }
      }
    }

  private fun IconPackBased.toDomain() = IconPack.Custom(iconPackId, name)

  private fun IconPack.Custom.toBased() = IconPackBased(iconPackId, name)
}
