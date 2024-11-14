package com.example.saenaljigi_app.menu

import android.view.Menu
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import java.time.LocalDate

interface MenuApiService {

    @GET("menu/{day}")
    fun getMenu ( // 메뉴 조회
        @Path("day") day: String
    ): Call<CalendarDto>

}