package google.material.design.symbols

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Symbols.Image: ImageVector
  get() {
    if (_Image != null) {
      return _Image!!
    }
    _Image =
      ImageVector.Builder(
          name = "Image",
          defaultWidth = 24.dp,
          defaultHeight = 24.dp,
          viewportWidth = 960f,
          viewportHeight = 960f,
        )
        .apply {
          path(fill = SolidColor(Color.Black)) {
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
            moveTo(200f, 760f)
            horizontalLineToRelative(560f)
            verticalLineToRelative(-560f)
            lineTo(200f, 200f)
            verticalLineToRelative(560f)
            close()
            moveTo(200f, 760f)
            verticalLineToRelative(-560f)
            verticalLineToRelative(560f)
            close()
            moveTo(280f, 680f)
            horizontalLineToRelative(400f)
            quadToRelative(12f, 0f, 18f, -11f)
            reflectiveQuadToRelative(-2f, -21f)
            lineTo(586f, 501f)
            quadToRelative(-6f, -8f, -16f, -8f)
            reflectiveQuadToRelative(-16f, 8f)
            lineTo(450f, 640f)
            lineToRelative(-74f, -99f)
            quadToRelative(-6f, -8f, -16f, -8f)
            reflectiveQuadToRelative(-16f, 8f)
            lineToRelative(-80f, 107f)
            quadToRelative(-8f, 10f, -2f, 21f)
            reflectiveQuadToRelative(18f, 11f)
            close()
          }
        }
        .build()

    return _Image!!
  }

@Suppress("ObjectPropertyName") private var _Image: ImageVector? = null
