package io.github.sadellie.sukko.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import io.github.sadellie.sukko.core.designsystem.theme.ListArrangement
import io.github.sadellie.sukko.core.designsystem.theme.Sizes
import io.github.sadellie.sukko.core.ui.ListItem2
import io.github.sadellie.sukko.core.ui.NavigateUpButton
import io.github.sadellie.sukko.core.ui.ScaffoldWithLargeTopAppBar
import io.github.sadellie.sukko.core.ui.firstShape
import io.github.sadellie.sukko.core.ui.lastShape
import io.github.sadellie.sukko.core.ui.singleShape
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.common_disabled
import io.github.sadellie.sukko.resources.common_enabled
import io.github.sadellie.sukko.resources.fonts_editor_title
import io.github.sadellie.sukko.resources.icon_packs_editor_title
import io.github.sadellie.sukko.resources.settings_notification_listener
import io.github.sadellie.sukko.resources.settings_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SettingsScene(
  onNavigateUp: () -> Unit,
  onNavigateToIconPackEditor: () -> Unit,
  onNavigateToFontFilesEditor: () -> Unit,
  navigateToNotificationListener: () -> Unit,
) {
  SettingsScreen(
    onNavigateUp = onNavigateUp,
    onNavigateToIconPackEditor = onNavigateToIconPackEditor,
    onNavigateToFontFilesEditor = onNavigateToFontFilesEditor,
    navigateToNotificationListener = navigateToNotificationListener,
    isNotificationListenerEnabled = isNotificationListenerEnabled().value,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreen(
  onNavigateUp: () -> Unit,
  onNavigateToIconPackEditor: () -> Unit,
  onNavigateToFontFilesEditor: () -> Unit,
  navigateToNotificationListener: () -> Unit,
  isNotificationListenerEnabled: Boolean,
) {
  val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
  ScaffoldWithLargeTopAppBar(
    title = { Text(stringResource(Res.string.settings_title)) },
    navigationIcon = { NavigateUpButton(onNavigateUp) },
    scrollBehavior = scrollBehavior,
  ) { paddingValues ->
    Column(
      modifier =
        Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
          .verticalScroll(rememberScrollState())
          .padding(paddingValues)
          .padding(horizontal = Sizes.large),
      verticalArrangement = Arrangement.spacedBy(Sizes.small),
    ) {
      Column(modifier = Modifier, verticalArrangement = ListArrangement) {
        ListItem2(
          headlineContent = { Text(stringResource(Res.string.icon_packs_editor_title)) },
          modifier = Modifier.clickable(onClick = onNavigateToIconPackEditor),
          shape = ListItemDefaults.firstShape,
        )

        ListItem2(
          headlineContent = { Text(stringResource(Res.string.fonts_editor_title)) },
          modifier = Modifier.clickable(onClick = onNavigateToFontFilesEditor),
          shape = ListItemDefaults.lastShape,
        )
      }

      ListItem2(
        headlineContent = { Text(stringResource(Res.string.settings_notification_listener)) },
        supportingContent = {
          Text(
            stringResource(
              if (isNotificationListenerEnabled) Res.string.common_enabled
              else Res.string.common_disabled
            )
          )
        },
        modifier = Modifier.clickable(onClick = navigateToNotificationListener),
        shape = ListItemDefaults.singleShape,
      )
    }
  }
}

@Composable
@Preview
private fun PreviewSettingsScene() {
  SettingsScreen(
    onNavigateUp = {},
    onNavigateToIconPackEditor = {},
    onNavigateToFontFilesEditor = {},
    navigateToNotificationListener = {},
    isNotificationListenerEnabled = true,
  )
}
