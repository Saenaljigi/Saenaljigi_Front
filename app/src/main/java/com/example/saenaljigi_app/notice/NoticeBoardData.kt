package com.example.saenaljigi_app.notice

data class NoticeBoardData(
    val id: Long,
    val title: String,
    val created_at: String,
    var content: String? = null,
    var writer: String? = null,
    var file: List<fileData>? = null
)

data class fileData(
    val fileUrl: String
)
