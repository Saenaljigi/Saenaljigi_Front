package com.example.saenaljigi_app.board

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BoardClass(
    val postId: Long,
    val title: String,
    val content: String,
    val anonymousName: String,
    val likeCount: Int,
    val replyCount: Int,
    val replies: List<CommentDto>,
    val createdAt: String
) : Parcelable

@Parcelize
data class CommentDto(
    val commentId: Long,
    val postId: Long,
    val content: String,
    val anonymousName: String,
    val replyCount: Int,
    val replies: List<CommentDto>,
    val createdAt: String
) : Parcelable
