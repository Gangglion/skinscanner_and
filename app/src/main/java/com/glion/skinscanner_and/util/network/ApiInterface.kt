package com.glion.skinscanner_and.util.network

import com.glion.skinscanner_and.util.response.ResponseKeyword
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("search/keyword")
    fun searchKeyword(
        @Query("query") query: String,
        @Query("category_group_code") categoryCode: String,
        @Query("x") x: String,
        @Query("y") y: String,
        @Query("radius") radius: Int,
        @Query("page") page: Int
    ): Call<ResponseKeyword>
}