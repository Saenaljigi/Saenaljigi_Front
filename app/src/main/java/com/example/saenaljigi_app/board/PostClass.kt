package com.example.saenaljigi_app.board

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostClass(
    val id: Long,
    val userId:String,
    val title: String,
    val content: String,
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

data class CommentRequest(
    val userId: Int,
    val postId: Int,
    val content: String,
    val isAnonymous: Boolean
)