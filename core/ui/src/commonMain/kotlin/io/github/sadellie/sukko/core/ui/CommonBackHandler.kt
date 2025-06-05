package io.github.sadellie.sukko.core.ui

import androidx.compose.runtime.Composable

@Composable expect fun BackHandler(enabled: Boolean, block: () -> Unit)
