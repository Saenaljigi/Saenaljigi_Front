package com.example.saenaljigi_app.notice

import androidx.recyclerview.widget.RecyclerView
import com.example.saenaljigi_app.databinding.ItemNoticeBoardBinding

class NoticeBoardViewHolder(
    private val binding: ItemNoticeBoardBinding,
    private val onItemClickListener: (Long) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(notice: NoticeBoardData) {
        binding.tvTile.text = notice.title
        binding.tvCreatedAt.text = notice.created_at

        // 아이템 클릭 시 onItemClickListener 호출
        binding.root.setOnClickListener {
            onItemClickListener(notice.id)
        }
    }
}
