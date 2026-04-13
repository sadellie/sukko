package io.github.sadellie.sukko.core.data

import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.FixedScale
import io.github.sadellie.sukko.core.model.basic.ContentScaleSource

internal class ContentScaleSourceEvaluator(private val scriptableEvaluator: ScriptableEvaluator) {
  suspend fun evaluate(contentScaleSource: ContentScaleSource) =
    when (contentScaleSource) {
      ContentScaleSource.Crop -> ContentScale.Crop
      ContentScaleSource.FillBounds -> ContentScale.FillBounds
      ContentScaleSource.FillHeight -> ContentScale.FillHeight
      ContentScaleSource.FillWidth -> ContentScale.FillWidth
      ContentScaleSource.Fit -> ContentScale.Fit
      is ContentScaleSource.FixedScale ->
        FixedScale(scriptableEvaluator.evaluateDouble(contentScaleSource.scale).toFloat())
      ContentScaleSource.Inside -> ContentScale.Inside
      ContentScaleSource.None -> ContentScale.None
    }
}
