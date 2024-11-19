package com.example.saenaljigi_app.board

import androidx.recyclerview.widget.RecyclerView
import com.example.saenaljigi_app.databinding.ItemBoardBinding

class BoardViewHolder(
    private val binding: ItemBoardBinding,
    private val onItemClick: (BoardClass) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(board: BoardClass) {
        binding.tvTitle.text = board.title
        binding.tvCreatedAt.text = board.createdAt
        binding.tvWriter.text = board.anonymousName

        binding.root.setOnClickListener { onItemClick(board) }
    }
}
