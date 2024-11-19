package com.example.saenaljigi_app.board

import android.graphics.Color
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.saenaljigi_app.R
import com.example.saenaljigi_app.databinding.ActivitySearchPostBinding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class SearchPostActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchPostBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySearchPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val etKeyword = binding.etSearchPost
        val chipGroup = binding.searchChipGroup

        etKeyword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val text = etKeyword.text.toString().trim()
                if (text.isNotEmpty()) {
                    addChipToGroup(chipGroup, text)
                    etKeyword.text.clear() // 입력 필드 초기화
                }
                true // 이벤트 소비
            } else {
                false
            }
        }
        
        binding.deleteArea.setOnClickListener {
            binding.searchChipGroup.removeAllViews()
        }
    }

    // Chip 생성 함수
    private fun addChipToGroup(chipGroup: ChipGroup, text: String) {
        val chip = Chip(this).apply {
            this.text = text
            setTextColor(Color.parseColor("#46515B"))
            textSize = 13f
            chipBackgroundColor = getColorStateList("#E9E9E9")
            chipCornerRadius = 26.dpToPx().toFloat()
            chipStrokeColor = getColorStateList("#00000000") // 투명색
            chipStrokeWidth = 0f // 테두리 제거
            layoutParams = ChipGroup.LayoutParams(
                ChipGroup.LayoutParams.WRAP_CONTENT,
                ChipGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                height = 46.dpToPx()
            }
        }
        chipGroup.addView(chip, 0)
    }

    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }

    private fun getColorStateList(color: String) =
        android.content.res.ColorStateList.valueOf(Color.parseColor(color))
}
