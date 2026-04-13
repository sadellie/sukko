package io.github.sadellie.sukko.core.data

import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Provides

@BindingContainer
actual class DataBindings {
  @Provides fun provideWidgetDataRepository(): WidgetDataRepository = WidgetDataRepositoryImpl()

  @Provides fun provideWidgetInfoRepository(): WidgetInfoRepository = WidgetInfoRepositoryImpl()
}
