package io.github.sadellie.sukko.core.data

import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Named
import io.github.sadellie.sukko.core.fontfiles.FontFamilyLoader
import io.github.sadellie.sukko.core.model.GlobalValueCache
import io.github.sadellie.sukko.core.model.Globals
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
import okio.Path

class LayerEvaluator(
  internal val layers: List<Layer.Cold>,
  internal val imageProvider: ImageProvider,
  internal val scriptableEvaluator: ScriptableEvaluator,
  internal val filesDirPath: Path,
  internal val fontFamilyLoader: FontFamilyLoader,
  internal val textStyleSourceEvaluator: TextStyleSourceEvaluator,
) {
  @Inject
  class LayerEvaluatorFactory(
    private val imageProvider: ImageProvider,
    private val fontFamilyLoader: FontFamilyLoader,
    private val scriptableEvaluatorFactory: ScriptableEvaluator.ScriptableEvaluatorFactory,
    private val textStyleSourceEvaluatorFactory: TextStyleSourceEvaluator.Factory,
    @param:Named("filesDirPath") private val filesDirPath: Path,
  ) {
    fun create(layers: List<Layer.Cold>, globals: Globals): LayerEvaluator {
      val globalValueCache = GlobalValueCache()
      val scriptableEvaluator =
        scriptableEvaluatorFactory.create(
          globals = globals,
          widgetId = null,
          globalValueCache = globalValueCache,
        )
      return LayerEvaluator(
        layers = layers,
        imageProvider = imageProvider,
        scriptableEvaluator = scriptableEvaluator,
        filesDirPath = filesDirPath,
        fontFamilyLoader = fontFamilyLoader,
        textStyleSourceEvaluator =
          textStyleSourceEvaluatorFactory.create(
            globals = globals,
            globalValueCache = globalValueCache,
            scriptableEvaluator = scriptableEvaluator,
          ),
      )
    }
  }

  private val brushSourceEvaluator = BrushSourceEvaluator(scriptableEvaluator)
  private val clickActionEvaluator = ClickActionEvaluator(scriptableEvaluator)
  private val contentScaleSourceEvaluator = ContentScaleSourceEvaluator(scriptableEvaluator)
  private val widgetModifierEvaluator =
    WidgetModifierEvaluator(scriptableEvaluator, brushSourceEvaluator)

  /**
   * Evaluates enabled layers and their children. Children will not be evaluated unless parent layer
   * is enabled and ready to be drawn.
   *
   * @return Flow of enabled and ready to be drawn layers (never null)
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
   * Evaluates a single layer as a Flow. Parent will wait for first emission. Emits null if layer is
   * not ready or disabled.
   *
   * @param layer The layer to evaluate
   * @return Flow of evaluated layer or null if disabled/not ready
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
          widgetModifiers = widgetModifierEvaluator.evaluate(layer.widgetModifiers),
          clickActions = clickActionEvaluator.evaluate(layer.clickActions),
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
          widgetModifiers = widgetModifierEvaluator.evaluate(layer.widgetModifiers),
          clickActions = clickActionEvaluator.evaluate(layer.clickActions),
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
          widgetModifiers = widgetModifierEvaluator.evaluate(layer.widgetModifiers),
          clickActions = clickActionEvaluator.evaluate(layer.clickActions),
          // initially empty image to at least occupy the space in layout
          image = null,
          contentScale = contentScaleSourceEvaluator.evaluate(layer.contentScale),
          tint = scriptableEvaluator.evaluateColor(layer.tint).takeIf { it.isSpecified },
        )
      emit(evaluated)
      // once image uri was generated (from cache or waited for download), emit final layer
      val localImage =
        imageProvider.getBitmap(layer.imageUriSource, filesDirPath, scriptableEvaluator)
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
          widgetModifiers = widgetModifierEvaluator.evaluate(layer.widgetModifiers),
          clickActions = clickActionEvaluator.evaluate(layer.clickActions),
          progress =
            scriptableEvaluator.evaluateDouble(layer.progress).coerceIn(progressRange).toFloat(),
          progressBarType = layer.progressBarType,
          color = scriptableEvaluator.evaluateColor(layer.color),
          trackColor = scriptableEvaluator.evaluateColor(layer.trackColor),
          gapSize = scriptableEvaluator.evaluateDouble(layer.gapSize).dp,
          amplitude =
            scriptableEvaluator.evaluateDouble(layer.amplitude).coerceIn(amplitudeRange).toFloat(),
          waveLength = scriptableEvaluator.evaluateDouble(layer.waveLength).dp,
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
          widgetModifiers = widgetModifierEvaluator.evaluate(layer.widgetModifiers),
          clickActions = clickActionEvaluator.evaluate(layer.clickActions),
          arrangement = layer.arrangementSource.getArrangement(),
          alignment = layer.alignmentSource.getAlignment(),
        )
      emit(evaluated)
    }

  private fun evaluateTextLayer(layer: ColdTextLayer) =
    evaluateLayerIfEnabled(layer) {
      val minLines =
        scriptableEvaluator
          .evaluateDouble(layer.minLines)
          .coerceIn(ColdTextLayer.minLinesRange)
          .toInt()
      val maxLines =
        layer.maxLines
          ?.let { scriptableEvaluator.evaluateDouble(it).coerceIn(ColdTextLayer.maxLinesRange) }
          ?.toInt() ?: Int.MAX_VALUE
      val fixedMinLines = minLines.coerceIn(1..maxLines)
      val fixedMaxLines = maxLines.coerceIn(fixedMinLines..Int.MAX_VALUE)

      val evaluated =
        EvaluatedTextLayer(
          id = layer.id,
          parentId = layer.parentId,
          name = layer.name,
          widgetModifiers = widgetModifierEvaluator.evaluate(layer.widgetModifiers),
          clickActions = clickActionEvaluator.evaluate(layer.clickActions),
          textStyle = textStyleSourceEvaluator.evaluate(layer.textStyleSource),
          textColor = brushSourceEvaluator.evaluate(layer.textColor),
          text = scriptableEvaluator.evaluateString(layer.text),
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
          widgetModifiers = widgetModifierEvaluator.evaluate(layer.widgetModifiers),
          clickActions = clickActionEvaluator.evaluate(layer.clickActions),
          fill = scriptableEvaluator.evaluateBoolean(layer.fill),
          totalSteps = scriptableEvaluator.evaluateDouble(layer.totalSteps).toInt(),
          currentStep = scriptableEvaluator.evaluateDouble(layer.currentStep).toInt(),
          indicatorSize = scriptableEvaluator.evaluateDouble(layer.indicatorSize).dp,
          activeColor = scriptableEvaluator.evaluateColor(layer.activeColor),
          inactiveColor = scriptableEvaluator.evaluateColor(layer.inactiveColor),
          shape = layer.shape.getShape(),
        )
      emit(evaluated)
    }

  /**
   * Checks if [Layer.Cold.isEnabled] is `true` and allows evaluation only if enabled.
   *
   * @param coldLayer The layer to check
   * @param block The evaluation block to execute if layer is enabled
   * @return Flow that emits null initially, then executes block if layer is enabled. Null is
   *   emitted to indicate initial loading state
   */
  private fun <T : Layer.Evaluated> evaluateLayerIfEnabled(
    coldLayer: Layer.Cold,
    block: suspend FlowCollector<T?>.() -> Unit,
  ) = flow {
    emit(null)
    if (!scriptableEvaluator.evaluateBoolean(coldLayer.isEnabled)) return@flow
    block()
  }
}

private const val TAG = "LayerEvaluator"
