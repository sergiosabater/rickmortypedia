package dev.sergiosabater.rickmortypedia.data.datasource.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pagination_info")
data class PaginationInfoEntity(
    @PrimaryKey val id: Int = 1,
    val totalPages: Int,
    val totalCharacters: Int,
    val lastSyncTimestamp: Long
)