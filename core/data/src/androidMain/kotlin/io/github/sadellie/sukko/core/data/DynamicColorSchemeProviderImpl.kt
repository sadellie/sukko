package io.github.sadellie.sukko.core.data

import android.content.Context
import android.content.res.Configuration
import android.graphics.ImageDecoder
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.core.net.toUri
import com.kmpalette.palette.graphics.Palette
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.hct.Hct
import com.materialkolor.scheme.DynamicScheme
import com.materialkolor.scheme.SchemeExpressive
import io.github.sadellie.sukko.core.model.basic.M3Color
import io.github.sadellie.sukko.core.model.data.DynamicColorSchemeProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class DynamicColorSchemeProviderImpl(
  private val context: Context,
  private val imageUriProvider: ImageUriProvider,
) : DynamicColorSchemeProvider {
  private val cacheMutex by lazy { Mutex() }
  private val imageColorSchemes by lazy { hashMapOf<String, ColorScheme>() }
  private val systemColorScheme by lazy {
    val uiMode = context.resources.configuration.uiMode
    val isSystemInDarkTheme =
      (uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    val m3ColorScheme =
      if (isSystemInDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    m3ColorScheme
  }

  override fun extractHexFromSystemColorScheme(m3ColorName: String): String =
    extractHexFromColorScheme(m3ColorName, systemColorScheme)

  override suspend fun extractHexFromImageColorScheme(
    m3ColorName: String,
    imageUri: String,
  ): String =
    cacheMutex.withLock {
      val cached =
        imageColorSchemes.getOrPut(imageUri) {
          val localImageUri = imageUriProvider.loadAndCacheImage(imageUri)
          val imageBitmap =
            ImageDecoder.decodeBitmap(
                ImageDecoder.createSource(context.contentResolver, localImageUri.toUri())
              )
              .asImageBitmap()
          generateColorSchemeFromImage(imageBitmap)
        }
      extractHexFromColorScheme(m3ColorName, cached)
    }

  override fun getColorFromSystemColorScheme(m3Color: M3Color): Color =
    m3Color.extractFromScheme(systemColorScheme)
}

internal suspend fun generateColorSchemeFromImage(imageBitmap: ImageBitmap) =
  withContext(Dispatchers.Default) {
    val dominantColor = Palette.from(imageBitmap).generate().getDominantColor(Color.Red.toArgb())
    SchemeExpressive(
        sourceColorHct = Hct.fromInt(dominantColor),
        isDark = false,
        contrastLevel = 0.0,
        specVersion = ColorSpec.SpecVersion.SPEC_2025,
      )
      .toColorScheme()
  }

private fun DynamicScheme.toColorScheme() =
  ColorScheme(
    primary = Color(primary),
    onPrimary = Color(onPrimary),
    primaryContainer = Color(primaryContainer),
    onPrimaryContainer = Color(onPrimaryContainer),
    inversePrimary = Color(inversePrimary),
    secondary = Color(secondary),
    onSecondary = Color(onSecondary),
    secondaryContainer = Color(secondaryContainer),
    onSecondaryContainer = Color(onSecondaryContainer),
    tertiary = Color(tertiary),
    onTertiary = Color(onTertiary),
    tertiaryContainer = Color(tertiaryContainer),
    onTertiaryContainer = Color(onTertiaryContainer),
    background = Color(background),
    onBackground = Color(onBackground),
    surface = Color(surface),
    onSurface = Color(onSurface),
    surfaceVariant = Color(surfaceVariant),
    onSurfaceVariant = Color(onSurfaceVariant),
    surfaceTint = Color(surfaceTint),
    inverseSurface = Color(inverseSurface),
    inverseOnSurface = Color(inverseOnSurface),
    error = Color(error),
    onError = Color(onError),
    errorContainer = Color(errorContainer),
    onErrorContainer = Color(onErrorContainer),
    outline = Color(outline),
    outlineVariant = Color(outlineVariant),
    scrim = Color(scrim),
    surfaceBright = Color(surfaceBright),
    surfaceDim = Color(surfaceDim),
    surfaceContainer = Color(surfaceContainer),
    surfaceContainerHigh = Color(surfaceContainerHigh),
    surfaceContainerHighest = Color(surfaceContainerHighest),
    surfaceContainerLow = Color(surfaceContainerLow),
    surfaceContainerLowest = Color(surfaceContainerLowest),
    primaryFixed = Color(primaryFixed),
    primaryFixedDim = Color(primaryFixedDim),
    onPrimaryFixed = Color(onPrimaryFixed),
    onPrimaryFixedVariant = Color(onPrimaryFixedVariant),
    secondaryFixed = Color(secondaryFixed),
    secondaryFixedDim = Color(secondaryFixedDim),
    onSecondaryFixed = Color(onSecondaryFixed),
    onSecondaryFixedVariant = Color(onSecondaryFixedVariant),
    tertiaryFixed = Color(tertiaryFixed),
    tertiaryFixedDim = Color(tertiaryFixedDim),
    onTertiaryFixed = Color(onTertiaryFixed),
    onTertiaryFixedVariant = Color(onTertiaryFixedVariant),
  )
