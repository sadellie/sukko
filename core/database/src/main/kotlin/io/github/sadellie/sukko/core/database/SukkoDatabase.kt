package io.github.sadellie.sukko.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import io.github.sadellie.sukko.core.common.SCHEMA_VERSION

@Database(
  version = SCHEMA_VERSION,
  exportSchema = true,
  entities = [WidgetDataBased::class, WidgetDataPresetBased::class, IconPackBased::class],
)
abstract class SukkoDatabase : RoomDatabase() {
  abstract fun widgetDataDao(): WidgetDataDao

  abstract fun widgetDataPresetDao(): WidgetDataPresetDao

  abstract fun iconPackDao(): IconPackDao

  companion object {
    /** Do NOT change */
    const val DATABASE_NAME = "sukko.db"
  }
}
