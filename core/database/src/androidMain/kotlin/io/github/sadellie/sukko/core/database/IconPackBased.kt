package io.github.sadellie.sukko.core.database

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "icon_pack")
data class IconPackBased(
  @PrimaryKey(autoGenerate = true) @ColumnInfo("iconPackId") val iconPackId: Long,
  @ColumnInfo("name") val name: String,
)

@Dao
interface IconPackDao {
  @Query("SELECT * FROM icon_pack") fun getAll(): Flow<List<IconPackBased>>

  @Query("DELETE from icon_pack WHERE iconPackId = :iconPackId") fun delete(iconPackId: Long)

  @Query("UPDATE icon_pack SET name = :newName WHERE iconPackId = :iconPackId")
  fun rename(iconPackId: Long, newName: String)

  /**
   * Insert new [iconPackBased] into database.
   *
   * @return inserted row id
   */
  @Insert(onConflict = OnConflictStrategy.ABORT)
  suspend fun insertNew(iconPackBased: IconPackBased): Long
}
