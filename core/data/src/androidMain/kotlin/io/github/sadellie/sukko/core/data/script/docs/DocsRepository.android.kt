package io.github.sadellie.sukko.core.data.script.docs

import io.github.sadellie.sukko.core.data.resources.Res
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.InputStream

actual class DocsRepositoryImpl : DocsRepository {
  companion object {
    private const val SCRIPTING_FILE_PATH = "files/scripting.json"
  }

  private val docs = MutableStateFlow<Docs?>(null)

  actual override suspend fun load() =
    withContext(Dispatchers.IO) {
      Res.readBytes(SCRIPTING_FILE_PATH).inputStream().use { parseAndUpdateDocs(it) }
    }

  actual override suspend fun search(query: String, lang: String): Docs? =
    withContext(Dispatchers.Default) {
      val filteredDocs = docs.value ?: return@withContext null
      val constants =
        filteredDocs.constants.filter {
          it.name[lang]?.contains(query) == true || it.description[lang]?.contains(query) == true
        }
      val methods =
        filteredDocs.methods.filter {
          it.name[lang]?.contains(query) == true || it.description[lang]?.contains(query) == true
        }
      val templates = filteredDocs.templates.filter { it.name[lang]?.contains(query) == true }
      return@withContext filteredDocs.copy(
        constants = constants,
        methods = methods,
        templates = templates,
      )
    }

  @OptIn(ExperimentalSerializationApi::class)
  internal suspend fun parseAndUpdateDocs(inputStream: InputStream) =
    withContext(Dispatchers.IO) {
      val parsedDocs = Json.decodeFromStream<Docs>(inputStream)
      docs.update { parsedDocs }
    }
}
