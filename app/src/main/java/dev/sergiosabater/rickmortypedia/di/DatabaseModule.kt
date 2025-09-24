package dev.sergiosabater.rickmortypedia.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.sergiosabater.rickmortypedia.data.datasource.local.AppDatabase
import dev.sergiosabater.rickmortypedia.data.datasource.local.dao.CharacterDao
import dev.sergiosabater.rickmortypedia.data.datasource.local.dao.PaginationInfoDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    fun provideCharacterDao(appDatabase: AppDatabase): CharacterDao = appDatabase.characterDao()

    @Provides
    fun providePaginationInfoDao(appDatabase: AppDatabase): PaginationInfoDao =
        appDatabase.paginationInfoDao()
}