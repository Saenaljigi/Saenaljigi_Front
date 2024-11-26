package com.example.saenaljigi_app.login

data class UserResponse(
    val userId:Int,
    val username:String,
    val mealCnt:Int
)

data class UserRequest(
    val username: String,
    val password: String
)
