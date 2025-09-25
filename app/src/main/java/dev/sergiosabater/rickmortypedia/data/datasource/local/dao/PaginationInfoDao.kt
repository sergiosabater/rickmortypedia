package dev.sergiosabater.rickmortypedia.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.sergiosabater.rickmortypedia.data.datasource.local.entity.PaginationInfoEntity

@Dao
interface PaginationInfoDao {

    // Insert or update pagination information
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaginationInfo(info: PaginationInfoEntity)

    // Get the pagination information
    @Query("SELECT * FROM pagination_info WHERE id = 1")
    suspend fun getPaginationInfo(): PaginationInfoEntity?

    // Check if we have cached data
    @Query("SELECT COUNT(*) FROM pagination_info WHERE id = 1")
    suspend fun hasCachedData(): Int

    // Remove pagination information
    @Query("DELETE FROM pagination_info")
    suspend fun deletePaginationInfo()

    // Update timestamp of last synchronization
    @Query("UPDATE pagination_info SET lastSyncTimestamp = :timestamp WHERE id = 1")
    suspend fun updateLastSyncTimestamp(timestamp: Long)
}