package com.example.saenaljigi_app

data class UserDTO(
    val id: Long,            // 회원 고유번호
    val studentId: Int,      // 학번
    val mealCnt: Int,        // 식권 잔여 개수
    val name: String,        // 이름
    val reward: Int,         // 상점
    val penalty: Int,        // 벌점
    val totalCnt: Int        // 상점 - 벌점
)