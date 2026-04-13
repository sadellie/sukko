package io.github.sadellie.sukko.feature.settings.notificationlistener

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import google.material.design.symbols.Info
import google.material.design.symbols.Symbols
import io.github.sadellie.sukko.core.designsystem.theme.Sizes
import io.github.sadellie.sukko.core.ui.BigSwitch
import io.github.sadellie.sukko.core.ui.NavigateUpButton
import io.github.sadellie.sukko.core.ui.ScaffoldWithLargeTopAppBar
import io.github.sadellie.sukko.feature.settings.PRIVACY_POLICY_URL
import io.github.sadellie.sukko.feature.settings.isNotificationListenerEnabled
import io.github.sadellie.sukko.feature.settings.rememberLinkOpener
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.settings_notification_listener
import io.github.sadellie.sukko.resources.settings_notification_listener_description
import io.github.sadellie.sukko.resources.settings_notification_listener_footer
import io.github.sadellie.sukko.resources.settings_notification_listener_how_to_text
import io.github.sadellie.sukko.resources.settings_notification_listener_how_to_title
import io.github.sadellie.sukko.resources.settings_notification_listener_learn_more_from_faq
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun NotificationListenerScene(
  onNavigateUp: () -> Unit,
  openNotificationListenerPermission: () -> Unit,
) {
  NotificationListenerScreen(
    onNavigateUp = onNavigateUp,
    openNotificationListenerPermission = openNotificationListenerPermission,
    isNotificationListenerEnabled = isNotificationListenerEnabled().value,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationListenerScreen(
  onNavigateUp: () -> Unit,
  isNotificationListenerEnabled: Boolean,
  openNotificationListenerPermission: () -> Unit,
) {
  val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
  ScaffoldWithLargeTopAppBar(
    title = { Text(stringResource(Res.string.settings_notification_listener)) },
    navigationIcon = { NavigateUpButton(onNavigateUp) },
    scrollBehavior = scrollBehavior,
  ) { paddingValues ->
    Column(
      modifier =
        Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
          .verticalScroll(rememberScrollState())
          .padding(paddingValues)
          .padding(horizontal = Sizes.large),
      verticalArrangement = Arrangement.spacedBy(Sizes.large),
    ) {
      Text(
        text = stringResource(Res.string.settings_notification_listener_description),
        style = MaterialTheme.typography.bodyLarge,
      )

      BigSwitch(
        label = stringResource(Res.string.settings_notification_listener),
        checked = isNotificationListenerEnabled,
        onCheckedChange = openNotificationListenerPermission,
        modifier = Modifier.fillMaxWidth(),
      )
      AnimatedVisibility(
        visible = !isNotificationListenerEnabled,
        modifier = Modifier.fillMaxWidth(),
      ) {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(Sizes.small),
        ) {
          Text(
            text = stringResource(Res.string.settings_notification_listener_how_to_title),
            style = MaterialTheme.typography.labelMedium,
          )
          Text(
            text = stringResource(Res.string.settings_notification_listener_how_to_text),
            style = MaterialTheme.typography.bodyMedium,
          )
        }
      }

      Icon(
        imageVector = Symbols.Info,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.outline,
      )

      Text(
        text = stringResource(Res.string.settings_notification_listener_footer),
        color = MaterialTheme.colorScheme.outline,
      )

      val linkOpener = rememberLinkOpener()
      Text(
        text = stringResource(Res.string.settings_notification_listener_learn_more_from_faq),
        style = MaterialTheme.typography.bodyMedium.copy(textDecoration = TextDecoration.Underline),
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.clickable { linkOpener.launch(PRIVACY_POLICY_URL) },
      )
    }
  }
}

@Composable
@Preview
private fun PreviewNotificationListenerScreenDisabled() {
  NotificationListenerScreen(
    onNavigateUp = {},
    openNotificationListenerPermission = {},
    isNotificationListenerEnabled = false,
  )
}

@Composable
@Preview
private fun PreviewNotificationListenerScreenEnabled() {
  NotificationListenerScreen(
    onNavigateUp = {},
    openNotificationListenerPermission = {},
    isNotificationListenerEnabled = true,
  )
}
