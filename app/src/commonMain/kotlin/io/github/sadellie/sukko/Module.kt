package io.github.sadellie.sukko

import io.github.sadellie.sukko.core.data.IconPackCustomRepository
import io.github.sadellie.sukko.core.data.InstalledAppsProvider
import io.github.sadellie.sukko.core.data.WidgetDataPresetCustomRepository
import io.github.sadellie.sukko.core.data.WidgetDataRepository
import io.github.sadellie.sukko.core.widget.WidgetInfoRepository
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module

expect fun Module.widgetInfoRepository(): KoinDefinition<WidgetInfoRepository>

expect fun Module.widgetDataRepository(): KoinDefinition<WidgetDataRepository>

expect fun Module.widgetDataPresetRepository(): KoinDefinition<WidgetDataPresetCustomRepository>

expect fun Module.iconPackRepository(): KoinDefinition<IconPackCustomRepository>

expect fun Module.installedAppsRepository(): KoinDefinition<InstalledAppsProvider>
