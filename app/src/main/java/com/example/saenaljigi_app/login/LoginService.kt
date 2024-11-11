package com.example.saenaljigi_app.login

import com.example.saenaljigi_app.UserDTO
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface SejongApi {
    // 세종대학교 기숙사 로그인 API
    @POST("/auth?method=ClassicSession")
    fun login(@Body body: Map<String, String>): Call<SejongAuthResponse>
}
interface LoginService {

    // 애플리케이션 JWT 토큰 요청 API
    @POST("/users/login")
    fun requestJwtToken(@Body userDTO: UserDTO): Call<JwtResponse>

    // 애플리케이션 로그아웃 API
    @GET("/users/logout")
    fun logout(): Call<ResponseBody>
}