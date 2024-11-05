package com.example.saenaljigi_app.notice

data class NoticeBoardData(
    val id: Long,
    val title: String,
    val content: String,
    val writer: String,
    val created_at:String,
    val file: List<fileData>

)

data class fileData(
    val fileUrl: String
)
