package io.github.sadellie.sukko.core.common

import okio.Path

sealed class CommonExceptions : Exception() {
  class FileAlreadyExistsException(val path: Path) : CommonExceptions()
}
