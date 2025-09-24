package dev.sergiosabater.rickmortypedia.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.sergiosabater.rickmortypedia.data.mapper.CharacterMapper
import dev.sergiosabater.rickmortypedia.data.repository.CharacterRepository
import dev.sergiosabater.rickmortypedia.data.repository.CharacterRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCharacterRepository(
        characterRepositoryImpl: CharacterRepositoryImpl
    ): CharacterRepository

    @Singleton
    fun provideCharacterMapper(): CharacterMapper {
        return CharacterMapper
    }

}