package com.example.saenaljigi_app.menu

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import java.time.LocalDateTime

interface MenuApiService {

    @GET("menu/{day}/{foodtime}")
    fun getMenu ( // 메뉴 조회
        @Path("day") day: LocalDateTime,
        @Path("foodtime") foodtime: String
    ): Call<Menu>


}

//@POST("chats")
//fun chatRoom( // 채팅방 생성 : 학번 2개 요청
//    @Header("Authorization") token: String, // 인증 토큰
//    @Query("nickname1") nickname1: String,
//    @Query("nickname2") nickname2: String
//): Call<ChatRoomResponse> // 응답으로 ChatRoomResponse 클래스를 사용
//
//@GET("chats/rooms/{username}")
//fun getChatList( // 특정 유저가 참가 중인 채팅 목록 조회
//    @Header("Authorization") token: String,
//    @Path("username") username: String
//): Call<List<ChatRoomResponse>>
//
//@GET("chats/id/{chat_id}")
//fun getChatRoomById( // 채팅방 아이디롤 통해 특정 채팅방 조회
//    @Header("Authorization") token: String,
//    @Path("chat_id") id: Long
//): Call<ChatRoomResponse>
//
//@GET("users/{id}")
//fun getUserInfo( // 사용자 정보 조회
//    @Header("Authorization") token: String,
//    @Path("id") id: String // Path로 전송
//): Call<User>
//
//@GET("chats/room/{chatRoomId}/messages")
//fun getChatHistory(
//    @Header("Authorization") token: String,
//    @Path("chatRoomId") chatRoomId: Long
//): Call<List<ChatHistoryResponse>>
//
//@POST("users/rate/{nickname}/{rate}")
//fun userRate( // 유저 온도 변경
//    @Header("Authorization") token: String, // 인증 토큰
//    @Path("nickname") nickname: String,
//    @Path("rate") rate: Int
//): Call<Double>
//
//@GET("users/nickname/{nickname}")
//fun getUserInfobyNick(
//    @Header("Authorization") token: String,
//    @Path("nickname") nickname: String
//): Call<UserResponse>