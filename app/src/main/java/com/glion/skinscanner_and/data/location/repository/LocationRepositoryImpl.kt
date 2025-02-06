package com.glion.skinscanner_and.data.location.repository

import android.annotation.SuppressLint
import android.location.Location
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

@SuppressLint("MissingPermission")
class LocationRepositoryImpl @Inject constructor(
    private val locationClient: FusedLocationProviderClient
) : LocationRepository {
    override suspend fun getLastLocation(): Flow<Location?> = callbackFlow {
        locationClient.lastLocation.addOnSuccessListener { location ->
            if(location != null) {
                trySend(location)
            } else {
                trySend(null)
            }
        }
        awaitClose {  }
    }

    override suspend fun getCurrentLocation(): Flow<Result<Location>> = callbackFlow {
        locationClient.getCurrentLocation(createCurrentLocationRequest(), createCancellationToken())
            .addOnSuccessListener { location ->
                trySend(Result.success(location))
            }
            .addOnFailureListener { exception ->
                trySend(Result.failure(exception))
            }
        awaitClose {  }
    }

    /**
     * 현재 위치 가져올 수 있는 요청 Builder 생성 반환
     */
    private fun createCurrentLocationRequest() =
        CurrentLocationRequest.Builder()
            .setDurationMillis(10000)
            .setMaxUpdateAgeMillis(10000)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build()

    /**
     * 현재 위치 가져오기 실패했을때의 토큰 반환
     */
    private fun createCancellationToken() : CancellationToken = object : CancellationToken() {
        override fun onCanceledRequested(p0: OnTokenCanceledListener): CancellationToken {
            return CancellationTokenSource().token
        }

        override fun isCancellationRequested(): Boolean {
            return false
        }
    }
}