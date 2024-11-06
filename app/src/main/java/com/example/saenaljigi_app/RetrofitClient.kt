package com.example.saenaljigi_app

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val USER_BASE_URL = "http://15.165.213.186:8080"

    private fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            // HTTP 요청과 응답을 로깅하는 인터셉터 추가
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(USER_BASE_URL)
            .client(provideOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


}
