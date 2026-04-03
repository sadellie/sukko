package io.github.sadellie.sukko.core.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import google.material.design.symbols.Error
import google.material.design.symbols.Symbols
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.common_error
import io.github.sadellie.sukko.resources.common_error_text
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ErrorScreenPlaceholder(
  onNavigateUp: () -> Unit,
  screenTitle: String = stringResource(Res.string.common_error),
  bodyTitle: String = stringResource(Res.string.common_error),
  text: String = stringResource(Res.string.common_error_text),
) {
  ScaffoldWithTopAppBar(
    title = { Text(screenTitle) },
    navigationIcon = { NavigateUpButton(onNavigateUp) },
  ) { paddingValues ->
    ScenePlaceholder(
      modifier = Modifier.padding(paddingValues).fillMaxSize(),
      icon = Symbols.Error,
      title = bodyTitle,
      text = text,
    )
  }
}

@Composable
@Preview
private fun PreviewErrorScreenPlaceholder() {
  ErrorScreenPlaceholder(onNavigateUp = {})
}
