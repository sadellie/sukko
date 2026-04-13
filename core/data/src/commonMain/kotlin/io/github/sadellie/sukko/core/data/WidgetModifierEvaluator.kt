package io.github.sadellie.sukko.core.data

import androidx.compose.ui.unit.dp
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

internal class WidgetModifierEvaluator(
  private val scriptableEvaluator: ScriptableEvaluator,
  private val brushSourceEvaluator: BrushSourceEvaluator,
) {
  suspend fun evaluate(modifiers: List<WidgetModifier.Cold>): List<WidgetModifier.Evaluated> =
    modifiers.map { evaluateWidgetModifier(it) }

  private suspend fun evaluateWidgetModifier(
    modifier: WidgetModifier.Cold
  ): WidgetModifier.Evaluated =
    when (modifier) {
      is ColdAlphaModifier -> evaluateAlphaModifier(modifier)
      is ColdAspectRatioModifier -> evaluateAspectRatioModifier(modifier)
      is ColdBackgroundColorModifier -> evaluateBackgroundColorModifier(modifier)
      is ColdBorderModifier -> evaluateBorderModifier(modifier)
      is ColdBoxAlignmentModifier -> evaluateBoxAlignmentModifier(modifier)
      is ColdClipModifier -> evaluateClipModifier(modifier)
      is ColdColumnAlignmentModifier -> evaluateColumnAlignmentModifier(modifier)
      is ColdColumnWeightModifier -> evaluateColumnWeightModifier(modifier)
      is ColdFillMaxHeightModifier -> evaluateFillMaxHeightModifier(modifier)
      is ColdFillMaxSizeModifier -> evaluateFillMaxSizeModifier(modifier)
      is ColdFillMaxWidthModifier -> evaluateFillMaxWidthModifier(modifier)
      is ColdHeightModifier -> evaluateHeightModifier(modifier)
      is ColdOffsetModifier -> evaluateOffsetModifier(modifier)
      is ColdPaddingAllSidesModifier -> evaluatePaddingAllSidesModifier(modifier)
      is ColdPaddingAxisModifier -> evaluatePaddingAxisModifier(modifier)
      is ColdPaddingEachSideModifier -> evaluatePaddingEachSideModifier(modifier)
      is ColdRowAlignmentModifier -> evaluateRowAlignmentModifier(modifier)
      is ColdRowWeightModifier -> evaluateRowWeightModifier(modifier)
      is ColdSizeModifier -> evaluateSizeModifier(modifier)
      is ColdWidthModifier -> evaluateWidthModifier(modifier)
    }

  private suspend fun evaluateWidthModifier(modifier: ColdWidthModifier): EvaluatedWidthModifier =
    EvaluatedWidthModifier(
      id = modifier.id,
      width = scriptableEvaluator.evaluateDouble(modifier.width).coerceIn(widthRange).dp,
    )

  private suspend fun evaluateSizeModifier(modifier: ColdSizeModifier): EvaluatedSizeModifier =
    EvaluatedSizeModifier(
      id = modifier.id,
      size = scriptableEvaluator.evaluateDouble(modifier.size).dp,
    )

  private suspend fun evaluateRowWeightModifier(
    modifier: ColdRowWeightModifier
  ): EvaluatedRowWeightModifier =
    EvaluatedRowWeightModifier(
      id = modifier.id,
      weight =
        scriptableEvaluator
          .evaluateDouble(modifier.weight)
          .coerceIn(ColdRowWeightModifier.weightRange)
          .toFloat(),
      fill = scriptableEvaluator.evaluateBoolean(modifier.fill),
    )

  private fun evaluateRowAlignmentModifier(
    modifier: ColdRowAlignmentModifier
  ): EvaluatedRowAlignmentModifier =
    EvaluatedRowAlignmentModifier(
      id = modifier.id,
      alignment = modifier.alignmentSource.getAlignment(),
    )

  private suspend fun evaluatePaddingEachSideModifier(
    modifier: ColdPaddingEachSideModifier
  ): EvaluatedPaddingModifier =
    EvaluatedPaddingModifier(
      id = modifier.id,
      start =
        scriptableEvaluator
          .evaluateDouble(modifier.start)
          .coerceIn(ColdPaddingEachSideModifier.valueRange)
          .dp,
      end =
        scriptableEvaluator
          .evaluateDouble(modifier.end)
          .coerceIn(ColdPaddingEachSideModifier.valueRange)
          .dp,
      top =
        scriptableEvaluator
          .evaluateDouble(modifier.top)
          .coerceIn(ColdPaddingEachSideModifier.valueRange)
          .dp,
      bottom =
        scriptableEvaluator
          .evaluateDouble(modifier.bottom)
          .coerceIn(ColdPaddingEachSideModifier.valueRange)
          .dp,
    )

  private suspend fun evaluatePaddingAxisModifier(
    modifier: ColdPaddingAxisModifier
  ): EvaluatedPaddingModifier {
    val horizontalEvaluated =
      scriptableEvaluator
        .evaluateDouble(modifier.horizontal)
        .coerceIn(ColdPaddingAxisModifier.valueRange)
        .dp
    val verticalEvaluated =
      scriptableEvaluator
        .evaluateDouble(modifier.vertical)
        .coerceIn(ColdPaddingAxisModifier.valueRange)
        .dp
    return EvaluatedPaddingModifier(
      id = modifier.id,
      start = horizontalEvaluated,
      end = horizontalEvaluated,
      top = verticalEvaluated,
      bottom = verticalEvaluated,
    )
  }

  private suspend fun evaluatePaddingAllSidesModifier(
    modifier: ColdPaddingAllSidesModifier
  ): EvaluatedPaddingModifier {
    val allEvaluated = scriptableEvaluator.evaluateDouble(modifier.all).coerceIn(valueRange).dp
    return EvaluatedPaddingModifier(
      id = modifier.id,
      start = allEvaluated,
      end = allEvaluated,
      top = allEvaluated,
      bottom = allEvaluated,
    )
  }

  private suspend fun evaluateOffsetModifier(
    modifier: ColdOffsetModifier
  ): EvaluatedOffsetModifier =
    EvaluatedOffsetModifier(
      id = modifier.id,
      x = scriptableEvaluator.evaluateDouble(modifier.x).dp,
      y = scriptableEvaluator.evaluateDouble(modifier.y).dp,
    )

  private suspend fun evaluateHeightModifier(
    modifier: ColdHeightModifier
  ): EvaluatedHeightModifier =
    EvaluatedHeightModifier(
      id = modifier.id,
      height = scriptableEvaluator.evaluateDouble(modifier.height).coerceIn(heightRange).dp,
    )

  private suspend fun evaluateFillMaxWidthModifier(
    modifier: ColdFillMaxWidthModifier
  ): EvaluatedFillMaxWidthModifier =
    EvaluatedFillMaxWidthModifier(
      id = modifier.id,
      fraction =
        scriptableEvaluator
          .evaluateDouble(modifier.fraction)
          .coerceIn(ColdFillMaxWidthModifier.fractionRange)
          .toFloat(),
    )

  private suspend fun evaluateFillMaxSizeModifier(
    modifier: ColdFillMaxSizeModifier
  ): EvaluatedFillMaxSizeModifier =
    EvaluatedFillMaxSizeModifier(
      id = modifier.id,
      fraction =
        scriptableEvaluator
          .evaluateDouble(modifier.fraction)
          .coerceIn(ColdFillMaxSizeModifier.fractionRange)
          .toFloat(),
    )

  private suspend fun evaluateFillMaxHeightModifier(
    modifier: ColdFillMaxHeightModifier
  ): EvaluatedFillMaxHeightModifier =
    EvaluatedFillMaxHeightModifier(
      id = modifier.id,
      fraction =
        scriptableEvaluator.evaluateDouble(modifier.fraction).coerceIn(fractionRange).toFloat(),
    )

  private suspend fun evaluateColumnWeightModifier(
    modifier: ColdColumnWeightModifier
  ): EvaluatedColumnWeightModifier =
    EvaluatedColumnWeightModifier(
      id = modifier.id,
      weight = scriptableEvaluator.evaluateDouble(modifier.weight).coerceIn(weightRange).toFloat(),
      fill = scriptableEvaluator.evaluateBoolean(modifier.fill),
    )

  private fun evaluateColumnAlignmentModifier(
    modifier: ColdColumnAlignmentModifier
  ): EvaluatedColumnAlignmentModifier =
    EvaluatedColumnAlignmentModifier(
      id = modifier.id,
      alignment = modifier.alignmentSource.getAlignment(),
    )

  private fun evaluateClipModifier(modifier: ColdClipModifier): EvaluatedClipModifier =
    EvaluatedClipModifier(id = modifier.id, shape = modifier.shapeSource.getShape())

  private fun evaluateBoxAlignmentModifier(
    modifier: ColdBoxAlignmentModifier
  ): EvaluatedBoxAlignmentModifier =
    EvaluatedBoxAlignmentModifier(
      id = modifier.id,
      alignment = modifier.alignmentSource.getAlignment(),
    )

  private suspend fun evaluateBorderModifier(
    modifier: ColdBorderModifier
  ): EvaluatedBorderModifier =
    EvaluatedBorderModifier(
      id = modifier.id,
      width = scriptableEvaluator.evaluateDouble(modifier.width).dp,
      color = brushSourceEvaluator.evaluate(modifier.color),
      shape = modifier.shapeSource.getShape(),
    )

  private suspend fun evaluateBackgroundColorModifier(
    modifier: ColdBackgroundColorModifier
  ): EvaluatedBackgroundColorModifier =
    EvaluatedBackgroundColorModifier(
      id = modifier.id,
      color = brushSourceEvaluator.evaluate(modifier.color),
      shape = modifier.shapeSource.getShape(),
    )

  private suspend fun evaluateAspectRatioModifier(
    modifier: ColdAspectRatioModifier
  ): EvaluatedAspectRatioModifier =
    EvaluatedAspectRatioModifier(
      id = modifier.id,
      ratio = scriptableEvaluator.evaluateDouble(modifier.ratio).coerceIn(ratioRange).toFloat(),
      matchHeightConstraintsFirst =
        scriptableEvaluator.evaluateBoolean(modifier.matchHeightConstraintsFirst),
    )

  private suspend fun evaluateAlphaModifier(modifier: ColdAlphaModifier): EvaluatedAlphaModifier =
    EvaluatedAlphaModifier(
      id = modifier.id,
      alpha = scriptableEvaluator.evaluateDouble(modifier.alpha).coerceIn(alphaRange).toFloat(),
    )
}
