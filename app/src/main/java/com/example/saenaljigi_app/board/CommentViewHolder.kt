package com.example.saenaljigi_app.board

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.example.saenaljigi_app.R
import com.example.saenaljigi_app.databinding.ItemCommentBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CommentViewHolder(
    private val binding: ItemCommentBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(comment: CommentDto) {
        Log.d("CommentViewHolder", "Binding comment with ID: ${comment.id}, Content: ${comment.content}, CreatedAt: ${comment.createdAt}")

        // 작성자 설정 및 배경 이미지 변경
        if (comment.id == 1L) {
            binding.tvCommentWriter.text = "작성자"
            binding.ivBackgroundComment.setImageResource(R.drawable.background_comment2) // 배경 변경
            Log.d("CommentViewHolder", "Set background to background_comment2 for comment ID: ${comment.id}")
        } else {
            binding.tvCommentWriter.text = "종이${comment.id - 1}"
            binding.ivBackgroundComment.setImageResource(R.drawable.background_comment) // 기본 배경
            Log.d("CommentViewHolder", "Set background to background_comment for comment ID: ${comment.id}")
        }

        // 댓글 내용 설정
        binding.tvCommentContent.text = "네 가능해요" // 내용은 "네 가능해요"로 고정
        Log.d("CommentViewHolder", "Comment content: 네 가능해요")

        // 현재 시간 "00:00" 형식으로 설정
        val formattedTime = getCurrentTime()
        binding.tvCommentCreatedAt.text = formattedTime
        Log.d("CommentViewHolder", "Formatted createdAt: $formattedTime")
    }

    private fun getCurrentTime(): String {
        val currentDate = Date()
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault()) // 시간만 가져옴
        return timeFormat.format(currentDate)
    }
}