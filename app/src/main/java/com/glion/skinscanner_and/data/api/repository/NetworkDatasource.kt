package com.glion.skinscanner_and.data.api.repository

import com.glion.skinscanner_and.data.api.data.DermatologyData
import kotlinx.coroutines.flow.Flow

interface NetworkDatasource {

    suspend fun getVersion() : Flow<Int?>

    suspend fun getDermatologyList(
        query: String = "피부과",
        categoryGroupCode: String = "HP8",
        x: String,
        y: String,
        radius: Int = 3000,
        page: Int
    ): Flow<DermatologyData>
}