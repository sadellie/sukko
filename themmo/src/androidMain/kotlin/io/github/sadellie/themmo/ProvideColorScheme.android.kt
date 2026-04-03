package io.github.sadellie.themmo

import android.app.WallpaperColors
import android.app.WallpaperManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import io.github.sadellie.themmo.core.MonetMode

@Composable
internal actual fun provideDynamicColorScheme(
  isDark: Boolean,
  defaultColorScheme: ColorScheme,
): ColorScheme {
  return when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
      val context = LocalContext.current
      if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      generateColorScheme(
        keyColor = colorResource(android.R.color.system_accent1_500),
        isDark = isDark,
        style = MonetMode.TonalSpot,
      )
    }
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1 -> {
      val context = LocalContext.current
      generateColorScheme(
        keyColor = extractWallpaperPrimary(context) ?: return defaultColorScheme,
        isDark = isDark,
        style = MonetMode.TonalSpot,
      )
    }
    else -> defaultColorScheme
  }
}

/**
 * Extract primary color from device wallpaper.
 *
 * @param context Context
 * @return Primary color of current wallpaper image.
 */
@RequiresApi(Build.VERSION_CODES.O_MR1)
private fun extractWallpaperPrimary(context: Context): Color? {
  val wallpaperColors: WallpaperColors =
    WallpaperManager.getInstance(context).getWallpaperColors(WallpaperManager.FLAG_SYSTEM)
      ?: return null

  return Color(
    red = wallpaperColors.primaryColor.red(),
    green = wallpaperColors.primaryColor.green(),
    blue = wallpaperColors.primaryColor.blue(),
  )
}
