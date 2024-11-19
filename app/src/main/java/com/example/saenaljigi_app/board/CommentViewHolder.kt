package com.example.saenaljigi_app.board

import androidx.recyclerview.widget.RecyclerView
import com.example.saenaljigi_app.databinding.ItemCommentBinding

class CommentViewHolder(
    private val binding: ItemCommentBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(comment: CommentDto) {
        binding.tvCommentWriter.text = comment.anonymousName
        binding.tvCommentContent.text = comment.content
        binding.tvCommentCreatedAt.text = comment.createdAt
    }
}
