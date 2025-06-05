package io.github.sadellie.themmo

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

@Composable actual fun provideDynamicColorScheme(isDark: Boolean): ColorScheme = darkColorScheme()
