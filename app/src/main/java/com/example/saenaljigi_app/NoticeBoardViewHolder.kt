package com.example.saenaljigi_app

import android.widget.AdapterView.OnItemClickListener
import androidx.recyclerview.widget.RecyclerView
import com.example.saenaljigi_app.databinding.ItemNoticeBoardBinding

class NoticeBoardViewHolder(
    private val binding: ItemNoticeBoardBinding,
    private val onItemClickListener: (Long)->Unit
):  RecyclerView.ViewHolder(binding.root){

    fun bind(notice:NoticeBoardData){

        binding.tvTile.text=notice.tv_title
        binding.tvCreatedAt.text=notice.tv_created_at

    }
}