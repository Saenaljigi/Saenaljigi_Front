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
        // 게시글 제목 설정
        binding.tvTitle.text = board.title

        // 작성자 이름 설정
        binding.tvWriter.text = board.anonymousName

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

            // 시간 차이를 TextView에 설정
            binding.tvCreatedAt.text = elapsedTime
        } catch (e: Exception) {
            // 오류 발생 시 기본 메시지 표시
            binding.tvCreatedAt.text = "시간 정보 없음"
        }

        // 아이템 클릭 이벤트 설정
        binding.root.setOnClickListener { onItemClick(board) }
    }
}
