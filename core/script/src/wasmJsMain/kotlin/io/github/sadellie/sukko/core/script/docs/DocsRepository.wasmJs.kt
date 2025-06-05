package io.github.sadellie.sukko.core.script.docs

actual class DocsRepositoryImpl actual constructor() : DocsRepository {
  actual override suspend fun load() {
    error("not ready")
  }

  actual override suspend fun search(query: String, lang: String): Docs? = error("not ready")
}
