package io.github.sadellie.sukko.core.model.layer

import androidx.compose.foundation.border
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import io.github.sadellie.sukko.core.model.basic.ClickAction
import io.github.sadellie.sukko.core.model.basic.ScriptableBoolean
import io.github.sadellie.sukko.core.model.modifier.WidgetModifier
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
      renderOption: RenderOption,
      childrenLayers: List<Evaluated>,
      onGloballyPositioned: (Int, Rect) -> Unit,
      scope: Any,
    )

    @Composable
    fun createModifier(
      baseModifier: Modifier,
      renderOption: RenderOption,
      onGloballyPositioned: (Int, Rect) -> Unit,
      scope: Any,
    ): Modifier {
      var modifier =
        when (renderOption) {
          RenderOption.HomeScreen -> baseModifier
          is RenderOption.Editor ->
            baseModifier.ifTrue(
              renderOption.highlightSelectedLayer && renderOption.selectedLayerId == id
            ) {
              Modifier.border(2.dp, MaterialTheme.colorScheme.primary)
            }
        }.onGloballyPositioned { onGloballyPositioned(id, it.boundsInWindow()) }
      if (widgetModifiers.isEmpty()) return modifier
      for (widgetModifier in widgetModifiers) {
        modifier = widgetModifier.addToModifier(modifier, scope)
      }
      return modifier
    }
  }
}

sealed interface RenderOption {
  data class Editor(val selectedLayerId: Int?, val highlightSelectedLayer: Boolean) : RenderOption

  data object HomeScreen : RenderOption
}

private inline fun Modifier.ifTrue(value: Boolean, block: Modifier.() -> Modifier) =
  if (value) {
    then(block())
  } else {
    this
  }
