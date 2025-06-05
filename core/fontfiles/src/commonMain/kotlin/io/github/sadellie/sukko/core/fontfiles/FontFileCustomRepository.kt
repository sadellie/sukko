package io.github.sadellie.sukko.core.fontfiles

import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.flow.Flow

interface FontFileCustomRepository {
  fun loadAll(): Flow<List<FontFile.Custom>>

  suspend fun importFontFiles(filesToImport: List<PlatformFile>)

  suspend fun delete(fontFile: FontFile.Custom)

  suspend fun rename(fontFile: FontFile.Custom, newName: String)
}
