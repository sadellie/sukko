package google.material.design.symbols

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Symbols.LineEnd: ImageVector
    get() {
        if (_LineEnd != null) {
            return _LineEnd!!
        }
        _LineEnd = ImageVector.Builder(
            name = "LineEnd",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color(0xFFE3E3E3))) {
                moveTo(780f, 580f)
                quadToRelative(-31f, 0f, -56f, -17f)
                reflectiveQuadToRelative(-36f, -43f)
                lineTo(80f, 520f)
                verticalLineToRelative(-80f)
                horizontalLineToRelative(608f)
                quadToRelative(11f, -26f, 36f, -43f)
                reflectiveQuadToRelative(56f, -17f)
                quadToRelative(42f, 0f, 71f, 29f)
                reflectiveQuadToRelative(29f, 71f)
                quadToRelative(0f, 42f, -29f, 71f)
                reflectiveQuadToRelative(-71f, 29f)
                close()
            }
        }.build()

        return _LineEnd!!
    }

@Suppress("ObjectPropertyName")
private var _LineEnd: ImageVector? = null
