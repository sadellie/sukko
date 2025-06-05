package io.github.sadellie.sukko.core.data

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.sadellie.sukko.core.common.toHex
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import okio.Path.Companion.toPath
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DynamicColorSchemeProviderImplTest {
  @Test
  fun generateColorSchemeFromImage_validPng() = runTest {
    val sourceImagePath = "./testFiles/file.png".toPath()
    val sourceFile = sourceImagePath.toFile()
    if (!sourceFile.exists()) error("Not found: ${sourceFile.absolutePath}")
    withContext(Dispatchers.IO) {
      val imageBitmap = BitmapFactory.decodeFile(sourceFile.absolutePath).asImageBitmap()
      val colorScheme = generateColorSchemeFromImage(imageBitmap)
      val expectedPrimary = Color(0xFF6E6600)
      val actualPrimary = colorScheme.primary
      assertEquals(
        expectedPrimary,
        actualPrimary,
        "Expected: ${expectedPrimary.toHex()} Actual: ${actualPrimary.toHex()}",
      )
    }
  }
}
