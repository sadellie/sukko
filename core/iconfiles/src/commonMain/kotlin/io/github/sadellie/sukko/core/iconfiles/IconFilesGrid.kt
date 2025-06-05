package io.github.sadellie.sukko.core.iconfiles

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import google.material.design.symbols.Error
import google.material.design.symbols.Symbols
import io.github.sadellie.sukko.core.designsystem.LocalFilesDirPath
import io.github.sadellie.sukko.core.designsystem.LocalImageLoader
import io.github.sadellie.sukko.core.designsystem.theme.Sizes
import io.github.sadellie.sukko.core.ui.ScenePlaceholder
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.icon_files_grid_placeholder_text
import io.github.sadellie.sukko.resources.icon_files_grid_placeholder_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun IconFilesGrid(
  modifier: Modifier,
  iconFiles: List<IconFile>,
  onClick: (selected: IconFile) -> Unit,
  selectedIconFile: IconFile? = null,
  contentPadding: PaddingValues = PaddingValues(0.dp),
) {
  if (iconFiles.isEmpty()) {
    ScenePlaceholder(
      modifier = modifier,
      icon = Symbols.Error,
      title = stringResource(Res.string.icon_files_grid_placeholder_title),
      text = stringResource(Res.string.icon_files_grid_placeholder_text),
    )
    return
  }

  LazyVerticalGrid(
    modifier = modifier,
    columns = GridCells.Adaptive(minSize = 46.dp),
    verticalArrangement = Arrangement.spacedBy(Sizes.small),
    horizontalArrangement = Arrangement.spacedBy(Sizes.small),
    contentPadding = contentPadding,
  ) {
    items(iconFiles, key = { it.fileName }) { iconFile ->
      Column(
        modifier =
          Modifier.clip(MaterialTheme.shapes.small).clickable(onClick = { onClick(iconFile) }),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Sizes.small),
      ) {
        val transition = updateTransition(iconFile == selectedIconFile)
        val animatedContainerColor =
          transition.animateColor {
            if (it) MaterialTheme.colorScheme.secondaryContainer
            else MaterialTheme.colorScheme.surfaceBright
          }
        val animatedContentColor =
          transition.animateColor {
            if (it) MaterialTheme.colorScheme.onSecondaryContainer
            else MaterialTheme.colorScheme.onSurface
          }
        AsyncImage(
          model = iconFile.getFullPath(LocalFilesDirPath.current).toString(),
          imageLoader = LocalImageLoader.current,
          contentDescription = null,
          modifier =
            Modifier.clip(MaterialTheme.shapes.small)
              .background(animatedContainerColor.value)
              .padding(Sizes.small)
              .aspectRatio(1f),
          colorFilter = ColorFilter.tint(animatedContentColor.value),
          contentScale = ContentScale.Fit,
        )
        Text(
          text = iconFile.name,
          style = MaterialTheme.typography.labelSmall,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          color = LocalContentColor.current,
        )
      }
    }
  }
}
