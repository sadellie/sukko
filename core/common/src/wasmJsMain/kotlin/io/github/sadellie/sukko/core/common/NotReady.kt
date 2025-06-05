package io.github.sadellie.sukko.core.common

val notReady: Nothing
  get() = error("WASM is not ready yet")
