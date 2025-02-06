package com.glion.skinscanner_and.data.api.di

import com.glion.skinscanner_and.util.Define
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class AppInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response = with(chain) {
        val newRequest = request().newBuilder()
            .addHeader("Authorization", "KakaoAK ${Define.KAKAO_REST_KEY}")
            .build()
        proceed(newRequest)
    }
}