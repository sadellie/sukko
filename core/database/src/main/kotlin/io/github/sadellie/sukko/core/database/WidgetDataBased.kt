package io.github.sadellie.sukko.core.database

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "widget_data")
data class WidgetDataBased(
  @PrimaryKey @ColumnInfo("appWidgetId") val appWidgetId: Int,
  @ColumnInfo("name") val name: String?,
  @ColumnInfo("layers") val layers: String,
  @ColumnInfo("globals") val globals: String,
)

@Dao
interface WidgetDataDao {
  @Query("SELECT * FROM widget_data") fun getAll(): Flow<List<WidgetDataBased>>

  @Query("SELECT * FROM widget_data WHERE appWidgetId = :appWidgetId LIMIT 1")
  suspend fun getById(appWidgetId: Int): WidgetDataBased?

  @Upsert suspend fun save(widgetDataBased: WidgetDataBased)

  @Query("UPDATE widget_data SET name = :newName WHERE appWidgetId = :appWidgetId")
  suspend fun rename(appWidgetId: Int, newName: String)

  @Query("DELETE FROM widget_data  WHERE appWidgetId = :appWidgetId")
  suspend fun deleteById(appWidgetId: Int)
}
