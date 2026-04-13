package io.github.sadellie.sukko.feature.importpreset

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import io.github.sadellie.sukko.core.routes.CommonRoute
import io.github.sadellie.sukko.core.routes.LocalNavigator

fun EntryProviderScope<NavKey>.importPresetNavigation() {
  entry<CommonRoute.ImportPresetRoute> {
    ImportPresetScene(
      navigateUp = LocalNavigator.current::goBack,
      importingPresetUri = it.selectedFileUri,
    )
  }
}
