package io.github.sadellie.sukko.core.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

@Composable
actual fun BackHandler(enabled: Boolean, block: () -> Unit) = BackHandler(enabled, block)
