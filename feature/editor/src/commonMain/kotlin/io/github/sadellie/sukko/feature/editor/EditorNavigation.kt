package io.github.sadellie.sukko.feature.editor

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey

fun EntryProviderScope<NavKey>.editorNavigation() {
  editorRoute()
}

internal expect fun EntryProviderScope<NavKey>.editorRoute()
