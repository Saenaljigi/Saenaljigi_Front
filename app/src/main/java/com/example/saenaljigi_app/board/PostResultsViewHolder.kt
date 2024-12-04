package com.example.saenaljigi_app.board

import android.graphics.Typeface
import android.text.SpannableString
import android.text.style.StyleSpan
import androidx.recyclerview.widget.RecyclerView
import com.example.saenaljigi_app.databinding.ItemBoardBinding
import java.util.Locale

class PostResultsViewHolder(
    private val binding: ItemBoardBinding,
    private val onItemClick: (PostClass) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: PostClass, searchKeyword: String) {
        val spannableTitle = SpannableString(post.title)

        val searchKeywordLowerCase = searchKeyword.lowercase(Locale.getDefault())  // Locale을 명시적으로 설정

        var startIndex = post.title.lowercase(Locale.getDefault()).indexOf(searchKeywordLowerCase)  // Locale 명시
        while (startIndex != -1) {
            val endIndex = startIndex + searchKeyword.length
            spannableTitle.setSpan(StyleSpan(Typeface.BOLD), startIndex, endIndex, 0)

            startIndex = post.title.lowercase(Locale.getDefault()).indexOf(searchKeywordLowerCase, startIndex + 1)
        }

        binding.tvTitle.text = spannableTitle

        try {
            val elapsedTime = calculateElapsedTime(post.createdAt)
            binding.tvCreatedAt.text = elapsedTime
        } catch (e: Exception) {
            binding.tvCreatedAt.text = "시간 정보 없음"
        }

        binding.root.setOnClickListener { onItemClick(post) }
    }

    private fun calculateElapsedTime(createdAt: String): String {
        val formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
        val utcTime = java.time.LocalDateTime.parse(createdAt, formatter)
        val koreaDateTime = utcTime.atZone(java.time.ZoneId.of("UTC")).withZoneSameInstant(java.time.ZoneId.of("Asia/Seoul"))
        val now = java.time.ZonedDateTime.now(java.time.ZoneId.of("Asia/Seoul"))
        val duration = java.time.Duration.between(koreaDateTime, now)

        return when {
            duration.toDays() > 0 -> "${duration.toDays()} 일 전"
            duration.toHours() > 0 -> "${duration.toHours() % 24} 시간 전"
            duration.toMinutes() > 0 -> "${duration.toMinutes() % 60} 분 전"
            else -> "방금 전"
        }
    }
}
