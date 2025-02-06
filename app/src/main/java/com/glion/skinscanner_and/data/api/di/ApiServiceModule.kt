package com.glion.skinscanner_and.data.api.di

import com.glion.skinscanner_and.BuildConfig
import com.glion.skinscanner_and.data.api.repository.NetworkDatasource
import com.glion.skinscanner_and.data.api.repository.NetworkDatasourceImpl
import com.glion.skinscanner_and.data.api.source.ApiService
import com.glion.skinscanner_and.util.Define
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(ViewModelComponent::class)
abstract class NetworkDataSourceModule {
    @Binds
    abstract fun bindsNetworkDatasource(
        networkDatasourceImpl: NetworkDatasourceImpl
    ) : NetworkDatasource
}

@Module
@InstallIn(SingletonComponent::class)
object ApiServiceModule {
    private val logInterceptor = HttpLoggingInterceptor(PrettyJsonLogger()).apply {
        level = if(BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }


    private fun provideOkHttpClient(interceptor: AppInterceptor): OkHttpClient = OkHttpClient.Builder().run {
        addInterceptor(interceptor)
        addInterceptor(logInterceptor)
        build()
    }

    @Provides
    fun provideApiService() : ApiService {
        return Retrofit.Builder()
            .baseUrl(Define.BASE_URL)
            .client(provideOkHttpClient(AppInterceptor()))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

