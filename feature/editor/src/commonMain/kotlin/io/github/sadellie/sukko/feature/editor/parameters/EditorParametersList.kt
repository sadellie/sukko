package io.github.sadellie.sukko.feature.editor.parameters

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.sadellie.sukko.core.designsystem.Preview2
import io.github.sadellie.sukko.core.designsystem.theme.ListArrangement
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.basic.ImageUriSource
import io.github.sadellie.sukko.core.model.basic.ScriptableString
import io.github.sadellie.sukko.core.model.layer.ColdBoxLayer
import io.github.sadellie.sukko.core.model.layer.ColdColumnLayer
import io.github.sadellie.sukko.core.model.layer.ColdImageLayer
import io.github.sadellie.sukko.core.model.layer.ColdProgressBarLayer
import io.github.sadellie.sukko.core.model.layer.ColdRowLayer
import io.github.sadellie.sukko.core.model.layer.ColdTextLayer
import io.github.sadellie.sukko.core.model.layer.Layer
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider

@Composable
internal fun EditorParametersList(
  modifier: Modifier,
  onUpdateLayer: (Layer.Cold) -> Unit,
  layer: Layer.Cold,
  compactListMode: Boolean,
  bottomContentPadding: Dp,
  globals: Globals,
) {
  ScrollableColumn(modifier = modifier, bottomContentPadding = bottomContentPadding) {
    LayerParameters(
      onUpdateLayer = onUpdateLayer,
      layer = layer,
      compactListMode = compactListMode,
      globals = globals,
    )
  }
}

@Composable
private fun LayerParameters(
  onUpdateLayer: (Layer.Cold) -> Unit,
  layer: Layer.Cold,
  compactListMode: Boolean,
  globals: Globals,
) {
  Column(
    modifier = Modifier.clip(MaterialTheme.shapes.large),
    verticalArrangement = ListArrangement,
  ) {
    EditorParametersCommon(
      layer = layer,
      onUpdateLayer = onUpdateLayer,
      compactListMode = compactListMode,
      globals = globals,
    )
    when (layer) {
      is ColdBoxLayer ->
        EditorParametersBoxLayer(
          onUpdateLayer = onUpdateLayer,
          layer = layer,
          compactListMode = compactListMode,
        )
      is ColdColumnLayer ->
        EditorParametersColumnLayer(
          onUpdateLayer = onUpdateLayer,
          layer = layer,
          compactListMode = compactListMode,
        )
      is ColdRowLayer ->
        EditorParametersRowLayer(
          onUpdateLayer = onUpdateLayer,
          layer = layer,
          compactListMode = compactListMode,
        )
      is ColdTextLayer ->
        EditorParametersTextLayer(
          layer = layer,
          onUpdateLayer = onUpdateLayer,
          compactListMode = compactListMode,
          globals = globals,
        )
      is ColdImageLayer ->
        EditorParametersImageLayer(
          onUpdateLayer = onUpdateLayer,
          layer = layer,
          compactListMode = compactListMode,
          globals = globals,
        )
      is ColdProgressBarLayer ->
        EditorParametersProgressBarLayer(
          onUpdateLayer = onUpdateLayer,
          layer = layer,
          compactListMode = compactListMode,
          globals = globals,
        )
    }
  }
}

@Composable
private fun ScrollableColumn(
  modifier: Modifier,
  bottomContentPadding: Dp,
  content: @Composable () -> Unit,
) {
  Column(Modifier.verticalScroll(rememberScrollState()).then(modifier)) {
    content()
    if (bottomContentPadding > 0.dp) Spacer(Modifier.size(bottomContentPadding))
  }
}

@Preview
@Composable
private fun PreviewEditorParametersList(
  @PreviewParameter(LayersCollection::class) layer: Layer.Cold
) = Preview2 {
  var currentLayer by remember { mutableStateOf(layer) }
  EditorParametersList(
    modifier = Modifier.background(MaterialTheme.colorScheme.surface).fillMaxSize(),
    onUpdateLayer = { currentLayer = it },
    layer = currentLayer,
    compactListMode = false,
    bottomContentPadding = 0.dp,
    globals = Globals(),
  )
}

private class LayersCollection(
  override val values: Sequence<Layer.Cold> =
    sequenceOf(
      ColdColumnLayer(id = 0, parentId = null),
      ColdTextLayer(id = 0, parentId = null),
      ColdTextLayer(id = 0, parentId = null, text = ScriptableString.Fixed("text")),
      ColdImageLayer(id = 0, parentId = null, imageUriSource = ImageUriSource.AlbumCover),
      ColdProgressBarLayer(id = 0, parentId = null),
    )
) : PreviewParameterProvider<Layer.Cold>
