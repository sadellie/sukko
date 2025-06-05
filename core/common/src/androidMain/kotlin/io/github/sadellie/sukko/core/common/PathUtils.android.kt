package io.github.sadellie.sukko.core.common

import okio.Path.Companion.toPath

actual val ASSET_PATH by lazy { "file:///android_asset/".toPath() }
