package com.glion.skinscanner_and.data.tflite.reppository

import com.glion.skinscanner_and.data.tflite.data.AnalyzeResult
import kotlinx.coroutines.flow.Flow

interface TfliteRepository {
    suspend fun cancerAnalyze() : Flow<AnalyzeResult?>
}