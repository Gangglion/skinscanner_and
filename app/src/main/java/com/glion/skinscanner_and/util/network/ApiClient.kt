package com.glion.skinscanner_and.util.network

import com.glion.skinscanner_and.BuildConfig
import com.glion.skinscanner_and.common.Define
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.internal.platform.Platform
import okhttp3.internal.platform.Platform.Companion.INFO
import okhttp3.internal.platform.Platform.Companion.WARN
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

object ApiClient {
    class AppInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response = with(chain) {
            val newRequest = request().newBuilder()
                .addHeader("Authorization", "KakaoAK ${Define.KAKAO_REST_KEY}")
                .build()
            proceed(newRequest)
        }
    }

    class PrettyJsonLogger: HttpLoggingInterceptor.Logger {
        private val gson = GsonBuilder().setPrettyPrinting().create()
        private val jsonParser = JsonParser()
        override fun log(message: String) {
            val trimMessage = message.trim()

            if ((trimMessage.startsWith("{") && trimMessage.endsWith("}"))
                || (trimMessage.startsWith("[") && trimMessage.endsWith("]"))) {
                try {
                    val prettyJson = gson.toJson(jsonParser.parse(message))
                    Platform.get().log(prettyJson, INFO,null)
                } catch (e: Exception) {
                    Platform.get().log(message, WARN, e)
                }
            } else {
                Platform.get().log(message, INFO, null)
            }
        }

    }

    private val logInterceptor = HttpLoggingInterceptor(PrettyJsonLogger()).apply {
        level = if(BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    private fun getApiClient(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Define.BASE_URL)
            .client(provideOkHttpClient(AppInterceptor()))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun provideOkHttpClient(interceptor: AppInterceptor): OkHttpClient = OkHttpClient.Builder().run {
        addInterceptor(interceptor)
        addInterceptor(logInterceptor)
        build()
    }

    val api: ApiInterface = getApiClient().create(ApiInterface::class.java)
}