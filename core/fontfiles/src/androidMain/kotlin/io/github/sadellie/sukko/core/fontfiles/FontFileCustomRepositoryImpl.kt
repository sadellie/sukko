package io.github.sadellie.sukko.core.fontfiles

import android.content.Context
import io.github.sadellie.sukko.core.common.fileObserverFlow
import io.github.sadellie.sukko.core.common.filesPath
import io.github.sadellie.sukko.core.common.uri
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.name
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import okio.Path
import okio.Path.Companion.toPath

internal class FontFileCustomRepositoryImpl(private val context: Context) :
  FontFileCustomRepository {
  @OptIn(ExperimentalCoroutinesApi::class)
  override fun loadAll(): Flow<List<FontFile.Custom>> {
    val fontsDirectory = (context.filesPath / FontFile.Custom.DIR_PATH.toPath()).toFile()
    return fileObserverFlow(fontsDirectory) { _, _ -> fontsDirectory.listFiles() ?: emptyArray() }
      .onStart { fontsDirectory.mkdirs() }
      .mapLatest { files -> files.map { file -> FontFile.Custom(file.name) } }
  }

  override suspend fun importFontFiles(filesToImport: List<PlatformFile>) =
    withContext(Dispatchers.IO) {
      val fontsDirectory = FontFile.Custom.DIR_PATH.toPath().toFile()
      fontsDirectory.mkdirs()
      filesToImport.forEach { platformFile ->
        context.contentResolver.openInputStream(platformFile.uri()).use { inputStream ->
          val destinationPath = FontFile.Custom(platformFile.name).getFullPath(context.filesPath)
          val destinationFile = destinationPath.toFile()
          if (destinationFile.exists()) throw FileAlreadyExistsException(destinationFile)
          destinationFile.createNewFile()
          val destinationOutputStream = destinationFile.outputStream()
          inputStream?.copyTo(destinationOutputStream)
        }
      }
    }

  override suspend fun delete(fontFile: FontFile.Custom) {
    withContext(Dispatchers.IO) { fontFile.getFullPath(context.filesPath).toFile().delete() }
  }

  override suspend fun rename(fontFile: FontFile.Custom, newName: String) {
    withContext(Dispatchers.IO) {
      val path = fontFile.getFullPath(context.filesPath)
      val newPathParent = path.parent
      val newPath: Path = if (newPathParent == null) newName.toPath() else newPathParent / newName
      val destination = newPath.toFile()
      if (destination.exists()) throw FileAlreadyExistsException(destination)
      path.toFile().renameTo(destination)
    }
  }
}
