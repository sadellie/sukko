package io.github.sadellie.sukko.feature.editor

import androidx.compose.ui.unit.DpSize
import io.github.sadellie.sukko.core.model.WidgetData
import io.github.sadellie.sukko.core.model.basic.ClickAction
import io.github.sadellie.sukko.core.model.basic.GlobalValue
import io.github.sadellie.sukko.core.model.layer.Layer
import io.github.sadellie.sukko.core.model.modifier.WidgetModifier

internal data class EditorUIState(
  val widgetData: WidgetData,
  val widgetDataSaverState: WidgetDataSaverState,
  val isWidgetDataSaved: Boolean,
  val evaluatedLayers: List<Layer.Evaluated>,
  val canvasSize: DpSize,
  val viewerState: ViewerState,
)

internal data class ViewerState(
  val currentLayer: Layer.Cold?,
  val parentLayer: Layer.Cold?,
  val breadcrumbs: List<Layer.Cold>,
  val loadedLayers: List<Layer.Cold>,
)

internal sealed interface WidgetDataSaverState {
  data object NotRunning : WidgetDataSaverState

  data object Running : WidgetDataSaverState

  data object Error : WidgetDataSaverState

  data object MissingNotificationListener : WidgetDataSaverState
}

internal sealed interface EditorEvent {
  sealed interface WidgetModifierAction : EditorEvent {
    data class Add(val newModifier: WidgetModifier.Cold, val layerId: Int) : WidgetModifierAction

    data class Update(val updatedModifier: WidgetModifier.Cold, val layerId: Int) :
      WidgetModifierAction

    data class Delete(val modifierToDelete: WidgetModifier.Cold, val layerId: Int) :
      WidgetModifierAction

    data class Reorder(val updatedModifiers: List<WidgetModifier.Cold>, val layerId: Int) :
      WidgetModifierAction
  }

  sealed interface LayerAction : EditorEvent {
    data class Add(val layerToAdd: Layer.Cold) : LayerAction

    data class Delete(val layerId: Int) : LayerAction

    data class Update(val updatedLayer: Layer.Cold) : LayerAction

    data class Reorder(val updatedLayers: List<Layer.Cold>) : LayerAction
  }

  sealed interface ClickActionAction : EditorEvent {
    data class Add(val clickActionToAdd: ClickAction.Cold, val layerId: Int) : ClickActionAction

    data class Update(val updatedClickAction: ClickAction.Cold, val layerId: Int) :
      ClickActionAction

    data class Delete(val clickActionToDelete: ClickAction.Cold, val layerId: Int) :
      ClickActionAction

    data class Reorder(val updatedClickActions: List<ClickAction.Cold>, val layerId: Int) :
      ClickActionAction
  }

  sealed interface GlobalAction : EditorEvent {
    data class Add(val globalToAdd: GlobalValue<*>) : GlobalAction

    data class Update(val globalToUpdate: GlobalValue<*>) : GlobalAction

    data class Delete(val globalToDelete: GlobalValue<*>) : GlobalAction
  }
}
