package io.github.sadellie.sukko.core.data

data class ScriptableEvaluatorContext(
  val dynamicColorSchemeProvider: DynamicColorSchemeProvider,
  val batteryInfoProvider: BatteryInfoProvider,
  val dateTimeProvider: DateTimeProvider,
  val deviceInfoProvider: DeviceInfoProvider,
  val mediaInfoProvider: MediaInfoProvider,
)
