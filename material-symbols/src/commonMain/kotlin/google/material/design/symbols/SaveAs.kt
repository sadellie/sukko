package google.material.design.symbols

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Symbols.SaveAs: ImageVector
    get() {
        if (_SaveAs != null) {
            return _SaveAs!!
        }
        _SaveAs = ImageVector.Builder(
            name = "SaveAs",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(200f, 840f)
                quadToRelative(-33f, 0f, -56.5f, -23.5f)
                reflectiveQuadTo(120f, 760f)
                verticalLineToRelative(-560f)
                quadToRelative(0f, -33f, 23.5f, -56.5f)
                reflectiveQuadTo(200f, 120f)
                horizontalLineToRelative(480f)
                lineToRelative(160f, 160f)
                verticalLineToRelative(212f)
                quadToRelative(-19f, -8f, -39.5f, -10.5f)
                reflectiveQuadToRelative(-40.5f, 0.5f)
                verticalLineToRelative(-169f)
                lineTo(647f, 200f)
                lineTo(200f, 200f)
                verticalLineToRelative(560f)
                horizontalLineToRelative(240f)
                verticalLineToRelative(80f)
                lineTo(200f, 840f)
                close()
                moveTo(200f, 200f)
                verticalLineToRelative(560f)
                verticalLineToRelative(-560f)
                close()
                moveTo(520f, 920f)
                verticalLineToRelative(-123f)
                lineToRelative(221f, -220f)
                quadToRelative(9f, -9f, 20f, -13f)
                reflectiveQuadToRelative(22f, -4f)
                quadToRelative(12f, 0f, 23f, 4.5f)
                reflectiveQuadToRelative(20f, 13.5f)
                lineToRelative(37f, 37f)
                quadToRelative(8f, 9f, 12.5f, 20f)
                reflectiveQuadToRelative(4.5f, 22f)
                quadToRelative(0f, 11f, -4f, 22.5f)
                reflectiveQuadTo(863f, 700f)
                lineTo(643f, 920f)
                lineTo(520f, 920f)
                close()
                moveTo(820f, 657f)
                lineTo(783f, 620f)
                lineTo(820f, 657f)
                close()
                moveTo(580f, 860f)
                horizontalLineToRelative(38f)
                lineToRelative(121f, -122f)
                lineToRelative(-18f, -19f)
                lineToRelative(-19f, -18f)
                lineToRelative(-122f, 121f)
                verticalLineToRelative(38f)
                close()
                moveTo(721f, 719f)
                lineTo(702f, 701f)
                lineTo(739f, 738f)
                lineTo(721f, 719f)
                close()
                moveTo(240f, 400f)
                horizontalLineToRelative(360f)
                verticalLineToRelative(-160f)
                lineTo(240f, 240f)
                verticalLineToRelative(160f)
                close()
                moveTo(480f, 720f)
                horizontalLineToRelative(4f)
                lineToRelative(116f, -115f)
                verticalLineToRelative(-5f)
                quadToRelative(0f, -50f, -35f, -85f)
                reflectiveQuadToRelative(-85f, -35f)
                quadToRelative(-50f, 0f, -85f, 35f)
                reflectiveQuadToRelative(-35f, 85f)
                quadToRelative(0f, 50f, 35f, 85f)
                reflectiveQuadToRelative(85f, 35f)
                close()
            }
        }.build()

        return _SaveAs!!
    }

@Suppress("ObjectPropertyName")
private var _SaveAs: ImageVector? = null
