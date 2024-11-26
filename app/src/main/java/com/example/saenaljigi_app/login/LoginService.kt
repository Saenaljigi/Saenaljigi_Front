package com.example.saenaljigi_app.login

import com.example.saenaljigi_app.UserDTO
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface LoginService {

    // 애플리케이션 JWT 토큰 요청 API
    @POST("/login")
    fun requestJwtToken(@Body userRequest: UserRequest): Call<UserResponse>

    // 애플리케이션 로그아웃 API
    @GET("/logout")
    fun logout(): Call<ResponseBody>
}

