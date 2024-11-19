package com.example.saenaljigi_app.menu

import java.time.LocalDate

data class MenuDto(
    val id: Long,
    val foodTime: String,
    val isCheck: Boolean,
    val foods: List<FoodDto>,
    val calendarId: Long
)

data class FoodDto(
    val foodName: String,
    val isSelected: Boolean,
    val menuId: Long
)

data class CalendarDto(
    val id: Long,
    val day: LocalDate,
    val isHilight: Boolean,
    val isBreakfast: Boolean,
    val menus: List<MenuDto>
)
