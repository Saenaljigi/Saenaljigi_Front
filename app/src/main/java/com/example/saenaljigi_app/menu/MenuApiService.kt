package com.example.saenaljigi_app.menu

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface MenuApiService {

    @GET("/calendar/day")
    fun getMenu ( // 메뉴 조회
        @Header("Authorization") token: String,
        @Query("day") day: String
    ): Call<CalendarDto>

    @GET("/calendar")
    fun getAllDay ( // 하이라이트된 날 조회 (모든 날짜 요청 받아 분류해야 함)
        @Header("Authorization") token: String
    ): Call<List<CalendarDto>>

    @PUT("/select")
    fun updateHighlightedMenu( // 메뉴 하이라이트 업데이트
        @Header("Authorization") token: String,
        @Query("menuId") menuId: Long,
        @Query("isSelected") isSelected: Boolean,
        @Query("foodName") foodName: String
    ): Call<Void>

    @POST("/calendar/{calendarId}/breakfast")
    fun applyForBreakfast( // 조식 신청
        @Header("Authorization") token: String,
        @Path("calendarId") calendarId: Long,
        @Query("isBreakfast") isBreakfast: Boolean
    ): Call<Void>

}