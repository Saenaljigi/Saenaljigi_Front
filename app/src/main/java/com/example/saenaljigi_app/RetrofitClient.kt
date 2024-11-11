package com.example.saenaljigi_app

import android.content.Context
import com.example.saenaljigi_app.login.LoginService
import com.example.saenaljigi_app.login.SejongApi
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val SEJONG_BASE_URL = "https://auth.imsejong.com"
    private const val USER_BASE_URL = "http://15.165.213.186:8080" // 사용자 API URL

    val sejongApi: SejongApi by lazy {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(SEJONG_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(SejongApi::class.java)
    }

    val userApi: LoginService by lazy {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(USER_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(LoginService::class.java)
    }

    private const val BASE_URL = "http://15.165.213.186:8080/"

    val instance: Retrofit by lazy {
        val gson: Gson = GsonBuilder()
            .create()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://15.165.213.186:8080/") // 서버의 base URL을 입력하세요
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
