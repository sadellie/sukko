package io.github.sadellie.sukko.core.model.data

/** Provider for device battery and power information */
interface BatteryInfoProvider {
  /** Current battery capacity. 0 to 100 inclusive */
  val capacity: Int

  /** Battery status. See docs or implementation for possible values */
  val status: String

  /** How may second until device charges to 100 or discharges to 0 */
  val chargeDischargeSeconds: Int
}
