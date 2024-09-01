package com.glion.skinscanner_and.util.network

import com.glion.skinscanner_and.util.response.ResponseKeyword
import com.glion.skinscanner_and.util.response.ResponseSearch
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    /**
     * 주소 검색하기
     */
    @GET("search/address")
    fun searchAddress(@Query("query") query: String): Call<ResponseSearch>

    /**
     * 키워드로 장소 검색하기
     * @param [query] 검색 키워드명
     * @param [category_group_code] 카테고리 그룹 코드 - 병원 HP8 고정
     * @param [x] 기준위치 X
     * @param [y] 기준위치 Y
     * @param [radius] 범위 - 3키로로 고정(단위 m)
     */
    @GET("search/keyword")
    fun searchKeyword(
        @Query("query") query: String,
        @Query("category_group_code") categoryCode: String,
        @Query("x") x: String,
        @Query("y") y: String,
        @Query("radius") radius: Int
    ): Call<ResponseKeyword>
}