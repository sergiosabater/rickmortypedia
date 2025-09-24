package dev.sergiosabater.rickmortypedia.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.sergiosabater.rickmortypedia.domain.usecase.GetCharacterByIdUseCase
import dev.sergiosabater.rickmortypedia.domain.usecase.GetCharactersUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideGetCharactersUseCase(
        getCharactersUseCase: GetCharactersUseCase
    ): GetCharactersUseCase = getCharactersUseCase

    @Provides
    @Singleton
    fun provideGetCharacterByIdUseCase(
        getCharacterByIdUseCase: GetCharacterByIdUseCase
    ): GetCharacterByIdUseCase = getCharacterByIdUseCase
}