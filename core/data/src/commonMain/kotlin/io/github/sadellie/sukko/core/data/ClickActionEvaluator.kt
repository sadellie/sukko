package io.github.sadellie.sukko.core.data

import io.github.sadellie.sukko.core.model.basic.ClickAction

internal class ClickActionEvaluator(private val scriptableEvaluator: ScriptableEvaluator) {
  suspend fun evaluate(clickActions: List<ClickAction.Cold>) =
    clickActions.map { evaluateClickAction(it) }

  private suspend fun evaluateClickAction(clickAction: ClickAction.Cold) =
    when (clickAction) {
      is ClickAction.Cold.OpenLink ->
        ClickAction.Evaluated.OpenLink(
          id = clickAction.id,
          url = scriptableEvaluator.evaluateString(clickAction.url),
        )
      is ClickAction.LaunchApp,
      is ClickAction.RunScript,
      is ClickAction.MediaAction -> clickAction
    }
}
