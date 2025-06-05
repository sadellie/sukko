package io.github.sadellie.sukko.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.sadellie.sukko.core.designsystem.LocalImageLoader
import io.github.sadellie.sukko.core.designsystem.LocalWindowSize
import io.github.sadellie.sukko.core.designsystem.theme.Sizes
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.common_my_presets
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun <T> WidgetDataList(
  modifier: Modifier,
  gridState: LazyGridState = rememberLazyGridState(),
  contentPadding: PaddingValues,
  widgets: List<T>,
  key: (T) -> Any,
  previewSrc: (T) -> String?,
  name: @Composable (T) -> String,
  onClick: (T) -> Unit,
) {
  val widthSizeClass = LocalWindowSize.current.widthSizeClass
  val columns =
    remember(widthSizeClass) {
      if (widthSizeClass == WindowWidthSizeClass.Compact) GridCells.Adaptive(156.dp)
      else GridCells.Adaptive(288.dp)
    }
  MediumWidgetGrid(
    modifier = modifier,
    gridState = gridState,
    contentPadding = contentPadding,
    columns = columns,
    widgets = widgets,
    key = key,
    previewSrc = previewSrc,
    name = name,
    onClick = onClick,
  )
}

@Composable
fun <T> WidgetDataPresetList(
  modifier: Modifier,
  widgetDataPresetsBuiltIn: List<T>,
  widgetDataPresetsCustom: List<T>,
  key: (T) -> Any,
  onClick: (T) -> Unit,
  previewSrc: (T) -> String?,
  name: @Composable (T) -> String,
  placeholder: @Composable () -> Unit,
  contentPadding: PaddingValues,
) {
  val gridState = rememberLazyGridState()
  val widthSizeClass = LocalWindowSize.current.widthSizeClass
  val columns =
    remember(widthSizeClass) {
      if (widthSizeClass == WindowWidthSizeClass.Compact) GridCells.Adaptive(156.dp)
      else GridCells.Adaptive(288.dp)
    }
  LazyVerticalGrid(
    modifier = modifier,
    state = gridState,
    columns = columns,
    contentPadding = contentPadding,
    verticalArrangement = Arrangement.spacedBy(Sizes.small),
    horizontalArrangement = Arrangement.spacedBy(Sizes.small),
  ) {
    if (widgetDataPresetsBuiltIn.isNotEmpty()) {
      items(widgetDataPresetsBuiltIn, key) { item ->
        WidgetDataCardItem(
          modifier = Modifier.fillMaxWidth(),
          onClick = { onClick(item) },
          previewSrc = previewSrc(item),
          label = name(item),
        )
      }
    }

    item("custom", span = { GridItemSpan(maxLineSpan) }) {
      ListHeader(stringResource(Res.string.common_my_presets))
    }
    if (widgetDataPresetsCustom.isNotEmpty()) {
      items(widgetDataPresetsCustom, key) { item ->
        WidgetDataCardItem(
          modifier = Modifier.fillMaxWidth(),
          onClick = { onClick(item) },
          previewSrc = previewSrc(item),
          label = name(item),
        )
      }
    } else {
      item("placeholder", span = { GridItemSpan(maxLineSpan) }) { placeholder() }
    }
  }
}

@Composable
private fun <T> MediumWidgetGrid(
  modifier: Modifier,
  gridState: LazyGridState,
  contentPadding: PaddingValues,
  columns: GridCells,
  widgets: List<T>,
  key: (T) -> Any,
  previewSrc: (T) -> String?,
  name: @Composable (T) -> String,
  onClick: (T) -> Unit,
) {
  LazyVerticalGrid(
    state = gridState,
    modifier = modifier,
    columns = columns,
    contentPadding = contentPadding,
    verticalArrangement = Arrangement.spacedBy(Sizes.small),
    horizontalArrangement = Arrangement.spacedBy(Sizes.small),
  ) {
    items(items = widgets, key = key) { widgetData ->
      WidgetDataCardItem(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onClick(widgetData) },
        previewSrc = previewSrc(widgetData),
        label = name(widgetData),
      )
    }
  }
}

@Composable
private fun WidgetDataCardItem(
  modifier: Modifier,
  onClick: () -> Unit,
  previewSrc: String?,
  label: String,
) {
  Column(
    modifier =
      modifier
        .clip(MaterialTheme.shapes.large)
        .background(MaterialTheme.colorScheme.surfaceBright)
        .clickable { onClick() }
  ) {
    Box(modifier = Modifier.fillMaxWidth()) {
      AsyncImage(
        model = previewSrc,
        imageLoader = LocalImageLoader.current,
        contentDescription = null,
        modifier = Modifier.clip(MaterialTheme.shapes.large).aspectRatio(1f),
        contentScale = ContentScale.Fit,
      )
    }
    Row(modifier = Modifier.padding(Sizes.large), verticalAlignment = Alignment.CenterVertically) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = label,
          style = MaterialTheme.typography.bodyLarge,
          color = MaterialTheme.colorScheme.onSurface,
          maxLines = 1,
          minLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
      }
    }
  }
}

@Composable
@Preview
private fun PreviewWidgetDataListItem() {
  WidgetDataCardItem(
    modifier = Modifier.size(200.dp, 300.dp),
    previewSrc = null,
    label = "Widget 1",
    onClick = {},
  )
}
