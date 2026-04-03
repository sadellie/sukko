package io.github.sadellie.sukko.core.routes

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface CommonRoute : NavKey {
  @Serializable data class EditorRoute(val appWidgetId: Int) : CommonRoute

  @Serializable data object PresetSelectorRoute : CommonRoute

  @Serializable data class SaveAsPresetRoute(val appWidgetId: Int) : CommonRoute

  @Serializable data object FontFilesEditorRoute : CommonRoute

  @Serializable data object HomeRoute : CommonRoute

  @Serializable data class WidgetInfoRoute(val appWidgetId: Int) : CommonRoute

  @Serializable data object SettingsRoute : CommonRoute

  @Serializable data class ImportPresetRoute(val selectedFileUri: String) : CommonRoute

  @Serializable data object IconPacksListEditorRoute : CommonRoute
}
