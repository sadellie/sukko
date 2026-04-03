package io.github.sadellie.sukko.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import google.material.design.symbols.SearchOff
import google.material.design.symbols.Symbols
import io.github.sadellie.sukko.core.designsystem.theme.Sizes

@Composable
fun ScenePlaceholder(
  modifier: Modifier = Modifier,
  icon: ImageVector,
  title: String,
  text: String,
  onClick: () -> Unit,
  actionLabel: String,
) {
  ScenePlaceholder(modifier = modifier, icon = icon, title = title, text = text) {
    OutlinedButton(onClick = onClick, shapes = ButtonDefaults.shapes()) { Text(actionLabel) }
  }
}

@Composable
fun ScenePlaceholder(
  modifier: Modifier = Modifier,
  icon: ImageVector,
  title: String,
  text: String,
  content: (@Composable () -> Unit)? = null,
) {
  Column(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(Sizes.small, Alignment.CenterVertically),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(48.dp))
    Text(text = title, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
    Text(text = text, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
    content?.invoke()
  }
}

@Composable
@Preview
private fun PreviewScenePlaceholder() {
  ScenePlaceholder(
    modifier = Modifier.background(MaterialTheme.colorScheme.background).fillMaxSize(),
    icon = Symbols.SearchOff,
    title = "List is empty",
    text = "Click button below",
    onClick = {},
    actionLabel = "Action",
  )
}
