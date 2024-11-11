package com.example.saenaljigi_app.login

data class SejongAuthResponse(
    val result: SejongAuthResult
)

data class SejongAuthResult(
    val isAuth: String, // "true" 또는 "false"로 인증 성공 여부 표시
    val body: SejongAuthResponseResultBodyJson
)

data class SejongAuthResponseResultBodyJson(
    val name: String,
    val studentId: Int,
)
