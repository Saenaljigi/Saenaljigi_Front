package com.example.saenaljigi_app.menu

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import java.time.LocalDateTime

interface MenuApiService {
    @GET("menu/{day}/{foodtime}")
    fun getMenu ( // 메뉴 조회
        @Path("day") day: LocalDateTime,
        @Path("foodtime") foodtime: String
    ): Call<Menu>

}