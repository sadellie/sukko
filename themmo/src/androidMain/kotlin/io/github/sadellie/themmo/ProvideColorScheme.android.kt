package io.github.sadellie.themmo

import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource

@Composable
actual fun provideDynamicColorScheme(isDark: Boolean): ColorScheme {
  val context = LocalContext.current
  return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
    if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
  } else {
    generateColorScheme(
      keyColor = colorResource(android.R.color.system_accent1_500),
      isDark = isDark,
      style = MonetMode.TonalSpot,
    )
  }
}
