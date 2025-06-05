package io.github.sadellie.sukko.core.model.basic

import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.toShape2
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.star
import io.github.sadellie.sukko.core.common.DpSerializer
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.core_model_shape_source_arch
import io.github.sadellie.sukko.resources.core_model_shape_source_circle
import io.github.sadellie.sukko.resources.core_model_shape_source_rectangle_dp
import io.github.sadellie.sukko.resources.core_model_shape_source_rectangle_percent
import io.github.sadellie.sukko.resources.core_model_shape_source_slanted
import io.github.sadellie.sukko.resources.core_model_shape_source_star
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.jetbrains.compose.resources.StringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Serializable
sealed interface ShapeSource {
  companion object {
    fun allShapes(): List<ShapeSource> =
      listOf(CutCornersDp(), CutCornersPercent(), Circle, Slanted, Arch, Star())
  }

  @Serializable
  sealed interface Rectangle : ShapeSource {
    val isRounded: Boolean
  }

  @Serializable
  sealed interface Material : ShapeSource {
    fun roundedPolygon(): RoundedPolygon

    override fun getShape() = roundedPolygon().normalized().toShape2()
  }

  @Serializable
  data class CutCornersDp(
    override val isRounded: Boolean = true,
    @Serializable(DpSerializer::class) val size: Dp = 16.dp,
  ) : Rectangle {
    @Transient override val displayName = Res.string.core_model_shape_source_rectangle_dp

    override fun getShape(): Shape {
      if (size == 0.dp) return RectangleShape
      return if (isRounded) RoundedCornerShape(size) else CutCornerShape(size)
    }
  }

  @Serializable
  data class CutCornersPercent(override val isRounded: Boolean = true, val percent: Int = 20) :
    Rectangle {
    @Transient override val displayName = Res.string.core_model_shape_source_rectangle_percent

    override fun getShape(): Shape {
      if (percent == 0) return RectangleShape
      return if (isRounded) RoundedCornerShape(percent) else CutCornerShape(percent)
    }
  }

  @Serializable
  data object Circle : Material {
    @Transient override val displayName = Res.string.core_model_shape_source_circle

    override fun roundedPolygon() = MaterialShapes.Circle
  }

  @Serializable
  data object Slanted : Material {
    @Transient override val displayName = Res.string.core_model_shape_source_slanted

    override fun roundedPolygon() = MaterialShapes.Slanted
  }

  @Serializable
  data object Arch : Material {
    @Transient override val displayName = Res.string.core_model_shape_source_arch

    override fun roundedPolygon() = MaterialShapes.Arch
  }

  @Serializable
  data class Star(
    val numVerticesPerRadius: Int = 7,
    // 0..0.9f
    val innerRadius: Float = 0.75f,
    // 0..1
    val cornerRounding: Float = 0.5f,
  ) : Material {
    @Transient override val displayName = Res.string.core_model_shape_source_star

    override fun roundedPolygon() =
      RoundedPolygon.star(
        numVerticesPerRadius = numVerticesPerRadius,
        innerRadius = innerRadius,
        rounding = CornerRounding(radius = cornerRounding),
      )
    // TODO this should be rotated by -90 degrees, but transformed is impossible to use
  }

  fun getShape(): Shape

  val displayName: StringResource
}
