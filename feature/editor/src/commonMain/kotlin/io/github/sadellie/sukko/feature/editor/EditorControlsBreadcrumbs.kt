package io.github.sadellie.sukko.feature.editor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import google.material.design.symbols.ChevronRight
import google.material.design.symbols.Home
import google.material.design.symbols.Symbols
import io.github.sadellie.sukko.core.designsystem.Preview2
import io.github.sadellie.sukko.core.designsystem.theme.Sizes
import io.github.sadellie.sukko.core.model.layer.ColdColumnLayer
import io.github.sadellie.sukko.core.model.layer.Layer
import io.github.sadellie.sukko.core.ui.BackHandler
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.editor_layer_name_placeholder
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun EditorBreadcrumbs(
  modifier: Modifier,
  onClick: (layerId: Int?) -> Unit,
  breadcrumbs: List<Layer.Cold>,
) {
  val listState =
    rememberLazyListState(initialFirstVisibleItemIndex = breadcrumbs.lastIndex.coerceAtLeast(0))
  val coroutineScope = rememberCoroutineScope()

  LaunchedEffect(breadcrumbs.size) {
    coroutineScope.launch { listState.animateScrollToItem(breadcrumbs.lastIndex.coerceAtLeast(0)) }
  }

  BackHandler(breadcrumbs.isNotEmpty()) { onClick(breadcrumbs.lastOrNull()?.parentId) }

  LazyRow(
    modifier = modifier.minimumInteractiveComponentSize().padding(Sizes.small),
    state = listState,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    item(Int.MIN_VALUE) {
      IconButton(
        shapes = IconButtonDefaults.shapes(),
        onClick = { onClick(null) },
        modifier = Modifier.size(IconButtonDefaults.smallContainerSize()),
      ) {
        Icon(
          imageVector = Symbols.Home,
          contentDescription = null,
          modifier = Modifier.size(IconButtonDefaults.smallIconSize),
        )
      }
    }

    items(breadcrumbs, { it.id }) { layer ->
      Row(
        modifier =
          Modifier.clip(MaterialTheme.shapes.medium)
            .clickable(role = Role.Tab) { onClick(layer.id) }
            .height(40.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Icon(
          modifier = Modifier.size(IconButtonDefaults.smallIconSize),
          imageVector = Symbols.ChevronRight,
          contentDescription = null,
        )
        Text(
          modifier = Modifier.padding(horizontal = Sizes.small),
          text = layer.name ?: stringResource(Res.string.editor_layer_name_placeholder, layer.id),
          style = MaterialTheme.typography.bodyMedium,
        )
      }
    }
  }
}

@Preview
@Composable
private fun PreviewEditorBreadcrumbs() = Preview2 {
  EditorBreadcrumbs(
    modifier = Modifier,
    onClick = {},
    breadcrumbs = List(7) { ColdColumnLayer(it, null) },
  )
}
