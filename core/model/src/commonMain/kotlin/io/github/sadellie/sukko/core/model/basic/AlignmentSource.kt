package io.github.sadellie.sukko.core.model.basic

import androidx.compose.ui.Alignment
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.core_model_alignment_bottom
import io.github.sadellie.sukko.resources.core_model_alignment_bottom_center
import io.github.sadellie.sukko.resources.core_model_alignment_bottom_end
import io.github.sadellie.sukko.resources.core_model_alignment_bottom_start
import io.github.sadellie.sukko.resources.core_model_alignment_center
import io.github.sadellie.sukko.resources.core_model_alignment_center_end
import io.github.sadellie.sukko.resources.core_model_alignment_center_start
import io.github.sadellie.sukko.resources.core_model_alignment_end
import io.github.sadellie.sukko.resources.core_model_alignment_start
import io.github.sadellie.sukko.resources.core_model_alignment_top
import io.github.sadellie.sukko.resources.core_model_alignment_top_center
import io.github.sadellie.sukko.resources.core_model_alignment_top_end
import io.github.sadellie.sukko.resources.core_model_alignment_top_start
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.jetbrains.compose.resources.StringResource

@Serializable
sealed interface AlignmentSource {
  @Serializable
  sealed interface Vertical : AlignmentSource {
    fun getAlignment(): Alignment.Vertical
  }

  @Serializable
  sealed interface Horizontal : AlignmentSource {
    fun getAlignment(): Alignment.Horizontal
  }

  @Serializable
  sealed interface Both : AlignmentSource {
    fun getAlignment(): Alignment
  }

  @Serializable
  data object Top : Vertical {
    @Transient override val displayName = Res.string.core_model_alignment_top

    override fun getAlignment() = Alignment.Top
  }

  @Serializable
  data object Bottom : Vertical {
    @Transient override val displayName = Res.string.core_model_alignment_bottom

    override fun getAlignment() = Alignment.Bottom
  }

  @Serializable
  data object CenterVertically : Vertical {
    @Transient override val displayName = Res.string.core_model_alignment_center

    override fun getAlignment() = Alignment.CenterVertically
  }

  @Serializable
  data object Start : Horizontal {
    @Transient override val displayName = Res.string.core_model_alignment_start

    override fun getAlignment() = Alignment.Start
  }

  @Serializable
  data object End : Horizontal {
    @Transient override val displayName = Res.string.core_model_alignment_end

    override fun getAlignment() = Alignment.End
  }

  @Serializable
  data object CenterHorizontally : Horizontal {
    @Transient override val displayName = Res.string.core_model_alignment_center

    override fun getAlignment() = Alignment.CenterHorizontally
  }

  @Serializable
  data object TopStart : Both {
    @Transient override val displayName = Res.string.core_model_alignment_top_start

    override fun getAlignment() = Alignment.TopStart
  }

  @Serializable
  data object TopCenter : Both {
    @Transient override val displayName = Res.string.core_model_alignment_top_center

    override fun getAlignment() = Alignment.TopCenter
  }

  @Serializable
  data object TopEnd : Both {
    @Transient override val displayName = Res.string.core_model_alignment_top_end

    override fun getAlignment() = Alignment.TopEnd
  }

  @Serializable
  data object BottomStart : Both {
    @Transient override val displayName = Res.string.core_model_alignment_bottom_start

    override fun getAlignment() = Alignment.BottomStart
  }

  @Serializable
  data object BottomCenter : Both {
    @Transient override val displayName = Res.string.core_model_alignment_bottom_center

    override fun getAlignment() = Alignment.BottomCenter
  }

  @Serializable
  data object BottomEnd : Both {
    @Transient override val displayName = Res.string.core_model_alignment_bottom_end

    override fun getAlignment() = Alignment.BottomEnd
  }

  @Serializable
  data object Center : Both {
    @Transient override val displayName = Res.string.core_model_alignment_center

    override fun getAlignment() = Alignment.Center
  }

  @Serializable
  data object CenterStart : Both {
    @Transient override val displayName = Res.string.core_model_alignment_center_start

    override fun getAlignment() = Alignment.CenterStart
  }

  @Serializable
  data object CenterEnd : Both {
    @Transient override val displayName = Res.string.core_model_alignment_center_end

    override fun getAlignment() = Alignment.CenterEnd
  }

  val displayName: StringResource

  companion object {
    val allVertical by lazy { listOf<Vertical>(Top, Bottom, CenterVertically) }
    val allHorizontal by lazy { listOf<Horizontal>(Start, End, CenterHorizontally) }
    val allBoth by lazy {
      listOf<Both>(
        TopStart,
        TopCenter,
        TopEnd,
        CenterStart,
        Center,
        CenterEnd,
        BottomStart,
        BottomCenter,
        BottomEnd,
      )
    }
  }
}
