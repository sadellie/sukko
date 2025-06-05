package google.material.design.symbols

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Symbols.ExpandAll: ImageVector
    get() {
        if (_ExpandAll != null) {
            return _ExpandAll!!
        }
        _ExpandAll = ImageVector.Builder(
            name = "ExpandAll",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveToRelative(480f, 766f)
                lineToRelative(155f, -155f)
                quadToRelative(12f, -12f, 28f, -12f)
                reflectiveQuadToRelative(28f, 12f)
                quadToRelative(12f, 12f, 12f, 28.5f)
                reflectiveQuadTo(691f, 668f)
                lineTo(537f, 823f)
                quadToRelative(-23f, 23f, -57f, 23f)
                reflectiveQuadToRelative(-57f, -23f)
                lineTo(268f, 668f)
                quadToRelative(-12f, -12f, -11.5f, -28.5f)
                reflectiveQuadTo(269f, 611f)
                quadToRelative(12f, -12f, 28.5f, -12f)
                reflectiveQuadToRelative(28.5f, 12f)
                lineToRelative(154f, 155f)
                close()
                moveTo(480f, 194f)
                lineTo(326f, 348f)
                quadToRelative(-12f, 12f, -28f, 11.5f)
                reflectiveQuadTo(270f, 348f)
                quadToRelative(-12f, -12f, -12.5f, -28.5f)
                reflectiveQuadTo(269f, 291f)
                lineToRelative(154f, -154f)
                quadToRelative(23f, -23f, 57f, -23f)
                reflectiveQuadToRelative(57f, 23f)
                lineToRelative(154f, 154f)
                quadToRelative(12f, 12f, 11.5f, 28.5f)
                reflectiveQuadTo(690f, 348f)
                quadToRelative(-12f, 11f, -28f, 11.5f)
                reflectiveQuadTo(634f, 348f)
                lineTo(480f, 194f)
                close()
            }
        }.build()

        return _ExpandAll!!
    }

@Suppress("ObjectPropertyName")
private var _ExpandAll: ImageVector? = null
