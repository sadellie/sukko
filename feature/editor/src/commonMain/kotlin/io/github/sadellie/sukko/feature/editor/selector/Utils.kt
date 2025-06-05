package io.github.sadellie.sukko.feature.editor.selector

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import io.github.sadellie.sukko.core.data.LayerContextProvider
import io.github.sadellie.sukko.core.model.LayerContext

/**
 * Create an instance of [LayerContext]. Doesn't survive configuration changes.
 *
 * @return Remembered [LayerContext]
 */
@Composable
internal fun rememberLayerContext(): LayerContext {
  val coroutineScope = rememberCoroutineScope()
  return remember { LayerContextProvider().provide(coroutineScope.coroutineContext) }
}
