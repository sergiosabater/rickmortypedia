package dev.sergiosabater.rickmortypedia.data.datasource.local

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.sergiosabater.rickmortypedia.data.datasource.local.dao.CharacterDao
import dev.sergiosabater.rickmortypedia.data.datasource.local.dao.PaginationInfoDao
import dev.sergiosabater.rickmortypedia.data.datasource.local.entity.CharacterEntity
import dev.sergiosabater.rickmortypedia.data.datasource.local.entity.PaginationInfoEntity

@Database(
    entities = [
        CharacterEntity::class,
        PaginationInfoEntity::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun characterDao(): CharacterDao
    abstract fun paginationInfoDao(): PaginationInfoDao

    companion object {
        const val DATABASE_NAME = "rick_and_morty.db"
    }
}