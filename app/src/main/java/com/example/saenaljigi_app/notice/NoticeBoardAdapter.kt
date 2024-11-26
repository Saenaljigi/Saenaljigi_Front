package com.example.saenaljigi_app.notice

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.saenaljigi_app.databinding.ItemNoticeBoardBinding

class NoticeBoardAdapter(
    private val noticeList: ArrayList<NoticeBoardData>,
    private val onItemClickListener: (Long) -> Unit // 클릭 리스너
) : RecyclerView.Adapter<NoticeBoardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeBoardViewHolder {
        val binding = ItemNoticeBoardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoticeBoardViewHolder(binding, onItemClickListener)
    }

    override fun getItemCount(): Int {
        return noticeList.size
    }

    override fun onBindViewHolder(holder: NoticeBoardViewHolder, position: Int) {
        val item = noticeList[position]
        holder.bind(item, position == noticeList.size - 1) // 마지막 아이템 여부 전달
    }
}
