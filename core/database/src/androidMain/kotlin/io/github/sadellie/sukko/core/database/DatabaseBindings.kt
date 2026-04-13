package io.github.sadellie.sukko.core.database

import android.content.Context
import androidx.room.Room
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import io.github.sadellie.sukko.core.common.defaultIODispatcher

@BindingContainer
class DatabaseBindings {
  @SingleIn(AppScope::class)
  @Provides
  fun provideDatabase(context: Context): SukkoDatabase =
    Room.databaseBuilder<SukkoDatabase>(context, SukkoDatabase.DATABASE_NAME)
      .fallbackToDestructiveMigration(false)
      .fallbackToDestructiveMigrationOnDowngrade(true)
      .setQueryCoroutineContext(defaultIODispatcher)
      .build()

  @Provides
  fun provideWidgetDataDao(database: SukkoDatabase): WidgetDataDao = database.widgetDataDao()

  @Provides
  fun provideWidgetDataPresetDao(database: SukkoDatabase): WidgetDataPresetDao =
    database.widgetDataPresetDao()

  @Provides fun provideIconPackDao(database: SukkoDatabase): IconPackDao = database.iconPackDao()
}
