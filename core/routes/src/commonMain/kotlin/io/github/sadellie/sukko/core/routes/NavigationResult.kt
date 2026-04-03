package io.github.sadellie.sukko.core.routes

sealed interface NavigationResult {
  data class PresetSelectorResult(val presetId: Long, val isBuiltIn: Boolean) : NavigationResult {
    companion object {
      const val KEY = "preset_selector_result"
    }
  }
}
