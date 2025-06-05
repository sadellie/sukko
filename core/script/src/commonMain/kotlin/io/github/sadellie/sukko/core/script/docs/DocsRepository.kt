package io.github.sadellie.sukko.core.script.docs

expect class DocsRepositoryImpl() : DocsRepository {
  override suspend fun load()

  override suspend fun search(query: String, lang: String): Docs?
}

interface DocsRepository {
  suspend fun load()

  suspend fun search(query: String, lang: String): Docs?
}
