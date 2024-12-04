package com.example.saenaljigi_app.board

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.saenaljigi_app.R
import com.example.saenaljigi_app.databinding.ItemAnswerBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AnswerViewHolder(
    private val binding: ItemAnswerBinding
) : RecyclerView.ViewHolder(binding.root) {

    private var commentAdapter: CommentAdapter? = null

    init {
        Log.d("AnswerViewHolder", "AnswerViewHolder initialized. Item binding: ${binding.root}")
    }

    fun bind(answer: CommentDto) {
        Log.d("AnswerViewHolder", "Binding answer: ${answer.id}, Content: ${answer.content}, CreatedAt: ${answer.createdAt}")

        // createdAt 포맷 변환
        val formattedTime = formatTime(answer.createdAt)
        binding.tvAnswerCreatedAt.text = formattedTime
        Log.d("AnswerViewHolder", "Formatted time: $formattedTime")

        // tvAnswerWriter 조건에 따른 값 설정
        val writerText = if (answer.id == 1L) "작성자" else "종이${answer.id - 1}"
        binding.tvAnswerWriter.text = writerText
        Log.d("AnswerViewHolder", "Answer writer: $writerText")

        // 댓글이 있을 경우 처리
        if (answer.comments != null && answer.comments.isNotEmpty()) {
            Log.d("AnswerViewHolder", "Answer has comments: ${answer.comments.size} comments")

            // 댓글이 있다면 RecyclerView에 CommentAdapter를 설정
            if (commentAdapter == null) {
                Log.d("AnswerViewHolder", "Creating new CommentAdapter")
                commentAdapter = CommentAdapter(answer.comments)
                binding.rvAnswerContainer.layoutManager = LinearLayoutManager(binding.root.context)
                binding.rvAnswerContainer.adapter = commentAdapter
            } else {
                Log.d("AnswerViewHolder", "Updating CommentAdapter with new data")
                commentAdapter?.updateComments(answer.comments)
            }
        } else {
            // 댓글이 없으면 RecyclerView를 숨김
            binding.rvAnswerContainer.visibility = RecyclerView.GONE
            Log.d("AnswerViewHolder", "No comments found, hiding RecyclerView")
        }

        // 댓글 내용 표시
        binding.tvAnswerContent.text = answer.content
        Log.d("AnswerViewHolder", "Answer content: ${answer.content}")
    }

    // createdAt에서 시간을 추출하여 "12:42" 형식으로 변환하는 함수
    private fun formatTime(createdAt: String): String {
        return try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSS", Locale.getDefault())
            val date = dateFormat.parse(createdAt)
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault()) // 시간만 가져옴
            timeFormat.format(date ?: Date())
        } catch (e: ParseException) {
            Log.e("AnswerViewHolder", "Date parsing failed: ${e.message}")
            ""
        }
    }
}
