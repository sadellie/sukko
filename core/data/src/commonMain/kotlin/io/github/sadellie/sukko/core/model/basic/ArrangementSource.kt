package io.github.sadellie.sukko.core.model.basic

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.unit.dp
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.core_model_arrangement_bottom
import io.github.sadellie.sukko.resources.core_model_arrangement_center
import io.github.sadellie.sukko.resources.core_model_arrangement_end
import io.github.sadellie.sukko.resources.core_model_arrangement_space_around
import io.github.sadellie.sukko.resources.core_model_arrangement_space_between
import io.github.sadellie.sukko.resources.core_model_arrangement_space_evenly
import io.github.sadellie.sukko.resources.core_model_arrangement_spaced_by
import io.github.sadellie.sukko.resources.core_model_arrangement_spaced_by_horizontal
import io.github.sadellie.sukko.resources.core_model_arrangement_spaced_by_vertical
import io.github.sadellie.sukko.resources.core_model_arrangement_start
import io.github.sadellie.sukko.resources.core_model_arrangement_top
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.jetbrains.compose.resources.StringResource

@Serializable
sealed interface ArrangementSource {
  @Serializable
  sealed interface Vertical : ArrangementSource {
    fun getArrangement(): Arrangement.Vertical
  }

  @Serializable
  sealed interface Horizontal : ArrangementSource {
    fun getArrangement(): Arrangement.Horizontal
  }

  @Serializable
  data object Top : Vertical {
    @Transient override val displayName = Res.string.core_model_arrangement_top

    override fun getArrangement() = Arrangement.Top
  }

  @Serializable
  data object Bottom : Vertical {
    @Transient override val displayName = Res.string.core_model_arrangement_bottom

    override fun getArrangement() = Arrangement.Bottom
  }

  @Serializable
  data object Start : Horizontal {
    @Transient override val displayName = Res.string.core_model_arrangement_start

    override fun getArrangement() = Arrangement.Start
  }

  @Serializable
  data object End : Horizontal {
    @Transient override val displayName = Res.string.core_model_arrangement_end

    override fun getArrangement() = Arrangement.End
  }

  @Serializable
  data object Center : Vertical, Horizontal {
    @Transient override val displayName = Res.string.core_model_arrangement_center

    override fun getArrangement() = Arrangement.Center
  }

  @Serializable
  data object SpaceAround : Vertical, Horizontal {
    @Transient override val displayName = Res.string.core_model_arrangement_space_around

    override fun getArrangement() = Arrangement.SpaceAround
  }

  @Serializable
  data object SpaceEvenly : Vertical, Horizontal {
    @Transient override val displayName = Res.string.core_model_arrangement_space_evenly

    override fun getArrangement() = Arrangement.SpaceEvenly
  }

  @Serializable
  data object SpaceBetween : Vertical, Horizontal {
    @Transient override val displayName = Res.string.core_model_arrangement_space_between

    override fun getArrangement() = Arrangement.SpaceBetween
  }

  @Serializable
  data class SpacedBy(val space: ScriptableDouble.Fixed) : Vertical, Horizontal {
    @Transient override val displayName = Res.string.core_model_arrangement_spaced_by

    override fun getArrangement() = Arrangement.spacedBy(space.value.dp)
  }

  @Serializable
  data class SpacedByVertical(
    val space: ScriptableDouble.Fixed,
    val alignmentSource: AlignmentSource.Vertical,
  ) : Vertical {
    @Transient override val displayName = Res.string.core_model_arrangement_spaced_by_vertical

    override fun getArrangement() =
      Arrangement.spacedBy(space.value.dp, alignment = alignmentSource.getAlignment())
  }

  @Serializable
  data class SpacedByHorizontal(
    val space: ScriptableDouble.Fixed,
    val alignmentSource: AlignmentSource.Horizontal,
  ) : Horizontal {
    @Transient override val displayName = Res.string.core_model_arrangement_spaced_by_horizontal

    override fun getArrangement() =
      Arrangement.spacedBy(space.value.dp, alignment = alignmentSource.getAlignment())
  }

  val displayName: StringResource

  companion object {
    val allVertical by lazy {
      listOf<Vertical>(
        Top,
        Bottom,
        Center,
        SpaceAround,
        SpaceEvenly,
        SpaceBetween,
        SpacedBy(ScriptableDouble.Fixed(0.0)),
        SpacedByVertical(ScriptableDouble.Fixed(0.0), AlignmentSource.Top),
      )
    }

    val allHorizontal by lazy {
      listOf<Horizontal>(
        Start,
        End,
        Center,
        SpaceAround,
        SpaceEvenly,
        SpaceBetween,
        SpacedBy(ScriptableDouble.Fixed(0.0)),
        SpacedByHorizontal(ScriptableDouble.Fixed(0.0), AlignmentSource.Start),
      )
    }
  }
}
