package com.example.saenaljigi_app.UserDto

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface MenuApiService {

    @GET("/calendar/{day}")
    fun getMenu ( // 메뉴 조회
        @Path("day") day: String
    ): Call<CalendarDto>

    @GET("/calendar")
    fun getAllDay ( // 하이라이트된 날 조회 (모든 날짜 요청 받아 분류해야 함)
    ): Call<List<CalendarDto>>

    @PUT("/food/{foodId}/select")
    fun updateHighlightedMenu(
        @Path("foodId") foodId: String,
        @Query("isSelected") isSelected: Boolean
    ): Call<Void>

}