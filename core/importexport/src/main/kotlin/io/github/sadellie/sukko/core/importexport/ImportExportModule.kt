package io.github.sadellie.sukko.core.importexport

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.lazyModule

val importExportModule = lazyModule {
  factoryOf(::WidgetDataPresetExportImport)
}
