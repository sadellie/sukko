package io.github.sadellie.sukko.feature.editor.selector

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import io.github.sadellie.sukko.core.data.LayerContextProvider
import io.github.sadellie.sukko.core.model.LayerContext

/**
 * Create an instance of [LayerContext]. Doesn't survive configuration changes.
 *
 * @return Remembered [LayerContext]
 */
@Composable
internal fun rememberLayerContext(): LayerContext {
  return remember { LayerContextProvider().provide() }
}
