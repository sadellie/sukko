package io.github.sadellie.sukko.core.model.modifier

import androidx.compose.ui.Modifier
import io.github.sadellie.sukko.core.model.layer.ColdBoxLayer
import io.github.sadellie.sukko.core.model.layer.ColdColumnLayer
import io.github.sadellie.sukko.core.model.layer.ColdRowLayer
import io.github.sadellie.sukko.core.model.layer.Layer
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource

sealed interface WidgetModifier {
  val id: Int

  @Serializable
  sealed interface Cold : WidgetModifier {
    companion object {
      fun getAllWidgetModifiers(parentLayer: Layer.Cold?): List<Cold> {
        val basicWidgetModifiers =
          listOf(
            ColdSizeModifier(id = 0),
            ColdFillMaxSizeModifier(id = 0),
            ColdWidthModifier(id = 0),
            ColdFillMaxWidthModifier(id = 0),
            ColdHeightModifier(id = 0),
            ColdFillMaxHeightModifier(id = 0),
            ColdBackgroundColorModifier(id = 0),
            ColdPaddingAllSidesModifier(id = 0),
            ColdPaddingAxisModifier(id = 0),
            ColdPaddingEachSideModifier(id = 0),
            ColdClipModifier(id = 0),
            ColdOffsetModifier(id = 0),
            ColdBorderModifier(id = 0),
            ColdAlphaModifier(id = 0),
            ColdAspectRatioModifier(id = 0),
          )
        return when (parentLayer) {
          is ColdBoxLayer -> listOf(ColdBoxAlignmentModifier(id = 0)) + basicWidgetModifiers
          is ColdColumnLayer ->
            listOf(ColdColumnAlignmentModifier(id = 0), ColdColumnWeightModifier(id = 0)) +
              basicWidgetModifiers
          is ColdRowLayer ->
            listOf(ColdRowAlignmentModifier(id = 0), ColdRowWeightModifier(id = 0)) +
              basicWidgetModifiers
          else -> basicWidgetModifiers
        }
      }
    }

    val displayName: StringResource

    fun updateId(newId: Int): Cold
  }

  interface Evaluated : WidgetModifier {
    fun addToModifier(modifier: Modifier, scope: Any): Modifier
  }
}
