package com.example.saenaljigi_app.notice

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.saenaljigi_app.databinding.ItemNoticeBoardBinding

class NoticeBoardViewHolder(
    private val binding: ItemNoticeBoardBinding,
    private val onItemClickListener: (Long) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(notice: NoticeBoardData, isLastItem: Boolean) {
        binding.tvTile.text = notice.title
        binding.tvCreatedAt.text = notice.created_at
        binding.imgLine.visibility = if (isLastItem) View.GONE else View.VISIBLE

        binding.root.setOnClickListener {
            onItemClickListener(notice.id)
        }
    }
}
