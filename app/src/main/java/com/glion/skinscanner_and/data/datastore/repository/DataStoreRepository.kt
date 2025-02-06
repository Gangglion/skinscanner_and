package com.glion.skinscanner_and.data.datastore.repository

import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    val gender: Flow<String?>
    val age: Flow<Int?>
    suspend fun setGender(gender: String)
    suspend fun setAge(age: Int)
}