package io.github.sadellie.sukko.core.database

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "widget_data_preset")
data class WidgetDataPresetBased(
  @PrimaryKey(autoGenerate = true) @ColumnInfo("presetId") val presetId: Long,
  @ColumnInfo("name") val name: String,
  @ColumnInfo("layers") val layers: String,
  @ColumnInfo("globals") val globals: String,
)

@Dao
interface WidgetDataPresetDao {
  @Query("SELECT * FROM widget_data_preset") fun getAll(): Flow<List<WidgetDataPresetBased>>

  @Query("SELECT * FROM widget_data_preset WHERE presetId = :presetId LIMIT 1")
  suspend fun getById(presetId: Long): WidgetDataPresetBased?

  @Insert(onConflict = OnConflictStrategy.ABORT)
  suspend fun insertNew(widgetDataPresetBased: WidgetDataPresetBased): Long

  @Query("UPDATE widget_data_preset SET name = :newName WHERE presetId = :presetId")
  suspend fun rename(presetId: Long, newName: String)

  @Query("DELETE FROM widget_data_preset  WHERE presetId = :presetId")
  suspend fun deleteById(presetId: Long)
}
