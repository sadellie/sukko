package io.github.sadellie.sukko.core.data.script

import io.github.sadellie.sukko.core.data.BatteryInfoProvider
import io.github.sadellie.sukko.core.data.DateTimeProvider
import io.github.sadellie.sukko.core.data.DeviceInfoProvider
import io.github.sadellie.sukko.core.data.DynamicColorSchemeProvider
import io.github.sadellie.sukko.core.data.MediaInfoProvider

data class ScriptContext(
  internal val batteryInfoProvider: BatteryInfoProvider,
  internal val dateTimeProvider: DateTimeProvider,
  internal val dynamicColorSchemeProvider: DynamicColorSchemeProvider,
  internal val mediaInfoProvider: MediaInfoProvider,
  internal val getGlobalStringValue: suspend (id: Long) -> String,
  internal val getGlobalDoubleValue: suspend (id: Long) -> Double,
  internal val getGlobalBooleanValue: suspend (id: Long) -> Boolean,
  internal val setGlobalStringValue: suspend (id: Long, value: String) -> Unit,
  internal val setGlobalDoubleValue: suspend (id: Long, value: Double) -> Unit,
  internal val setGlobalBooleanValue: suspend (id: Long, value: Boolean) -> Unit,
  internal val deviceInfoProvider: DeviceInfoProvider,
) {
  internal val variableValueMemory: HashMap<VariableNode, ASTNode> = hashMapOf()
}
