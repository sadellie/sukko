package io.github.sadellie.sukko.feature.fontseditor

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import io.github.sadellie.sukko.core.routes.CommonRoute
import io.github.sadellie.sukko.core.routes.LocalNavigator

fun EntryProviderScope<NavKey>.fontsEditorNavigation() =
  entry<CommonRoute.FontFilesEditorRoute> {
    FontsEditorScene(onNavigateUp = LocalNavigator.current::goBack)
  }
