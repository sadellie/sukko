package io.github.sadellie.sukko.core.data

import kotlin.time.Clock
import kotlin.time.Instant

internal class DateTimeProviderImpl : DateTimeProvider {
  override val instant: Instant by lazy { Clock.System.now() }
}
