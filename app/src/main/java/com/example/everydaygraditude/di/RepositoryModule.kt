package com.example.everydaygraditude.di

import com.example.everydaygraditude.repositories.repository.GratitudeNotesRepository
import com.example.everydaygraditude.repositories.repositoryImplementation.GratitudeNotesRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindGratitudeRepository(impl: GratitudeNotesRepositoryImpl) : GratitudeNotesRepository

}