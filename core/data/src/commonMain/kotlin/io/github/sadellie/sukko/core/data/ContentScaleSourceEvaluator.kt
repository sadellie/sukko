package io.github.sadellie.sukko.core.data

import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.FixedScale
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.LayerContext
import io.github.sadellie.sukko.core.model.basic.ContentScaleSource

internal class ContentScaleSourceEvaluator(
  private val contentScaleSource: ContentScaleSource,
  private val layerContext: LayerContext,
  private val globals: Globals,
) {
  suspend fun evaluate() =
    when (contentScaleSource) {
      ContentScaleSource.Crop -> ContentScale.Crop
      ContentScaleSource.FillBounds -> ContentScale.FillBounds
      ContentScaleSource.FillHeight -> ContentScale.FillHeight
      ContentScaleSource.FillWidth -> ContentScale.FillWidth
      ContentScaleSource.Fit -> ContentScale.Fit
      is ContentScaleSource.FixedScale ->
        FixedScale(contentScaleSource.scale.getValue(layerContext, globals).toFloat())
      ContentScaleSource.Inside -> ContentScale.Inside
      ContentScaleSource.None -> ContentScale.None
    }
}
