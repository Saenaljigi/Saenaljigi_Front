package com.example.saenaljigi_app.board

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.saenaljigi_app.databinding.ItemCommentBinding

class CommentAdapter(
    private var comments: List<CommentDto>
) : RecyclerView.Adapter<CommentViewHolder>() {

    fun updateComments(newComments: List<CommentDto>) {
        comments = newComments
        notifyDataSetChanged()
        Log.d("CommentAdapter", "Updated comments list with ${newComments.size} items")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(comments[position])
    }

    override fun getItemCount(): Int = comments.size
}
