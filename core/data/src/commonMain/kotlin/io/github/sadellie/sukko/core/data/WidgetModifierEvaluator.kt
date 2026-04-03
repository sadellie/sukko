package io.github.sadellie.sukko.core.data

import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.LayerContext
import io.github.sadellie.sukko.core.model.modifier.ColdAlphaModifier
import io.github.sadellie.sukko.core.model.modifier.ColdAlphaModifier.Companion.alphaRange
import io.github.sadellie.sukko.core.model.modifier.ColdAspectRatioModifier
import io.github.sadellie.sukko.core.model.modifier.ColdAspectRatioModifier.Companion.ratioRange
import io.github.sadellie.sukko.core.model.modifier.ColdBackgroundColorModifier
import io.github.sadellie.sukko.core.model.modifier.ColdBorderModifier
import io.github.sadellie.sukko.core.model.modifier.ColdBoxAlignmentModifier
import io.github.sadellie.sukko.core.model.modifier.ColdClipModifier
import io.github.sadellie.sukko.core.model.modifier.ColdColumnAlignmentModifier
import io.github.sadellie.sukko.core.model.modifier.ColdColumnWeightModifier
import io.github.sadellie.sukko.core.model.modifier.ColdColumnWeightModifier.Companion.weightRange
import io.github.sadellie.sukko.core.model.modifier.ColdFillMaxHeightModifier
import io.github.sadellie.sukko.core.model.modifier.ColdFillMaxHeightModifier.Companion.fractionRange
import io.github.sadellie.sukko.core.model.modifier.ColdFillMaxSizeModifier
import io.github.sadellie.sukko.core.model.modifier.ColdFillMaxWidthModifier
import io.github.sadellie.sukko.core.model.modifier.ColdHeightModifier
import io.github.sadellie.sukko.core.model.modifier.ColdHeightModifier.Companion.heightRange
import io.github.sadellie.sukko.core.model.modifier.ColdOffsetModifier
import io.github.sadellie.sukko.core.model.modifier.ColdPaddingAllSidesModifier
import io.github.sadellie.sukko.core.model.modifier.ColdPaddingAllSidesModifier.Companion.valueRange
import io.github.sadellie.sukko.core.model.modifier.ColdPaddingAxisModifier
import io.github.sadellie.sukko.core.model.modifier.ColdPaddingEachSideModifier
import io.github.sadellie.sukko.core.model.modifier.ColdRowAlignmentModifier
import io.github.sadellie.sukko.core.model.modifier.ColdRowWeightModifier
import io.github.sadellie.sukko.core.model.modifier.ColdSizeModifier
import io.github.sadellie.sukko.core.model.modifier.ColdWidthModifier
import io.github.sadellie.sukko.core.model.modifier.ColdWidthModifier.Companion.widthRange
import io.github.sadellie.sukko.core.model.modifier.EvaluatedAlphaModifier
import io.github.sadellie.sukko.core.model.modifier.EvaluatedAspectRatioModifier
import io.github.sadellie.sukko.core.model.modifier.EvaluatedBackgroundColorModifier
import io.github.sadellie.sukko.core.model.modifier.EvaluatedBorderModifier
import io.github.sadellie.sukko.core.model.modifier.EvaluatedBoxAlignmentModifier
import io.github.sadellie.sukko.core.model.modifier.EvaluatedClipModifier
import io.github.sadellie.sukko.core.model.modifier.EvaluatedColumnAlignmentModifier
import io.github.sadellie.sukko.core.model.modifier.EvaluatedColumnWeightModifier
import io.github.sadellie.sukko.core.model.modifier.EvaluatedFillMaxHeightModifier
import io.github.sadellie.sukko.core.model.modifier.EvaluatedFillMaxSizeModifier
import io.github.sadellie.sukko.core.model.modifier.EvaluatedFillMaxWidthModifier
import io.github.sadellie.sukko.core.model.modifier.EvaluatedHeightModifier
import io.github.sadellie.sukko.core.model.modifier.EvaluatedOffsetModifier
import io.github.sadellie.sukko.core.model.modifier.EvaluatedPaddingModifier
import io.github.sadellie.sukko.core.model.modifier.EvaluatedRowAlignmentModifier
import io.github.sadellie.sukko.core.model.modifier.EvaluatedRowWeightModifier
import io.github.sadellie.sukko.core.model.modifier.EvaluatedSizeModifier
import io.github.sadellie.sukko.core.model.modifier.EvaluatedWidthModifier
import io.github.sadellie.sukko.core.model.modifier.WidgetModifier

internal class WidgetModifierEvaluator(private val modifiers: List<WidgetModifier.Cold>) {
  suspend fun evaluate(
    layerContext: LayerContext,
    globals: Globals,
  ): List<WidgetModifier.Evaluated> =
    modifiers.map { evaluateWidgetModifier(it, layerContext, globals) }

  private suspend fun evaluateWidgetModifier(
    modifier: WidgetModifier.Cold,
    layerContext: LayerContext,
    globals: Globals,
  ): WidgetModifier.Evaluated =
    when (modifier) {
      is ColdAlphaModifier ->
        EvaluatedAlphaModifier(
          id = modifier.id,
          alpha = modifier.alpha.getValue(layerContext, globals).coerceIn(alphaRange).toFloat(),
        )
      is ColdAspectRatioModifier ->
        EvaluatedAspectRatioModifier(
          id = modifier.id,
          ratio = modifier.ratio.getValue(layerContext, globals).coerceIn(ratioRange).toFloat(),
          matchHeightConstraintsFirst =
            modifier.matchHeightConstraintsFirst.getValue(layerContext, globals),
        )
      is ColdBackgroundColorModifier ->
        EvaluatedBackgroundColorModifier(
          id = modifier.id,
          color = BrushSourceEvaluator(modifier.color, layerContext, globals).evaluate(),
          shape = modifier.shapeSource.getShape(),
        )
      is ColdBorderModifier ->
        EvaluatedBorderModifier(
          id = modifier.id,
          width = modifier.width.getValue(layerContext, globals),
          color = BrushSourceEvaluator(modifier.color, layerContext, globals).evaluate(),
          shape = modifier.shapeSource.getShape(),
        )
      is ColdBoxAlignmentModifier ->
        EvaluatedBoxAlignmentModifier(
          id = modifier.id,
          alignment = modifier.alignmentSource.getAlignment(),
        )
      is ColdClipModifier ->
        EvaluatedClipModifier(id = modifier.id, shape = modifier.shapeSource.getShape())
      is ColdColumnAlignmentModifier ->
        EvaluatedColumnAlignmentModifier(
          id = modifier.id,
          alignment = modifier.alignmentSource.getAlignment(),
        )
      is ColdColumnWeightModifier ->
        EvaluatedColumnWeightModifier(
          id = modifier.id,
          weight = modifier.weight.getValue(layerContext, globals).coerceIn(weightRange).toFloat(),
          fill = modifier.fill.getValue(layerContext, globals),
        )
      is ColdFillMaxHeightModifier ->
        EvaluatedFillMaxHeightModifier(
          id = modifier.id,
          fraction =
            modifier.fraction.getValue(layerContext, globals).coerceIn(fractionRange).toFloat(),
        )
      is ColdFillMaxSizeModifier ->
        EvaluatedFillMaxSizeModifier(
          id = modifier.id,
          fraction =
            modifier.fraction
              .getValue(layerContext, globals)
              .coerceIn(ColdFillMaxSizeModifier.fractionRange)
              .toFloat(),
        )
      is ColdFillMaxWidthModifier ->
        EvaluatedFillMaxWidthModifier(
          id = modifier.id,
          fraction =
            modifier.fraction
              .getValue(layerContext, globals)
              .coerceIn(ColdFillMaxWidthModifier.fractionRange)
              .toFloat(),
        )
      is ColdHeightModifier ->
        EvaluatedHeightModifier(
          id = modifier.id,
          height = modifier.height.getValue(layerContext, globals).coerceIn(heightRange),
        )
      is ColdOffsetModifier ->
        EvaluatedOffsetModifier(
          id = modifier.id,
          x = modifier.x.getValue(layerContext, globals),
          y = modifier.y.getValue(layerContext, globals),
        )
      is ColdPaddingAllSidesModifier -> {
        val allEvaluated = modifier.all.getValue(layerContext, globals).coerceIn(valueRange)
        return EvaluatedPaddingModifier(
          id = modifier.id,
          start = allEvaluated,
          end = allEvaluated,
          top = allEvaluated,
          bottom = allEvaluated,
        )
      }
      is ColdPaddingAxisModifier -> {
        val horizontalEvaluated =
          modifier.horizontal
            .getValue(layerContext, globals)
            .coerceIn(ColdPaddingAxisModifier.valueRange)
        val verticalEvaluated =
          modifier.vertical
            .getValue(layerContext, globals)
            .coerceIn(ColdPaddingAxisModifier.valueRange)
        return EvaluatedPaddingModifier(
          id = modifier.id,
          start = horizontalEvaluated,
          end = horizontalEvaluated,
          top = verticalEvaluated,
          bottom = verticalEvaluated,
        )
      }
      is ColdPaddingEachSideModifier ->
        EvaluatedPaddingModifier(
          id = modifier.id,
          start =
            modifier.start
              .getValue(layerContext, globals)
              .coerceIn(ColdPaddingEachSideModifier.valueRange),
          end =
            modifier.end
              .getValue(layerContext, globals)
              .coerceIn(ColdPaddingEachSideModifier.valueRange),
          top =
            modifier.top
              .getValue(layerContext, globals)
              .coerceIn(ColdPaddingEachSideModifier.valueRange),
          bottom =
            modifier.bottom
              .getValue(layerContext, globals)
              .coerceIn(ColdPaddingEachSideModifier.valueRange),
        )
      is ColdRowAlignmentModifier ->
        EvaluatedRowAlignmentModifier(
          id = modifier.id,
          alignment = modifier.alignmentSource.getAlignment(),
        )
      is ColdRowWeightModifier ->
        EvaluatedRowWeightModifier(
          id = modifier.id,
          weight =
            modifier.weight
              .getValue(layerContext, globals)
              .coerceIn(ColdRowWeightModifier.weightRange)
              .toFloat(),
          fill = modifier.fill.getValue(layerContext, globals),
        )
      is ColdSizeModifier ->
        EvaluatedSizeModifier(
          id = modifier.id,
          size = modifier.size.getValue(layerContext, globals),
        )
      is ColdWidthModifier ->
        EvaluatedWidthModifier(
          id = modifier.id,
          width = modifier.width.getValue(layerContext, globals).coerceIn(widthRange),
        )
    }
}
