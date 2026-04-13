package io.github.sadellie.sukko.feature.home

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey

fun EntryProviderScope<NavKey>.homeNavigation(onAddWidget: () -> Unit) {
  homeEntry(onAddWidget = onAddWidget)
}

internal expect fun EntryProviderScope<NavKey>.homeEntry(onAddWidget: () -> Unit)
