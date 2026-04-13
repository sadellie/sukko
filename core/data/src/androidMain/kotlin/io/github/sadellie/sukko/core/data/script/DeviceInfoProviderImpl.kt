package io.github.sadellie.sukko.core.data.script

import android.os.Build
import io.github.sadellie.sukko.core.data.DeviceInfoProvider

internal class DeviceInfoProviderImpl : DeviceInfoProvider {
  override val model: String = Build.MODEL
}
