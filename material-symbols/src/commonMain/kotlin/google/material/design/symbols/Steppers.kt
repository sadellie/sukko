package google.material.design.symbols

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Symbols.Steppers: ImageVector
    get() {
        if (_Steppers != null) {
            return _Steppers!!
        }
        _Steppers = ImageVector.Builder(
            name = "Steppers",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color(0xFFE3E3E3))) {
                moveTo(115f, 565f)
                quadToRelative(-35f, -35f, -35f, -85f)
                reflectiveQuadToRelative(35f, -85f)
                quadToRelative(35f, -35f, 85f, -35f)
                reflectiveQuadToRelative(85f, 35f)
                quadToRelative(35f, 35f, 35f, 85f)
                reflectiveQuadToRelative(-35f, 85f)
                quadToRelative(-35f, 35f, -85f, 35f)
                reflectiveQuadToRelative(-85f, -35f)
                close()
                moveTo(228.5f, 508.5f)
                quadTo(240f, 497f, 240f, 480f)
                reflectiveQuadToRelative(-11.5f, -28.5f)
                quadTo(217f, 440f, 200f, 440f)
                reflectiveQuadToRelative(-28.5f, 11.5f)
                quadTo(160f, 463f, 160f, 480f)
                reflectiveQuadToRelative(11.5f, 28.5f)
                quadTo(183f, 520f, 200f, 520f)
                reflectiveQuadToRelative(28.5f, -11.5f)
                close()
                moveTo(395f, 565f)
                quadToRelative(-35f, -35f, -35f, -85f)
                reflectiveQuadToRelative(35f, -85f)
                quadToRelative(35f, -35f, 85f, -35f)
                reflectiveQuadToRelative(85f, 35f)
                quadToRelative(35f, 35f, 35f, 85f)
                reflectiveQuadToRelative(-35f, 85f)
                quadToRelative(-35f, 35f, -85f, 35f)
                reflectiveQuadToRelative(-85f, -35f)
                close()
                moveTo(508.5f, 508.5f)
                quadTo(520f, 497f, 520f, 480f)
                reflectiveQuadToRelative(-11.5f, -28.5f)
                quadTo(497f, 440f, 480f, 440f)
                reflectiveQuadToRelative(-28.5f, 11.5f)
                quadTo(440f, 463f, 440f, 480f)
                reflectiveQuadToRelative(11.5f, 28.5f)
                quadTo(463f, 520f, 480f, 520f)
                reflectiveQuadToRelative(28.5f, -11.5f)
                close()
                moveTo(675f, 565f)
                quadToRelative(-35f, -35f, -35f, -85f)
                reflectiveQuadToRelative(35f, -85f)
                quadToRelative(35f, -35f, 85f, -35f)
                reflectiveQuadToRelative(85f, 35f)
                quadToRelative(35f, 35f, 35f, 85f)
                reflectiveQuadToRelative(-35f, 85f)
                quadToRelative(-35f, 35f, -85f, 35f)
                reflectiveQuadToRelative(-85f, -35f)
                close()
            }
        }.build()

        return _Steppers!!
    }

@Suppress("ObjectPropertyName")
private var _Steppers: ImageVector? = null
