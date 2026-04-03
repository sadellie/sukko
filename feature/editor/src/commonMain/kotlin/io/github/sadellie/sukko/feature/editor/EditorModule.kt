package io.github.sadellie.sukko.feature.editor

import io.github.sadellie.sukko.core.fontfiles.FontFile
import io.github.sadellie.sukko.core.fontfiles.FontFileCustomRepository
import io.github.sadellie.sukko.feature.editor.selector.AppSelectorViewModel
import io.github.sadellie.sukko.feature.editor.selector.FontFileSelectorViewModel
import io.github.sadellie.sukko.feature.editor.selector.IconSelectorViewModel
import io.github.sadellie.sukko.feature.editor.selector.scripteditor.DocsViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf

internal fun Module.editorModule() {
  viewModelOf(::EditorViewModel)
  viewModelOf(::IconSelectorViewModel)
  viewModel<FontFileSelectorViewModel> { (initialValue: FontFile?) ->
    FontFileSelectorViewModel(
      initialValue = initialValue,
      fontFileCustomRepository = get<FontFileCustomRepository>(),
    )
  }
  viewModel<AppSelectorViewModel> { (packageName: String?) ->
    AppSelectorViewModel(packageName = packageName, installedAppsProvider = get())
  }
  viewModelOf(::DocsViewModel)
}
