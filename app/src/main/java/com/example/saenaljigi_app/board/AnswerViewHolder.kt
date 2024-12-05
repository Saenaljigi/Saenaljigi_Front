package com.example.saenaljigi_app.board

import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.saenaljigi_app.R
import com.example.saenaljigi_app.databinding.ItemAnswerBinding
import com.example.saenaljigi_app.databinding.ItemCommentBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AnswerViewHolder(
    private val binding: ItemAnswerBinding,
    private val onItemClicked: (position: Int, isSelected: Boolean) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private var isSelected = false // 클릭 상태를 추적

    init {
        binding.root.setOnClickListener {
            // 클릭 상태 토글
            isSelected = !isSelected

            // 클릭 여부를 BoardDetailFragment로 전달
            onItemClicked(adapterPosition, isSelected)
        }
    }

    fun bind(answer: CommentDto) {
        binding.tvAnswerCreatedAt.text = formatTime(answer.createdAt)
        binding.tvAnswerWriter.text = if (answer.id == 1L) "작성자" else "종이${answer.id - 1}"
        binding.tvAnswerContent.text = answer.content

        // 대댓글 뷰의 가시성 설정
        binding.sampleComment.visibility = if (isSelected) {
            View.VISIBLE // 클릭된 상태면 대댓글 보이기
        } else {
            View.GONE // 기본적으로 숨기기
        }
        // 대댓글 내부 시간 업데이트
        val sampleCommentBinding = ItemCommentBinding.bind(binding.sampleComment)
        updateCommentTime(sampleCommentBinding)
    }

    fun setSelectedState(isSelected: Boolean) {
        this.isSelected = isSelected

        // 배경색 및 버튼 백그라운드 변경
        val backgroundColor = if (isSelected) {
            Color.parseColor("#F1F1F1") // 클릭된 상태 배경색
        } else {
            Color.parseColor("#FFFFFF") // 기본 배경색
        }
        binding.root.setBackgroundColor(backgroundColor)

        val buttonBackground = if (isSelected) {
            R.drawable.comment_icon3 // 클릭된 상태 버튼 아이콘
        } else {
            R.drawable.comment_icon2 // 기본 버튼 아이콘
        }
        binding.btnComment.setBackgroundResource(buttonBackground)
    }

    private fun formatTime(createdAt: String): String {
        return try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSS", Locale.getDefault())
            val date = dateFormat.parse(createdAt)
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            timeFormat.format(date ?: Date())
        } catch (e: ParseException) {
            ""
        }
    }

    private fun updateCommentTime(commentBinding: ItemCommentBinding) {
        // 현재 시간을 대댓글 시간에 설정
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        commentBinding.tvCommentCreatedAt.text = currentTime
    }
}
