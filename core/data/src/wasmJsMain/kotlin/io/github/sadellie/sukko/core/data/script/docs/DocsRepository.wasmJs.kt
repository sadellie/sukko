package io.github.sadellie.sukko.core.data.script.docs

actual class DocsRepositoryImpl : DocsRepository {
  actual override suspend fun load() {}

  actual override suspend fun search(query: String, lang: String): Docs? {
    TODO("Not yet implemented")
  }
}
