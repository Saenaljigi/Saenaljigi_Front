package com.example.saenaljigi_app.board

import androidx.recyclerview.widget.RecyclerView
import com.example.saenaljigi_app.databinding.ItemBoardBinding
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class BoardViewHolder(
    private val binding: ItemBoardBinding,
    private val onItemClick: (PostClass) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(board: PostClass) {
        binding.tvTitle.text = board.title

        binding.tvLikeIcon.text = "${board.likeCnt}개"
        binding.tvCommentIcon.text = "${board.commentCnt}개"

        try {
            // 시간 계산 로직
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
            val utcTime = LocalDateTime.parse(board.createdAt, formatter)
            val koreaDateTime = utcTime.atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.of("Asia/Seoul"))
            val now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
            val duration = Duration.between(koreaDateTime, now)

            val elapsedTime = when {
                duration.toDays() > 0 -> "${duration.toDays()} 일 전"
                duration.toHours() > 0 -> "${duration.toHours() % 24} 시간 전"
                duration.toMinutes() > 0 -> "${duration.toMinutes() % 60} 분 전"
                else -> "방금 전"
            }

            binding.tvCreatedAt.text = elapsedTime
        } catch (e: Exception) {
            binding.tvCreatedAt.text = "시간 정보 없음"
        }

        binding.root.setOnClickListener { onItemClick(board) }
    }
}
