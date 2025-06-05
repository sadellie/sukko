package io.github.sadellie.sukko.core.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.github.sadellie.sukko.core.designsystem.theme.Sizes

/**
 * Template screen. Uses [Scaffold] and [TopAppBar]
 *
 * @param modifier See [Scaffold]
 * @param title See [TopAppBar]
 * @param navigationIcon See [TopAppBar]
 * @param actions See [TopAppBar]
 * @param topAppBarColors See [TopAppBar]
 * @param floatingActionButton See [Scaffold]
 * @param scrollBehavior See [TopAppBar]
 * @param content See [Scaffold]
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldWithTopAppBar(
  modifier: Modifier = Modifier,
  title: @Composable () -> Unit,
  navigationIcon: @Composable () -> Unit = {},
  actions: @Composable RowScope.() -> Unit = {},
  containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
  topAppBarColors: TopAppBarColors =
    TopAppBarDefaults.topAppBarColors(containerColor = containerColor),
  snackbarHost: @Composable (() -> Unit) = {},
  floatingActionButton: @Composable () -> Unit = {},
  floatingActionButtonPosition: FabPosition = FabPosition.End,
  scrollBehavior: TopAppBarScrollBehavior? = null,
  content: @Composable (PaddingValues) -> Unit,
) {
  Scaffold(
    modifier = modifier,
    topBar = {
      TopAppBar(
        title = title,
        navigationIcon = { navigationIcon() },
        actions = {
          actions()
          Spacer(Modifier.width(Sizes.small))
        },
        colors = topAppBarColors,
        scrollBehavior = scrollBehavior,
      )
    },
    floatingActionButton = floatingActionButton,
    floatingActionButtonPosition = floatingActionButtonPosition,
    snackbarHost = snackbarHost,
    containerColor = containerColor,
    content = content,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldWithLargeTopAppBar(
  modifier: Modifier = Modifier,
  title: @Composable () -> Unit,
  navigationIcon: @Composable () -> Unit = {},
  actions: @Composable RowScope.() -> Unit = {},
  containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
  topAppBarColors: TopAppBarColors =
    TopAppBarDefaults.topAppBarColors(containerColor = containerColor),
  snackbarHost: @Composable (() -> Unit) = {},
  floatingActionButton: @Composable () -> Unit = {},
  floatingActionButtonPosition: FabPosition = FabPosition.End,
  scrollBehavior: TopAppBarScrollBehavior?,
  content: @Composable (PaddingValues) -> Unit,
) {
  Scaffold(
    modifier = modifier,
    topBar = {
      LargeFlexibleTopAppBar(
        title = title,
        navigationIcon = { navigationIcon() },
        actions = {
          actions()
          Spacer(Modifier.width(Sizes.small))
        },
        colors = topAppBarColors,
        scrollBehavior = scrollBehavior,
      )
    },
    floatingActionButton = floatingActionButton,
    floatingActionButtonPosition = floatingActionButtonPosition,
    snackbarHost = snackbarHost,
    containerColor = containerColor,
    content = content,
  )
}
