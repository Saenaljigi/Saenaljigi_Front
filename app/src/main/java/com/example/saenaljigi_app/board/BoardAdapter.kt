package com.example.saenaljigi_app.board

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.saenaljigi_app.databinding.ItemBoardBinding

class BoardAdapter(
    private val boards: List<BoardClass>,
    private val onItemClick: (BoardClass) -> Unit
) : RecyclerView.Adapter<BoardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        val binding = ItemBoardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BoardViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        holder.bind(boards[position])
    }

    override fun getItemCount(): Int = boards.size
}
