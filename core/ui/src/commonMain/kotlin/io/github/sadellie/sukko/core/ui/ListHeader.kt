package io.github.sadellie.sukko.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import io.github.sadellie.sukko.core.designsystem.theme.ListArrangement
import io.github.sadellie.sukko.core.designsystem.theme.Sizes
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ListHeader(
  text: String,
  modifier: Modifier =
    Modifier.padding(
      start = Sizes.small,
      end = Sizes.small,
      top = Sizes.large,
      bottom = Sizes.small,
    ),
  color: Color = MaterialTheme.colorScheme.onSurface,
  style: TextStyle = MaterialTheme.typography.labelLarge,
) {
  Text(text = text, modifier = modifier, color = color, style = style)
}

@Composable
@Preview
private fun PreviewListHeader() {
  LazyColumn(
    modifier = Modifier.background(MaterialTheme.colorScheme.background),
    verticalArrangement = ListArrangement,
  ) {
    item { ListHeader("Text") }
    items(10) {
      ListItem2(
        headlineContent = { Text("Item $it") },
        shape = ListItemDefaults.listedShape(it, 10),
      )
    }
  }
}
