package io.github.sadellie.sukko.core.data

import kotlin.time.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toLocalDateTime

/** Provider for date and time. Includes utilities */
interface DateTimeProvider {
  /** Static instant. Should be lazy */
  val instant: Instant

  /** Timestamp without timezone in seconds */
  val currentTimestamp: Long
    get() = instant.epochSeconds

  /** Format [instant] using provided [format] */
  @OptIn(FormatStringsInDatetimeFormats::class)
  fun currentDate(format: String): String =
    formatInstant(instant, format, TimeZone.currentSystemDefault())

  /** Offset [instant] to [timeZoneId] and format it using provided [format] */
  @OptIn(FormatStringsInDatetimeFormats::class)
  fun currentDateWithTimeZone(format: String, timeZoneId: String): String =
    formatInstant(instant, format, TimeZone.of(timeZoneId))

  /** Same as [currentDate] but uses [timestamp] instead of [instant] */
  @OptIn(FormatStringsInDatetimeFormats::class)
  fun formatTimestamp(timestamp: Long, format: String): String =
    formatInstant(Instant.fromEpochSeconds(timestamp), format, TimeZone.currentSystemDefault())

  @OptIn(FormatStringsInDatetimeFormats::class)
  private fun formatInstant(instant: Instant, format: String, timeZone: TimeZone): String {
    val localDateTimeInTimeZone = instant.toLocalDateTime(timeZone)
    val dateTimeFormat = LocalDateTime.Format { byUnicodePattern(format) }
    val formattedDate = localDateTimeInTimeZone.format(dateTimeFormat)
    return formattedDate
  }
}
