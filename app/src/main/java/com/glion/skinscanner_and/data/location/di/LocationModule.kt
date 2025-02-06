package com.glion.skinscanner_and.data.location.di

import android.content.Context
import com.glion.skinscanner_and.data.location.repository.LocationRepository
import com.glion.skinscanner_and.data.location.repository.LocationRepositoryImpl
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object LocationModule {
    @Provides
    fun provideLocationClient(
        @ApplicationContext context: Context
    ) : FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class LocationRepositoryModule {
    @Binds
    abstract fun bindsLocationRepository(
        locationRepositoryImpl: LocationRepositoryImpl
    ): LocationRepository
}