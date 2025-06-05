package google.material.design.symbols

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Symbols.TableRows: ImageVector
  get() {
    if (_TableRows != null) {
      return _TableRows!!
    }
    _TableRows =
      ImageVector.Builder(
          name = "TableRows",
          defaultWidth = 24.dp,
          defaultHeight = 24.dp,
          viewportWidth = 960f,
          viewportHeight = 960f,
        )
        .apply {
          path(fill = SolidColor(Color.Black)) {
            moveTo(760f, 760f)
            verticalLineToRelative(-120f)
            lineTo(200f, 640f)
            verticalLineToRelative(120f)
            horizontalLineToRelative(560f)
            close()
            moveTo(760f, 560f)
            verticalLineToRelative(-160f)
            lineTo(200f, 400f)
            verticalLineToRelative(160f)
            horizontalLineToRelative(560f)
            close()
            moveTo(760f, 320f)
            verticalLineToRelative(-120f)
            lineTo(200f, 200f)
            verticalLineToRelative(120f)
            horizontalLineToRelative(560f)
            close()
            moveTo(200f, 840f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(120f, 760f)
            verticalLineToRelative(-560f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(200f, 120f)
            horizontalLineToRelative(560f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(840f, 200f)
            verticalLineToRelative(560f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(760f, 840f)
            lineTo(200f, 840f)
            close()
          }
        }
        .build()

    return _TableRows!!
  }

@Suppress("ObjectPropertyName") private var _TableRows: ImageVector? = null
