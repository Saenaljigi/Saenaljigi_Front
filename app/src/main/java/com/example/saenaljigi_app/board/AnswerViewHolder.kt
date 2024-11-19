package com.example.saenaljigi_app.board

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.saenaljigi_app.databinding.ItemAnswerBinding

class AnswerViewHolder(
    private val binding: ItemAnswerBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(answer: CommentDto) {
        binding.tvAnswerWriter.text = answer.anonymousName
        binding.tvAnswerContent.text = answer.content
        binding.tvAnswerCreatedAt.text = answer.createdAt

        val commentAdapter = CommentAdapter(answer.replies)
        binding.rvAnswerContainer.layoutManager = LinearLayoutManager(binding.root.context)
        binding.rvAnswerContainer.adapter = commentAdapter
    }
}
