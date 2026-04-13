package io.github.sadellie.sukko.feature.editor

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import io.github.sadellie.sukko.core.routes.CommonRoute
import io.github.sadellie.sukko.core.routes.LocalNavigator
import io.github.sadellie.sukko.core.ui.ErrorScreenPlaceholder

internal actual fun EntryProviderScope<NavKey>.editorRoute() =
  entry<CommonRoute.EditorRoute> {
    ErrorScreenPlaceholder(onNavigateUp = LocalNavigator.current::goBack)
  }
