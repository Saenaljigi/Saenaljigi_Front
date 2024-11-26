package com.example.saenaljigi_app.notice

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.saenaljigi_app.databinding.ItemNoticeBoardBinding

class NoticeBoardViewHolder(
    private val binding: ItemNoticeBoardBinding,
    private val onItemClickListener: (Long) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(notice: NoticeBoardData, isLastItem: Boolean) {
        // 제목과 작성일 데이터 설정
        binding.tvTile.text = notice.title
        binding.tvCreatedAt.text = notice.created_at

        // 마지막 아이템일 경우 선 숨기기
        binding.imgLine.visibility = if (isLastItem) View.GONE else View.VISIBLE

        // 디버그 로그 추가
        Log.d("NoticeBoardViewHolder", "Binding: Title=${notice.title}, CreatedAt=${notice.created_at}")

        // 클릭 리스너 연결
        binding.root.setOnClickListener {
            onItemClickListener(notice.id) // 공지사항 ID 전달
        }
    }
}
