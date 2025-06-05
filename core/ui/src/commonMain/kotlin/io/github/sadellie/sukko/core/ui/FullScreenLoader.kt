package io.github.sadellie.sukko.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.common_loading
import org.jetbrains.compose.resources.stringResource

@Composable
fun LoadingScaffold(disableBack: Boolean) {
  Scaffold(containerColor = MaterialTheme.colorScheme.surfaceContainer) { paddingValues ->
    LoadingBox(modifier = Modifier.fillMaxSize())
    BackHandler(disableBack) {}
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadingScaffoldWithTopAppBar(
  onNavigateUp: () -> Unit,
  disableBack: Boolean,
  title: String = stringResource(Res.string.common_loading),
) {
  ScaffoldWithTopAppBar(
    title = { Text(title) },
    navigationIcon = { NavigateUpButton(onNavigateUp, enabled = !disableBack) },
  ) { paddingValues ->
    LoadingBox(modifier = Modifier.padding(paddingValues).fillMaxSize())
    BackHandler(disableBack) {}
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadingScaffoldWithLargeTopAppBar(
  onNavigateUp: () -> Unit,
  disableBack: Boolean,
  title: String = stringResource(Res.string.common_loading),
) {
  ScaffoldWithLargeTopAppBar(
    title = { Text(title) },
    navigationIcon = { NavigateUpButton(onNavigateUp, enabled = !disableBack) },
    scrollBehavior = null,
  ) { paddingValues ->
    LoadingBox(modifier = Modifier.padding(paddingValues).fillMaxSize())
    BackHandler(disableBack) {}
  }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LoadingBox(modifier: Modifier) {
  Box(modifier = modifier, contentAlignment = Alignment.Center) { LoadingIndicator() }
}
