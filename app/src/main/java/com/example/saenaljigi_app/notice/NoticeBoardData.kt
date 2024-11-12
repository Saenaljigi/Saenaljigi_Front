package com.example.saenaljigi_app.notice

import com.google.gson.annotations.SerializedName

data class NoticeBoardData(
    val id: Long,
    @SerializedName("subject") val title: String,
    @SerializedName("regdate") val created_at: String,
    var content: String? = null,
    var writer: String? = null,
    var files: List<FileData>? = null // 파일 목록 필드 수정
)

data class FileData(
    val fileName: String,  // 파일 이름 필드 추가
    val fileUrl: String
)
