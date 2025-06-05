package io.github.sadellie.sukko.core.model.layer

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import co.touchlab.kermit.Logger
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.LayerContext
import io.github.sadellie.sukko.core.model.basic.ClickAction
import io.github.sadellie.sukko.core.model.basic.ScriptableBoolean
import io.github.sadellie.sukko.core.model.modifier.WidgetModifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource

/** Basic layer info, unchanged after evaluation from [Cold] to [Evaluated] */
sealed interface Layer {
  val id: Int
  val parentId: Int?
  val name: String?
  val clickActions: List<ClickAction>
  val widgetModifiers: List<WidgetModifier>

  /** Before evaluating. */
  @Serializable
  sealed interface Cold : Layer {
    override val widgetModifiers: List<WidgetModifier.Cold>
    override val clickActions: List<ClickAction.Cold>
    val displayName: StringResource
    val displayDescription: StringResource
    val icon: ImageVector
    val isEnabled: ScriptableBoolean

    /**
     * Parent will wait for first emission. Emit null if not ready yet or layer is disabled
     *
     * @see evaluateAsFlowWithParent
     */
    fun evaluateAsFlow(layerContext: LayerContext, globals: Globals): Flow<Evaluated?>

    @OptIn(ExperimentalCoroutinesApi::class)
    fun evaluateAsFlowWithParent(
      layerContext: LayerContext,
      parentFlow: Flow<Evaluated?>?,
      globals: Globals,
    ): Flow<Evaluated?> {
      val evaluationFlow =
        parentFlow?.flatMapLatest { parent ->
          // layers parent is not drawn (not ready or disabled)
          if (parent == null) flowOf(null)
          // parent is drawn, allow children to emit their evaluations
          else evaluateAsFlow(layerContext, globals)
        }
          ?: // no parent (top level layers do not depend on other layers)
          evaluateAsFlow(layerContext, globals)
      return evaluationFlow.catch {
        Logger.e(TAG, it) { "evaluateAsFlowWithParent: Failed to evaluate" }
        emit(null)
      }
    }

    fun updateId(id: Int): Cold

    fun updateName(name: String): Cold

    fun updateClickActions(clickActions: List<ClickAction.Cold>): Cold

    fun updateWidgetModifiers(widgetModifiers: List<WidgetModifier.Cold>): Cold

    fun updateIsEnabled(isEnabled: ScriptableBoolean): Cold
  }

  /** Base widget layer fields. After evaluating. Ready to be rendered */
  sealed interface Evaluated : Layer {
    override val widgetModifiers: List<WidgetModifier.Evaluated>
    override val clickActions: List<ClickAction.Evaluated>

    @Composable
    fun Render(
      modifier: Modifier,
      renderOption: RenderOption?,
      childrenLayers: List<Evaluated>,
      onGloballyPositioned: (Int, Rect) -> Unit,
      scope: Any,
    )

    @Composable
    fun createModifier(
      baseModifier: Modifier,
      renderOption: RenderOption?,
      onGloballyPositioned: (Int, Rect) -> Unit,
      scope: Any,
    ): Modifier {
      var modifier =
        baseModifier.onGloballyPositioned { onGloballyPositioned(id, it.boundsInWindow()) }
      if (widgetModifiers.isEmpty()) return modifier
      for (widgetModifier in widgetModifiers) {
        modifier = widgetModifier.addToModifier(modifier, scope)
      }
      return modifier
    }
  }
}

/** Reserved for debugging API */
sealed interface RenderOption

/**
 * Evaluates enabled layers and their children. Children will not be evaluated unless parent layer
 * is enabled and ready to be drawn.
 *
 * @return Flow enabled and ready to be drawn layers (not null)
 */
suspend fun List<Layer.Cold>.evaluateEnabled(
  layerContext: LayerContext,
  globals: Globals,
): Flow<List<Layer.Evaluated>> {
  if (this.isEmpty()) return flowOf(emptyList())
  val flows = mutableListOf<Flow<Layer.Evaluated?>>()
  // state each flow in this scope so children consume same flow
  val scope = CoroutineScope(currentCoroutineContext())

  suspend fun traverse(parentLayer: Layer.Cold?, parentFlow: Flow<Layer.Evaluated?>?) {
    val layersInParent = this.filter { it.parentId == parentLayer?.id }
    layersInParent.forEach { layer ->
      // stateIn parent flow so children receive same evaluation (is parent enabled) from higher
      // level
      val pFlow = layer.evaluateAsFlowWithParent(layerContext, parentFlow, globals).stateIn(scope)
      flows.add(pFlow)
      traverse(layer, pFlow)
    }
  }
  // visit root and go deeper
  traverse(null, null)

  return combine(flows) { it.toList().filterNotNull() }
}

private const val TAG = "Layer"
