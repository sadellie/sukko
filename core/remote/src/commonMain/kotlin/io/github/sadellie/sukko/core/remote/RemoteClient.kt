package io.github.sadellie.sukko.core.remote

import io.ktor.client.HttpClient
import io.ktor.client.plugins.cache.HttpCache

// todo use in image loader
class RemoteClient {
  val ktorClient = HttpClient { install(HttpCache.Companion) }
}
