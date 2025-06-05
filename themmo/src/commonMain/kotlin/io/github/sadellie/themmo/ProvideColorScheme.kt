package io.github.sadellie.themmo

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.isSpecified

@Composable
fun commonProvideColorScheme(
  isSystemDark: Boolean,
  currentThemingMode: ThemingMode,
  isDynamicThemeEnabled: Boolean,
  currentCustomColor: Color,
  currentMonetMode: MonetMode,
  lightColorScheme: ColorScheme,
  darkColorScheme: ColorScheme,
  isAmoledThemeEnabled: Boolean,
): ColorScheme {
  return when (currentThemingMode) {
    ThemingMode.AUTO -> {
      if (isSystemDark) {
        provideDarkColorScheme(
          isDynamicThemeEnabled = isDynamicThemeEnabled,
          currentCustomColor = currentCustomColor,
          currentMonetMode = currentMonetMode,
          darkColorScheme = darkColorScheme,
          isAmoledThemeEnabled = isAmoledThemeEnabled,
        )
      } else {
        provideLightColorScheme(
          isDynamicThemeEnabled = isDynamicThemeEnabled,
          currentCustomColor = currentCustomColor,
          currentMonetMode = currentMonetMode,
          lightColorScheme = lightColorScheme,
        )
      }
    }

    ThemingMode.FORCE_LIGHT ->
      provideLightColorScheme(
        isDynamicThemeEnabled = isDynamicThemeEnabled,
        currentCustomColor = currentCustomColor,
        currentMonetMode = currentMonetMode,
        lightColorScheme = lightColorScheme,
      )

    ThemingMode.FORCE_DARK ->
      provideDarkColorScheme(
        isDynamicThemeEnabled = isDynamicThemeEnabled,
        currentCustomColor = currentCustomColor,
        currentMonetMode = currentMonetMode,
        darkColorScheme = darkColorScheme,
        isAmoledThemeEnabled = isAmoledThemeEnabled,
      )
  }
}

@Composable
private fun provideLightColorScheme(
  isDynamicThemeEnabled: Boolean,
  currentCustomColor: Color,
  currentMonetMode: MonetMode,
  lightColorScheme: ColorScheme,
): ColorScheme {
  return when {
    isDynamicThemeEnabled -> provideDynamicColorScheme(isDark = false)
    currentCustomColor.isSpecified ->
      generateColorScheme(keyColor = currentCustomColor, isDark = false, style = currentMonetMode)
    else -> lightColorScheme
  }
}

@Composable
private fun provideDarkColorScheme(
  isDynamicThemeEnabled: Boolean,
  currentCustomColor: Color,
  currentMonetMode: MonetMode,
  darkColorScheme: ColorScheme,
  isAmoledThemeEnabled: Boolean,
): ColorScheme {
  val darkColorScheme: ColorScheme =
    when {
      isDynamicThemeEnabled -> provideDynamicColorScheme(isDark = true)
      currentCustomColor.isSpecified ->
        generateColorScheme(keyColor = currentCustomColor, isDark = true, style = currentMonetMode)
      else -> darkColorScheme
    }

  return if (isAmoledThemeEnabled) darkColorScheme.toAmoled() else darkColorScheme
}

@Composable expect fun provideDynamicColorScheme(isDark: Boolean): ColorScheme

private fun ColorScheme.toAmoled(): ColorScheme {
  return ColorScheme(
    primary = this.primary.darken(AMOLED_MAIN_FACTOR),
    onPrimary = this.onPrimary.darken(AMOLED_TEXT_FACTOR),
    primaryContainer = this.primaryContainer.darken(AMOLED_MAIN_FACTOR),
    onPrimaryContainer = this.onPrimaryContainer.darken(AMOLED_TEXT_FACTOR),
    inversePrimary = this.inversePrimary.darken(AMOLED_MAIN_FACTOR),
    secondary = this.secondary.darken(AMOLED_MAIN_FACTOR),
    onSecondary = this.onSecondary.darken(AMOLED_TEXT_FACTOR),
    secondaryContainer = this.secondaryContainer.darken(AMOLED_MAIN_FACTOR),
    onSecondaryContainer = this.onSecondaryContainer.darken(AMOLED_TEXT_FACTOR),
    tertiary = this.tertiary.darken(AMOLED_MAIN_FACTOR),
    onTertiary = this.onTertiary.darken(AMOLED_TEXT_FACTOR),
    tertiaryContainer = this.tertiaryContainer.darken(AMOLED_MAIN_FACTOR),
    onTertiaryContainer = this.onTertiaryContainer.darken(AMOLED_TEXT_FACTOR),
    background = Color.Black,
    onBackground = this.onBackground.darken(AMOLED_TEXT_FACTOR),
    surface = Color.Black,
    onSurface = this.onSurface.darken(AMOLED_TEXT_FACTOR),
    surfaceVariant = this.surfaceVariant.darken(AMOLED_MAIN_FACTOR),
    onSurfaceVariant = this.onSurfaceVariant.darken(AMOLED_TEXT_FACTOR),
    surfaceTint = this.surfaceTint.darken(AMOLED_MAIN_FACTOR),
    inverseSurface = this.inverseSurface.darken(AMOLED_MAIN_FACTOR),
    inverseOnSurface = this.inverseOnSurface.darken(AMOLED_TEXT_FACTOR),
    error = this.error.darken(AMOLED_MAIN_FACTOR),
    onError = this.onError.darken(AMOLED_TEXT_FACTOR),
    errorContainer = this.errorContainer.darken(AMOLED_MAIN_FACTOR),
    onErrorContainer = this.onErrorContainer.darken(AMOLED_TEXT_FACTOR),
    outline = this.outline.darken(AMOLED_MAIN_FACTOR),
    outlineVariant = this.outlineVariant.darken(AMOLED_MAIN_FACTOR),
    scrim = this.scrim.darken(AMOLED_MAIN_FACTOR),
    surfaceBright = this.surfaceBright.darken(AMOLED_MAIN_FACTOR),
    surfaceDim = this.surfaceDim.darken(AMOLED_MAIN_FACTOR),
    surfaceContainer = this.surfaceContainer.darken(AMOLED_MAIN_FACTOR),
    surfaceContainerHigh = this.surfaceContainerHigh.darken(AMOLED_MAIN_FACTOR),
    surfaceContainerHighest = this.surfaceContainerHighest.darken(AMOLED_MAIN_FACTOR),
    surfaceContainerLow = this.surfaceContainerLow.darken(AMOLED_MAIN_FACTOR),
    surfaceContainerLowest = this.surfaceContainerLowest.darken(AMOLED_MAIN_FACTOR),
    primaryFixed = this.primaryFixed.darken(AMOLED_MAIN_FACTOR),
    primaryFixedDim = this.primaryFixedDim.darken(AMOLED_MAIN_FACTOR),
    onPrimaryFixed = this.onPrimaryFixed.darken(AMOLED_TEXT_FACTOR),
    onPrimaryFixedVariant = this.onPrimaryFixedVariant,
    secondaryFixed = this.secondaryFixed.darken(AMOLED_MAIN_FACTOR),
    secondaryFixedDim = this.secondaryFixedDim.darken(AMOLED_MAIN_FACTOR),
    onSecondaryFixed = this.onSecondaryFixed.darken(AMOLED_TEXT_FACTOR),
    onSecondaryFixedVariant = this.onSecondaryFixedVariant,
    tertiaryFixed = this.tertiaryFixed.darken(AMOLED_MAIN_FACTOR),
    tertiaryFixedDim = this.tertiaryFixedDim.darken(AMOLED_MAIN_FACTOR),
    onTertiaryFixed = this.onTertiaryFixed.darken(AMOLED_TEXT_FACTOR),
    onTertiaryFixedVariant = this.onTertiaryFixedVariant.darken(AMOLED_TEXT_FACTOR),
  )
}

private fun Color.darken(alpha: Float) = this.copy(alpha = alpha).compositeOver(Color.Black)

private const val AMOLED_MAIN_FACTOR = 0.65f
private const val AMOLED_TEXT_FACTOR = 0.85f
