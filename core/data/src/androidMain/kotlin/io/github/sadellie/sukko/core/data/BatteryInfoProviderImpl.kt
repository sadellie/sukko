package io.github.sadellie.sukko.core.data

import android.content.Context
import android.os.BatteryManager
import android.os.PowerManager
import kotlin.time.Duration.Companion.milliseconds

internal class BatteryInfoProviderImpl(context: Context) : BatteryInfoProvider {
  private val batteryManager by lazy {
    context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
  }

  override val capacity: Int by lazy {
    batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
  }

  override val status: String by lazy {
    when (batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_STATUS)) {
      BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "NOT_CHARGING"
      BatteryManager.BATTERY_STATUS_DISCHARGING -> "DISCHARGING"
      BatteryManager.BATTERY_STATUS_CHARGING -> "CHARGING"
      BatteryManager.BATTERY_STATUS_FULL -> "FULL"
      else -> "UNKNOWN"
    }
  }

  override val chargeDischargeSeconds by lazy {
    val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    val chargeInSeconds =
      powerManager.batteryDischargePrediction?.toSeconds()
        ?: batteryManager.computeChargeTimeRemaining().milliseconds.inWholeSeconds
    chargeInSeconds.toInt()
  }
}
