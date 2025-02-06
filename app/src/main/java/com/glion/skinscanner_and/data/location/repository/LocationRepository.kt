package com.glion.skinscanner_and.data.location.repository

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    suspend fun getLastLocation() : Flow<Location?>
    suspend fun getCurrentLocation() : Flow<Result<Location>>
}