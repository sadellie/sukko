package io.github.sadellie.sukko.core.data

import androidx.compose.ui.graphics.isSpecified
import co.touchlab.kermit.Logger
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.LayerContext
import io.github.sadellie.sukko.core.model.layer.ColdBoxLayer
import io.github.sadellie.sukko.core.model.layer.ColdColumnLayer
import io.github.sadellie.sukko.core.model.layer.ColdImageLayer
import io.github.sadellie.sukko.core.model.layer.ColdProgressBarLayer
import io.github.sadellie.sukko.core.model.layer.ColdProgressBarLayer.Companion.amplitudeRange
import io.github.sadellie.sukko.core.model.layer.ColdProgressBarLayer.Companion.progressRange
import io.github.sadellie.sukko.core.model.layer.ColdRowLayer
import io.github.sadellie.sukko.core.model.layer.ColdStepIndicatorLayer
import io.github.sadellie.sukko.core.model.layer.ColdTextLayer
import io.github.sadellie.sukko.core.model.layer.EvaluatedBoxLayer
import io.github.sadellie.sukko.core.model.layer.EvaluatedColumnLayer
import io.github.sadellie.sukko.core.model.layer.EvaluatedImageLayer
import io.github.sadellie.sukko.core.model.layer.EvaluatedProgressBarLayer
import io.github.sadellie.sukko.core.model.layer.EvaluatedRowLayer
import io.github.sadellie.sukko.core.model.layer.EvaluatedStepIndicatorLayer
import io.github.sadellie.sukko.core.model.layer.EvaluatedTextLayer
import io.github.sadellie.sukko.core.model.layer.Layer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

class LayerEvaluator(
  private val layers: List<Layer.Cold>,
  private val imageProvider: ImageProvider,
  private val layerContext: LayerContext,
  private val globals: Globals,
) {
  /**
   * Evaluates enabled layers and their children. Children will not be evaluated unless parent layer
   * is enabled and ready to be drawn.
   *
   * @return Flow enabled and ready to be drawn layers (not null)
   */
  suspend fun evaluateEnabled(): Flow<List<Layer.Evaluated>> {
    if (layers.isEmpty()) return flowOf(emptyList())
    val flows = mutableListOf<Flow<Layer.Evaluated?>>()
    // state each flow in this scope so children consume same flow
    val scope = CoroutineScope(currentCoroutineContext())

    suspend fun traverse(parentLayer: Layer.Cold?, parentFlow: Flow<Layer.Evaluated?>?) {
      val layersInParent = layers.filter { it.parentId == parentLayer?.id }
      layersInParent.forEach { layer ->
        // stateIn parent flow so children receive same evaluation (is parent enabled) from higher
        // level
        val pFlow = evaluateAsFlowWithParent(layer, parentFlow).stateIn(scope)
        flows.add(pFlow)
        traverse(layer, pFlow)
      }
    }
    // visit root and go deeper
    traverse(null, null)

    return combine(flows) { it.toList().filterNotNull() }
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  private fun evaluateAsFlowWithParent(
    layer: Layer.Cold,
    parentFlow: Flow<Layer.Evaluated?>?,
  ): Flow<Layer.Evaluated?> {
    val evaluationFlow =
      parentFlow?.flatMapLatest { parent ->
        // layers parent is not drawn (not ready or disabled)
        if (parent == null) flowOf(null)
        // parent is drawn, allow children to emit their evaluations
        else evaluateAsFlow(layer)
      }
        ?: // no parent (top level layers do not depend on other layers)
        evaluateAsFlow(layer)
    return evaluationFlow.catch {
      Logger.e(throwable = it, tag = TAG) { "evaluateAsFlowWithParent: Failed to evaluate" }
      emit(null)
    }
  }

  /**
   * Parent will wait for first emission. Emit null if not ready yet or layer is disabled
   *
   * @see evaluateAsFlowWithParent
   */
  private fun evaluateAsFlow(layer: Layer.Cold): Flow<Layer.Evaluated?> =
    when (layer) {
      is ColdBoxLayer -> evaluateBoxLayer(layer)
      is ColdColumnLayer -> evaluateColumnLayer(layer)
      is ColdImageLayer -> evaluateImageLayer(layer)
      is ColdProgressBarLayer -> evaluateProgressBarLayer(layer)
      is ColdRowLayer -> evaluateRowLayer(layer)
      is ColdTextLayer -> evaluateTextLayer(layer)
      is ColdStepIndicatorLayer -> evaluateStepIndicatorLayer(layer)
    }

  private fun evaluateBoxLayer(layer: ColdBoxLayer) =
    evaluateLayerIfEnabled(layer) {
      val evaluated =
        EvaluatedBoxLayer(
          id = layer.id,
          parentId = layer.parentId,
          name = layer.name,
          widgetModifiers =
            WidgetModifierEvaluator(layer.widgetModifiers).evaluate(layerContext, globals),
          clickActions = ClickActionEvaluator(layer.clickActions, layerContext, globals).evaluate(),
          alignment = layer.alignmentSource.getAlignment(),
        )
      emit(evaluated)
    }

  private fun evaluateColumnLayer(layer: ColdColumnLayer) =
    evaluateLayerIfEnabled(layer) {
      val evaluated =
        EvaluatedColumnLayer(
          id = layer.id,
          parentId = layer.parentId,
          name = layer.name,
          widgetModifiers =
            WidgetModifierEvaluator(layer.widgetModifiers).evaluate(layerContext, globals),
          clickActions = ClickActionEvaluator(layer.clickActions, layerContext, globals).evaluate(),
          arrangement = layer.arrangementSource.getArrangement(),
          alignment = layer.alignmentSource.getAlignment(),
        )
      emit(evaluated)
    }

  private fun evaluateImageLayer(layer: ColdImageLayer) =
    evaluateLayerIfEnabled(layer) {
      var evaluated =
        EvaluatedImageLayer(
          id = layer.id,
          parentId = layer.parentId,
          name = layer.name,
          widgetModifiers =
            WidgetModifierEvaluator(layer.widgetModifiers).evaluate(layerContext, globals),
          clickActions = ClickActionEvaluator(layer.clickActions, layerContext, globals).evaluate(),
          // initially empty image to at least occupy the space in layout
          image = null,
          contentScale =
            ContentScaleSourceEvaluator(layer.contentScale, layerContext, globals).evaluate(),
          tint = layer.tint.getValue(layerContext, globals).takeIf { it.isSpecified },
        )
      emit(evaluated)
      // once image uri was generated (from cache or waited for download), emit final layer
      val localImage = imageProvider.getBitmap(layer.imageUriSource, layerContext, globals)
      evaluated = evaluated.copy(image = localImage)
      emit(evaluated)
    }

  private fun evaluateProgressBarLayer(layer: ColdProgressBarLayer) =
    evaluateLayerIfEnabled(layer) {
      val evaluated =
        EvaluatedProgressBarLayer(
          id = layer.id,
          parentId = layer.parentId,
          name = layer.name,
          widgetModifiers =
            WidgetModifierEvaluator(layer.widgetModifiers).evaluate(layerContext, globals),
          clickActions = ClickActionEvaluator(layer.clickActions, layerContext, globals).evaluate(),
          progress =
            layer.progress.getValue(layerContext, globals).coerceIn(progressRange).toFloat(),
          progressBarType = layer.progressBarType,
          color = layer.color.getValue(layerContext, globals),
          trackColor = layer.trackColor.getValue(layerContext, globals),
          gapSize = layer.gapSize.getValue(layerContext, globals),
          amplitude =
            layer.amplitude.getValue(layerContext, globals).coerceIn(amplitudeRange).toFloat(),
          waveLength = layer.waveLength.getValue(layerContext, globals),
        )
      emit(evaluated)
    }

  private fun evaluateRowLayer(layer: ColdRowLayer) =
    evaluateLayerIfEnabled(layer) {
      val evaluated =
        EvaluatedRowLayer(
          id = layer.id,
          parentId = layer.parentId,
          name = layer.name,
          widgetModifiers =
            WidgetModifierEvaluator(layer.widgetModifiers).evaluate(layerContext, globals),
          clickActions = ClickActionEvaluator(layer.clickActions, layerContext, globals).evaluate(),
          arrangement = layer.arrangementSource.getArrangement(),
          alignment = layer.alignmentSource.getAlignment(),
        )
      emit(evaluated)
    }

  private fun evaluateTextLayer(layer: ColdTextLayer) =
    evaluateLayerIfEnabled(layer) {
      val minLines =
        layer.minLines.getValue(layerContext, globals).coerceIn(ColdTextLayer.minLinesRange).toInt()
      val maxLines =
        layer.maxLines.getValue(layerContext, globals).coerceIn(ColdTextLayer.maxLinesRange).toInt()
      val fixedMinLines = minLines.coerceIn(1..maxLines)
      val fixedMaxLines = maxLines.coerceIn(fixedMinLines..Int.MAX_VALUE)

      val evaluated =
        EvaluatedTextLayer(
          id = layer.id,
          parentId = layer.parentId,
          name = layer.name,
          widgetModifiers =
            WidgetModifierEvaluator(layer.widgetModifiers).evaluate(layerContext, globals),
          clickActions = ClickActionEvaluator(layer.clickActions, layerContext, globals).evaluate(),
          textStyle =
            TextStyleSourceEvaluator(layer.textStyleSource, layerContext, globals).evaluate(),
          textColor = BrushSourceEvaluator(layer.textColor, layerContext, globals).evaluate(),
          text = layer.text.getValue(layerContext, globals),
          overflow = layer.textOverflowSource.getTextOverflow(),
          minLines = fixedMinLines,
          maxLines = fixedMaxLines,
        )
      emit(evaluated)
    }

  private fun evaluateStepIndicatorLayer(layer: ColdStepIndicatorLayer) =
    evaluateLayerIfEnabled(layer) {
      val evaluated =
        EvaluatedStepIndicatorLayer(
          id = layer.id,
          parentId = layer.parentId,
          name = layer.name,
          widgetModifiers =
            WidgetModifierEvaluator(layer.widgetModifiers).evaluate(layerContext, globals),
          clickActions = ClickActionEvaluator(layer.clickActions, layerContext, globals).evaluate(),
          fill = layer.fill.getValue(layerContext, globals),
          totalSteps = layer.totalSteps.getValue(layerContext, globals).toInt(),
          currentStep = layer.currentStep.getValue(layerContext, globals).toInt(),
          indicatorSize = layer.indicatorSize.getValue(layerContext, globals),
          activeColor = layer.activeColor.getValue(layerContext, globals),
          inactiveColor = layer.inactiveColor.getValue(layerContext, globals),
          shape = layer.shape.getShape(),
        )
      emit(evaluated)
    }

  /** Check [Layer.Cold.isEnabled] and allow evaluation only if it is `true` */
  private fun <T : Layer.Evaluated> evaluateLayerIfEnabled(
    coldLayer: Layer.Cold,
    block: suspend FlowCollector<T?>.() -> Unit,
  ) = flow {
    emit(null)
    if (!coldLayer.isEnabled.getValue(layerContext, globals)) return@flow
    block()
  }
}

private const val TAG = "LayerEvaluator"
