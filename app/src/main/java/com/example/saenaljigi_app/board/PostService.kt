package com.example.saenaljigi_app.board

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface PostService {
    @POST("/post")
    fun createPost(
        @Header("Authorization") token: String,
        @Query("userId") userId: Int,
        @Query("title") title: String,
        @Query("content") content: String,
        @Query("isAnonymous") isAnonymous: Boolean
    ): Call<ResponseBody>

    @GET("/post")
    fun getAllPosts(
        @Header("Authorization") token: String
    ): Call<List<PostClass>>
}
