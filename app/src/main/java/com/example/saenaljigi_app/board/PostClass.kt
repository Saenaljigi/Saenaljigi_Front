package com.example.saenaljigi_app.board

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostClass(
    val id: Long,
    val username:String,
    val title: String,
    val content: String,
    val anonymousName: String,
    val likeCnt: Long,
    val commentCnt: Long,
    val comments: List<CommentDto>,
    val createdAt: String
) : Parcelable

@Parcelize
data class CommentDto(
    val id: Long,
    val postId: Long,
    val content: String,
    val anonymousName: String,
    val replyCnt: Int,
    val comments: List<CommentDto>,
    val createdAt: String
) : Parcelable
