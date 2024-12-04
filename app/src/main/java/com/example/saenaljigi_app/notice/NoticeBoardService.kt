package com.example.saenaljigi_app.notice

import NoticeBoardData
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface NoticeBoardService {

    // 전체 글 조회
    @POST("/notice")
    fun getNotice(
        @Header("Authorization") token: String
    ): Call<List<NoticeBoardData>>

    // 특정 글 조회 예시
    @POST("/notice/{seq}")
    fun getNoticeSeq(
        @Header("Authorization") token: String,
        @Path("seq") seq: String
    ): Call<NoticeBoardData>


}
