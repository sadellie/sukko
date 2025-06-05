package google.material.design.symbols

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Symbols.SentimentSatisfied: ImageVector
    get() {
        if (_SentimentSatisfied != null) {
            return _SentimentSatisfied!!
        }
        _SentimentSatisfied = ImageVector.Builder(
            name = "SentimentSatisfied",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(480f, 700f)
                quadToRelative(53f, 0f, 100.5f, -23f)
                reflectiveQuadToRelative(76.5f, -67f)
                quadToRelative(11f, -17f, 3f, -33.5f)
                reflectiveQuadTo(634f, 560f)
                quadToRelative(-8f, 0f, -14.5f, 3.5f)
                reflectiveQuadTo(609f, 574f)
                quadToRelative(-23f, 31f, -57f, 48.5f)
                reflectiveQuadTo(480f, 640f)
                quadToRelative(-38f, 0f, -72f, -17.5f)
                reflectiveQuadTo(351f, 574f)
                quadToRelative(-5f, -7f, -11.5f, -10.5f)
                reflectiveQuadTo(325f, 560f)
                quadToRelative(-18f, 0f, -26f, 16f)
                reflectiveQuadToRelative(3f, 32f)
                quadToRelative(29f, 45f, 76.5f, 68.5f)
                reflectiveQuadTo(480f, 700f)
                close()
                moveTo(620f, 440f)
                quadToRelative(25f, 0f, 42.5f, -17.5f)
                reflectiveQuadTo(680f, 380f)
                quadToRelative(0f, -25f, -17.5f, -42.5f)
                reflectiveQuadTo(620f, 320f)
                quadToRelative(-25f, 0f, -42.5f, 17.5f)
                reflectiveQuadTo(560f, 380f)
                quadToRelative(0f, 25f, 17.5f, 42.5f)
                reflectiveQuadTo(620f, 440f)
                close()
                moveTo(340f, 440f)
                quadToRelative(25f, 0f, 42.5f, -17.5f)
                reflectiveQuadTo(400f, 380f)
                quadToRelative(0f, -25f, -17.5f, -42.5f)
                reflectiveQuadTo(340f, 320f)
                quadToRelative(-25f, 0f, -42.5f, 17.5f)
                reflectiveQuadTo(280f, 380f)
                quadToRelative(0f, 25f, 17.5f, 42.5f)
                reflectiveQuadTo(340f, 440f)
                close()
                moveTo(480f, 880f)
                quadToRelative(-83f, 0f, -156f, -31.5f)
                reflectiveQuadTo(197f, 763f)
                quadToRelative(-54f, -54f, -85.5f, -127f)
                reflectiveQuadTo(80f, 480f)
                quadToRelative(0f, -83f, 31.5f, -156f)
                reflectiveQuadTo(197f, 197f)
                quadToRelative(54f, -54f, 127f, -85.5f)
                reflectiveQuadTo(480f, 80f)
                quadToRelative(83f, 0f, 156f, 31.5f)
                reflectiveQuadTo(763f, 197f)
                quadToRelative(54f, 54f, 85.5f, 127f)
                reflectiveQuadTo(880f, 480f)
                quadToRelative(0f, 83f, -31.5f, 156f)
                reflectiveQuadTo(763f, 763f)
                quadToRelative(-54f, 54f, -127f, 85.5f)
                reflectiveQuadTo(480f, 880f)
                close()
                moveTo(480f, 480f)
                close()
                moveTo(480f, 800f)
                quadToRelative(134f, 0f, 227f, -93f)
                reflectiveQuadToRelative(93f, -227f)
                quadToRelative(0f, -134f, -93f, -227f)
                reflectiveQuadToRelative(-227f, -93f)
                quadToRelative(-134f, 0f, -227f, 93f)
                reflectiveQuadToRelative(-93f, 227f)
                quadToRelative(0f, 134f, 93f, 227f)
                reflectiveQuadToRelative(227f, 93f)
                close()
            }
        }.build()

        return _SentimentSatisfied!!
    }

@Suppress("ObjectPropertyName")
private var _SentimentSatisfied: ImageVector? = null
