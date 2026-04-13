package io.github.sadellie.sukko.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State

@Composable internal expect fun isNotificationListenerEnabled(): State<Boolean>

internal const val PRIVACY_POLICY_URL = "https://sadellie.github.io/sukko/privacy/"

interface LinkOpener {
  fun launch(url: String)
}

@Composable expect fun rememberLinkOpener(): LinkOpener
