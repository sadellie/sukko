package io.github.sadellie.sukko.core.model.layer

import androidx.compose.foundation.border
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import io.github.pingpongboss.explodedlayers.SeparateLayer
import io.github.sadellie.sukko.core.model.basic.ClickAction
import io.github.sadellie.sukko.core.model.basic.ScriptableBoolean
import io.github.sadellie.sukko.core.model.modifier.WidgetModifier
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource

/**
 * Basic layer info, unchanged after evaluation from [Cold] to [Evaluated]
 *
 * @property id Unique id (within widget).
 * @property name Layer name, can be null or empty.
 * @property clickActions [ClickAction]s when user clicks on this layer, order matters.
 * @property widgetModifiers [WidgetModifier] for styling, order matters.
 */
sealed interface Layer {
  val id: Int
  val parentId: Int?
  val name: String?
  val clickActions: List<ClickAction>
  val widgetModifiers: List<WidgetModifier>

  /**
   * Layer before evaluation. Can be serialized and stored in database
   *
   * @property widgetModifiers [WidgetModifier.Cold] before being evaluated.
   * @property clickActions [ClickAction.Cold] before being evaluated.
   * @property displayName Display name of this layer type for UI, not used in evaluation.
   * @property displayDescription Short description of this layer type, not used in evaluation.
   * @property icon Layer icon for editor UI, not used in evaluation.
   * @property isEnabled When false this layer and it's children will not be evaluated.
   */
  @Serializable
  sealed interface Cold : Layer {
    override val widgetModifiers: List<WidgetModifier.Cold>
    override val clickActions: List<ClickAction.Cold>
    val displayName: StringResource
    val displayDescription: StringResource
    val icon: ImageVector
    val isEnabled: ScriptableBoolean

    fun updateId(id: Int): Cold

    fun updateName(name: String): Cold

    fun updateClickActions(clickActions: List<ClickAction.Cold>): Cold

    fun updateWidgetModifiers(widgetModifiers: List<WidgetModifier.Cold>): Cold

    fun updateIsEnabled(isEnabled: ScriptableBoolean): Cold
  }

  /**
   * Base widget layer fields. After evaluating. Ready to be rendered
   *
   * @property widgetModifiers [WidgetModifier.Evaluated] ready to be converted into [Modifier].
   * @property clickActions [ClickAction.Evaluated] ready to be converted into click areas when
   *   rendering on home screen.
   */
  sealed interface Evaluated : Layer {
    override val widgetModifiers: List<WidgetModifier.Evaluated>
    override val clickActions: List<ClickAction.Evaluated>

    @Composable
    fun Render(
      modifier: Modifier,
      renderOption: RenderOption,
      childrenLayers: List<Evaluated>,
      onGloballyPositioned: (Int, LayoutCoordinates) -> Unit,
      scope: Any,
    ) {
      val modifier = createModifier(modifier, renderOption, onGloballyPositioned, scope)
      if (renderOption is RenderOption.Editor) {
        SeparateLayer(modifier) {
          BaseRender(
            modifier = Modifier,
            renderOption = renderOption,
            childrenLayers = childrenLayers,
            onGloballyPositioned = onGloballyPositioned,
          )
        }
      } else {
        BaseRender(
          modifier = modifier,
          renderOption = renderOption,
          childrenLayers = childrenLayers,
          onGloballyPositioned = onGloballyPositioned,
        )
      }
    }

    /** Base render method, use [Render] for composition. */
    @Composable
    fun BaseRender(
      modifier: Modifier,
      renderOption: RenderOption,
      childrenLayers: List<Evaluated>,
      onGloballyPositioned: (Int, LayoutCoordinates) -> Unit,
    )

    @Composable
    private fun createModifier(
      baseModifier: Modifier,
      renderOption: RenderOption,
      onGloballyPositioned: (Int, LayoutCoordinates) -> Unit,
      scope: Any,
    ): Modifier {
      // apply mandatory modifiers (from renderer)
      var modifier =
        renderOption.toModifier(baseModifier).onGloballyPositioned {
          if (clickActions.isNotEmpty()) onGloballyPositioned(id, it)
        }

      // styling modifiers (from user)
      if (widgetModifiers.isEmpty()) return modifier
      for (widgetModifier in widgetModifiers) {
        modifier = widgetModifier.addToModifier(modifier, scope)
      }
      return modifier
    }

    @Composable
    private fun RenderOption.toModifier(baseModifier: Modifier): Modifier {
      return when (this) {
        is RenderOption.Editor ->
          baseModifier.ifTrue(this.highlightSelectedLayer && this.selectedLayerId == id) {
            Modifier.border(2.dp, MaterialTheme.colorScheme.primary)
          }
        RenderOption.HomeScreen -> baseModifier
      }
    }
  }
}

sealed interface RenderOption {
  data class Editor(val selectedLayerId: Int?, val highlightSelectedLayer: Boolean) : RenderOption

  data object HomeScreen : RenderOption
}

private inline fun Modifier.ifTrue(value: Boolean, block: Modifier.() -> Modifier) =
  if (value) then(block()) else this
