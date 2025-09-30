package dev.sergiosabater.rickmortypedia.di

import coil.ImageLoader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.sergiosabater.rickmortypedia.MyApplication
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoilModule {

    @Provides
    @Singleton
    fun provideImageLoader(application: MyApplication): ImageLoader {
        return application.newImageLoader()
    }
}