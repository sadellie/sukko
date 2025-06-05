package google.material.design.symbols

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Symbols.FileExport: ImageVector
  get() {
    if (_FileExport != null) {
      return _FileExport!!
    }
    _FileExport =
      ImageVector.Builder(
          name = "FileExport",
          defaultWidth = 24.dp,
          defaultHeight = 24.dp,
          viewportWidth = 960f,
          viewportHeight = 960f,
        )
        .apply {
          path(fill = SolidColor(Color.Black)) {
            moveTo(480f, 480f)
            close()
            moveTo(320f, 777f)
            lineToRelative(-90f, 90f)
            quadToRelative(-12f, 12f, -28f, 11.5f)
            reflectiveQuadTo(174f, 866f)
            quadToRelative(-11f, -12f, -11.5f, -28f)
            reflectiveQuadToRelative(11.5f, -28f)
            lineToRelative(90f, -90f)
            horizontalLineToRelative(-50f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(174f, 680f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(214f, 640f)
            horizontalLineToRelative(146f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(400f, 680f)
            verticalLineToRelative(146f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(360f, 866f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(320f, 826f)
            verticalLineToRelative(-49f)
            close()
            moveTo(200f, 560f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(160f, 520f)
            verticalLineToRelative(-360f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(240f, 80f)
            horizontalLineToRelative(320f)
            lineToRelative(240f, 240f)
            verticalLineToRelative(480f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(720f, 880f)
            lineTo(520f, 880f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(480f, 840f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(520f, 800f)
            horizontalLineToRelative(200f)
            verticalLineToRelative(-440f)
            lineTo(560f, 360f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(520f, 320f)
            verticalLineToRelative(-160f)
            lineTo(240f, 160f)
            verticalLineToRelative(360f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(200f, 560f)
            close()
          }
        }
        .build()

    return _FileExport!!
  }

@Suppress("ObjectPropertyName") private var _FileExport: ImageVector? = null
