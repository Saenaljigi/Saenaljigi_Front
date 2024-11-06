package com.example.saenaljigi_app.notice

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header

interface NoticeBoardService {

    // 전체 글 조회
    @GET("/notice")
    fun getNotice(
        @Header("Authorization") token: String
    ): Call<List<NoticeBoardData>>

    // 특정 글 조회 예시
    @GET("/notice/{seq}")
    fun getNoticeSeq(
        @Header("Authorization") token: String
    ): Call<NoticeBoardData>


}
