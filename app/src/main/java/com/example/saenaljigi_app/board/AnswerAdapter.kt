package com.example.saenaljigi_app.board

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.saenaljigi_app.R
import com.example.saenaljigi_app.databinding.ItemAnswerBinding

class AnswerAdapter(
    private val answers: List<CommentDto>? = null,  // answers가 nullable로 변경됨
    private val onItemClicked: (position: Int, isSelected: Boolean) -> Unit // 클릭 여부 전달
) : RecyclerView.Adapter<AnswerViewHolder>() {

    private val selectedAnswers = mutableListOf<Boolean>().apply {
        repeat(answers?.size ?: 0) { add(false) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerViewHolder {
        val binding = ItemAnswerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AnswerViewHolder(binding) { position, isSelected ->
            selectedAnswers[position] = isSelected
            onItemClicked(position, isSelected)
        }
    }

    override fun onBindViewHolder(holder: AnswerViewHolder, position: Int) {
        val answer = answers?.get(position)
        answer?.let {
            holder.bind(it)
            holder.setSelectedState(selectedAnswers[position])
        }
    }

    override fun getItemCount(): Int = answers?.size ?: 0

    fun showReplyForSelectedItem(position: Int) {
        notifyItemChanged(position)  // 선택된 댓글 다시 그리기
    }

    // 선택된 댓글의 position 반환
    fun getSelectedCommentPosition(): Int {
        return selectedAnswers.indexOfFirst { it }  // 첫 번째 true인 index 반환
    }
}
