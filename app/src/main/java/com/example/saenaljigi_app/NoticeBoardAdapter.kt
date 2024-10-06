package com.example.saenaljigi_app

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.saenaljigi_app.databinding.ItemNoticeBoardBinding

class NoticeBoardAdapter(
    private val noticeList : ArrayList<NoticeBoardData>,
    private val onItemClickListener:(Long)->Unit//클릭
) : RecyclerView.Adapter<NoticeBoardViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeBoardViewHolder {
        val binding = ItemNoticeBoardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoticeBoardViewHolder(binding, onItemClickListener) // 클릭 리스너를 전달
    }

    override fun getItemCount(): Int {
        return noticeList.size
    }

    override fun onBindViewHolder(holder: NoticeBoardViewHolder, position: Int) {
        val item = noticeList[position]
        holder.bind(item) // 아이템을 바인딩
    }
}