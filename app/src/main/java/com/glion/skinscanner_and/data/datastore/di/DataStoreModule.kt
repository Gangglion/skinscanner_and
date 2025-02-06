package com.glion.skinscanner_and.data.datastore.di

import com.glion.skinscanner_and.data.datastore.repository.DataStoreRepository
import com.glion.skinscanner_and.data.datastore.repository.DataStoreRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataStoreModule {
    @Binds
    abstract fun bindsDataStoreRepository(
        dataStoreRepositoryImpl: DataStoreRepositoryImpl
    ) : DataStoreRepository
}