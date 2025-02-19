package com.glion.skinscanner_and.data.api.source

import com.glion.skinscanner_and.data.api.data.RequestCheckFile
import com.glion.skinscanner_and.data.api.data.RequestExchangeKey
import com.glion.skinscanner_and.data.api.data.ResponseCheckFile
import com.glion.skinscanner_and.data.api.data.ResponseExchangeKey
import retrofit2.http.Body
import retrofit2.http.POST

interface MyApiService {
    /**
     * 키 교환 API
     */
    @POST("/exchangeKey")
    suspend fun exchangeKey(@Body requestExchangeKey: RequestExchangeKey) : ResponseExchangeKey

    /**
     * 파일 확인
     */
    @POST("/checkFile")
    suspend fun checkFile(@Body requestCheckFile: RequestCheckFile) : ResponseCheckFile
}