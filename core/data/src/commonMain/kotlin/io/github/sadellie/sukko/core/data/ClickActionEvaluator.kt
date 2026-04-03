package io.github.sadellie.sukko.core.data

import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.LayerContext
import io.github.sadellie.sukko.core.model.basic.ClickAction

internal class ClickActionEvaluator(
  private val clickActions: List<ClickAction.Cold>,
  private val layerContext: LayerContext,
  private val globals: Globals,
) {
  suspend fun evaluate() = clickActions.map { evaluateClickAction(it) }

  private suspend fun evaluateClickAction(clickAction: ClickAction.Cold) =
    when (clickAction) {
      is ClickAction.Cold.OpenLink ->
        ClickAction.Evaluated.OpenLink(
          clickAction.id,
          clickAction.url.getValue(layerContext, globals),
        )
      is ClickAction.LaunchApp,
      is ClickAction.MediaOpenPlayer,
      is ClickAction.MediaPause,
      is ClickAction.MediaPlay,
      is ClickAction.MediaSkipToNext,
      is ClickAction.MediaSkipToPrevious -> clickAction
    }
}
