package com.glion.skinscanner_and.data.api.source

import com.glion.skinscanner_and.data.api.data.ResponseKeyword
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    /**
     * 카카오맵 API - 현재위치에서 키워드로 찾기
     */
    @GET("search/keyword")
    suspend fun searchKeyword(
        @Query("query") query: String,
        @Query("category_group_code") categoryCode: String,
        @Query("x") x: String,
        @Query("y") y: String,
        @Query("radius") radius: Int,
        @Query("page") page: Int
    ): ResponseKeyword
}