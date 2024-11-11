package com.example.saenaljigi_app.menu

data class Menu(
    val id: Long,
    val foodTime: String,
    val isCheck: Boolean,
    val foods: List<Food>,
    val calendarId: Long
)

data class Food(
    val foodName: String,
    val isSelected: Boolean,
    val menuId: Long
)
