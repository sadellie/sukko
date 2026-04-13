package io.github.sadellie.sukko.feature.widgetinfo

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import io.github.sadellie.sukko.core.common.collectAsStateWithLifecycleKMP
import io.github.sadellie.sukko.core.designsystem.theme.ListArrangement
import io.github.sadellie.sukko.core.designsystem.theme.Sizes
import io.github.sadellie.sukko.core.ui.ListHeader
import io.github.sadellie.sukko.core.ui.ListItem2
import io.github.sadellie.sukko.core.ui.LoadingScaffoldWithTopAppBar
import io.github.sadellie.sukko.core.ui.NavigateUpButton
import io.github.sadellie.sukko.core.ui.ScaffoldWithLargeTopAppBar
import io.github.sadellie.sukko.core.ui.listedShapes
import io.github.sadellie.sukko.core.ui.singleShapes
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.common_widget_info
import io.github.sadellie.sukko.resources.common_widget_name
import io.github.sadellie.sukko.resources.common_widget_name_placeholder
import io.github.sadellie.sukko.resources.widget_info_subscriptions
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun WidgetInfoScene(onNavigateUp: () -> Unit, appWidgetId: Int) {
  val viewModel =
    assistedMetroViewModel<WidgetInfoViewModel, WidgetInfoViewModel.Factory> { create(appWidgetId) }
  val uiState = viewModel.uiState.collectAsStateWithLifecycleKMP().value
  if (uiState == null) {
    LoadingScaffoldWithTopAppBar(onNavigateUp = onNavigateUp, disableBack = false)
  } else {
    WidgetInfoScreen(onNavigateUp = onNavigateUp, uiState = uiState)
  }
}

@Composable
private fun WidgetInfoScreen(onNavigateUp: () -> Unit, uiState: WidgetInfoUIState) {
  val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
  ScaffoldWithLargeTopAppBar(
    title = { Text(stringResource(Res.string.common_widget_info)) },
    navigationIcon = { NavigateUpButton(onNavigateUp) },
    scrollBehavior = scrollBehavior,
  ) { paddingValues ->
    LazyColumn(
      modifier =
        Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
          .padding(horizontal = Sizes.large),
      contentPadding = paddingValues,
      verticalArrangement = ListArrangement,
    ) {
      item("name") {
        ListItem2(
          onClick = {},
          shapes = ListItemDefaults.singleShapes,
          content = { Text(stringResource(Res.string.common_widget_name)) },
          supportingContent = {
            Text(
              uiState.name
                ?: stringResource(Res.string.common_widget_name_placeholder, uiState.appWidgetId)
            )
          },
        )
      }
      item("subscriptions_header") {
        ListHeader(stringResource(Res.string.widget_info_subscriptions))
      }
      itemsIndexed(uiState.widgetSubscriptions) { index, subscription ->
        ListItem2(
          onClick = {},
          shapes = ListItemDefaults.listedShapes(index, uiState.widgetSubscriptions.size),
          content = { Text(stringResource(subscription.name)) },
          supportingContent = { Text(stringResource(subscription.description)) },
        )
      }
    }
  }
}

@Preview
@Composable
private fun PreviewWidgetInfoScreen() {
  WidgetInfoScreen(
    uiState =
      remember {
        WidgetInfoUIState(
          name = null,
          appWidgetId = 3,
          widgetSubscriptions = listOf(Subscription.Time, Subscription.Battery, Subscription.Media),
        )
      },
    onNavigateUp = {},
  )
}
