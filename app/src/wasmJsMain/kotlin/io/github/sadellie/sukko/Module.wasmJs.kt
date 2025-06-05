package io.github.sadellie.sukko

import io.github.sadellie.sukko.core.common.notReady
import io.github.sadellie.sukko.core.data.IconPackCustomRepository
import io.github.sadellie.sukko.core.data.InstalledAppsProvider
import io.github.sadellie.sukko.core.data.WidgetDataPresetCustomRepository
import io.github.sadellie.sukko.core.data.WidgetDataRepository
import io.github.sadellie.sukko.core.widget.WidgetInfoRepository
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module

actual fun Module.widgetInfoRepository(): KoinDefinition<WidgetInfoRepository> = notReady

actual fun Module.widgetDataRepository(): KoinDefinition<WidgetDataRepository> = notReady

actual fun Module.widgetDataPresetRepository(): KoinDefinition<WidgetDataPresetCustomRepository> =
  notReady

actual fun Module.iconPackRepository(): KoinDefinition<IconPackCustomRepository> = notReady

actual fun Module.installedAppsRepository(): KoinDefinition<InstalledAppsProvider> = notReady
