package io.github.sadellie.sukko.core.script.docs

import kotlinx.coroutines.test.runTest
import okio.Path.Companion.toPath
import kotlin.test.Test

class DocsRepositoryImplTest {
  @Test
  fun loadDocs() = runTest {
    val repo = DocsRepositoryImpl()
    val dir = System.getProperty("user.dir") ?: error("No used.dir")
    val scriptingFilePath = dir.toPath() / "src/commonMain/composeResources/files/scripting.json"
    scriptingFilePath.toFile().inputStream().use {
      // should not fail
      repo.parseAndUpdateDocs(it)
    }
  }
}
