package com.example.saenaljigi_app.board

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.saenaljigi_app.databinding.ItemAnswerBinding

class AnswerAdapter(private val comments: List<CommentDto>) : RecyclerView.Adapter<AnswerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerViewHolder {
        val binding = ItemAnswerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AnswerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnswerViewHolder, position: Int) {
        val comment = comments[position]

        // 로그를 추가해서 댓글 내용이 제대로 전달되는지 확인
        Log.d("AnswerAdapter", "Binding comment at position $position: ${comment.content}")

        holder.bind(comment) // 댓글 데이터를 바인딩
    }

    override fun getItemCount(): Int = comments.size
}
