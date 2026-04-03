package io.github.sadellie.sukko.core.database

import androidx.room.Room
import io.github.sadellie.sukko.core.common.defaultIODispatcher
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.lazyModule

val databaseModule = lazyModule {
  single {
    Room.databaseBuilder<SukkoDatabase>(androidContext(), SukkoDatabase.DATABASE_NAME)
      .fallbackToDestructiveMigration(false)
      .fallbackToDestructiveMigrationOnDowngrade(true)
      .setQueryCoroutineContext(defaultIODispatcher)
      .build()
  }
  factory<WidgetDataDao> { get<SukkoDatabase>().widgetDataDao() }
  factory<WidgetDataPresetDao> { get<SukkoDatabase>().widgetDataPresetDao() }
  factory<IconPackDao> { get<SukkoDatabase>().iconPackDao() }
}
