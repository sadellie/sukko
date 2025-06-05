package androidx.compose.material3

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.util.fastForEach
import androidx.graphics.shapes.Cubic
import androidx.graphics.shapes.RoundedPolygon
import kotlin.math.PI
import kotlin.math.atan2

fun RoundedPolygon.toShape2(startAngle: Int = 0): Shape {
  return object : Shape {
    // Store the Path we convert from the RoundedPolygon here. The path we will be
    // manipulating and using on the createOutline would be a copy of this to ensure we
    // don't mutate the original.
    private val shapePath: Path = toPath2(startAngle = startAngle)
    private var workPath: Path? = null
    private var lastSize = Size.Unspecified

    override fun createOutline(
      size: Size,
      layoutDirection: LayoutDirection,
      density: Density,
    ): Outline {
      if (size != lastSize || workPath == null) {
        lastSize = size
        // Create a new Path if the size has changed.
        workPath = Path()
      } else {
        workPath!!.rewind()
      }
      val path = workPath!!
      path.addPath(shapePath)
      val scaleMatrix = Matrix().apply { scale(x = size.width, y = size.height) }
      // Scale and translate the path to align its center with the available size
      // center.
      path.transform(scaleMatrix)
      path.translate(size.center - path.getBounds().center)
      return Outline.Generic(path)
    }
  }
}

private fun RoundedPolygon.toPath2(
  path: Path = Path(),
  startAngle: Int = 270,
  repeatPath: Boolean = false,
  closePath: Boolean = true,
): Path {
  pathFromCubics(
    path = path,
    startAngle = startAngle,
    repeatPath = repeatPath,
    closePath = closePath,
    cubics = cubics,
    rotationPivotX = centerX,
    rotationPivotY = centerY,
  )
  return path
}

private fun pathFromCubics(
  path: Path,
  startAngle: Int,
  repeatPath: Boolean,
  closePath: Boolean,
  cubics: List<Cubic>,
  rotationPivotX: Float,
  rotationPivotY: Float,
) {
  var first = true
  var firstCubic: Cubic? = null
  path.rewind()
  cubics.fastForEach {
    if (first) {
      path.moveTo(it.anchor0X, it.anchor0Y)
      if (startAngle != 0) {
        firstCubic = it
      }
      first = false
    }
    path.cubicTo(it.control0X, it.control0Y, it.control1X, it.control1Y, it.anchor1X, it.anchor1Y)
  }
  if (repeatPath) {
    var firstInRepeat = true
    cubics.fastForEach {
      if (firstInRepeat) {
        path.lineTo(it.anchor0X, it.anchor0Y)
        firstInRepeat = false
      }
      path.cubicTo(it.control0X, it.control0Y, it.control1X, it.control1Y, it.anchor1X, it.anchor1Y)
    }
  }

  if (closePath) path.close()

  if (startAngle != 0 && firstCubic != null) {
    val angleToFirstCubic =
      radiansToDegrees(
        atan2(y = cubics[0].anchor0Y - rotationPivotY, x = cubics[0].anchor0X - rotationPivotX)
      )
    // Rotate the Path to to start from the given angle.
    path.transform(Matrix().apply { rotateZ(-angleToFirstCubic + startAngle) })
  }
}

private fun radiansToDegrees(radians: Float): Float {
  return (radians * 180.0 / PI).toFloat()
}
