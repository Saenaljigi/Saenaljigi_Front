package com.example.saenaljigi_app.board

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.example.saenaljigi_app.MainActivity
import com.example.saenaljigi_app.R
import com.example.saenaljigi_app.databinding.ActivitySearchPostBinding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class SearchPostActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchPostBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySearchPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("ChipPreferences", Context.MODE_PRIVATE)

        val chipGroup = binding.searchChipGroup

        // 저장된 Chip 데이터 복원
        loadChips(chipGroup)

        val etKeyword = binding.etSearchPost

        etKeyword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val text = etKeyword.text.toString().trim()
                if (text.isNotEmpty()) {
                    // 검색어가 있을 경우 MainActivity로 검색어를 전달
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("searchQuery", text)
                    startActivity(intent)
                    finish() // SearchPostActivity 종료
                }
                true
            } else {
                false
            }
        }

        binding.deleteArea.setOnClickListener {
            chipGroup.removeAllViews()
            saveChips(emptyList()) // Chip 데이터 초기화
        }

        binding.searchBackBtn.setOnClickListener {
            onBackPressed() // 기존의 뒤로가기 동작을 호출
        }

        setStatusBarTransparent()
    }

    private fun setStatusBarTransparent() {
        window.apply {
            setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }
        if (Build.VERSION.SDK_INT >= 30) { // API 30 이상에 적용
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
    }

    // chip 생성 함수
    private fun addChipToGroup(chipGroup: ChipGroup, text: String) {
        // 새로운 Chip 추가 전에 Chip 개수를 확인하고 10개 초과 시 가장 오래된 Chip(맨 뒤)을 제거
        if (chipGroup.childCount >= 10) {
            chipGroup.removeViewAt(chipGroup.childCount - 1) // 가장 오래된 Chip 삭제 (맨 뒤)
        }

        // 새로운 Chip 생성
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

        // 새로운 Chip을 항상 맨 앞에 추가
        chipGroup.addView(chip, 0)

        // 현재 Chip 데이터를 저장
        saveChips(getChipTexts(chipGroup))
    }

    // SharedPreferences에 Chip 데이터 저장
    private fun saveChips(chips: List<String>) {
        val jsonString = chips.joinToString(",") // Chip 데이터를 JSON 형태로 저장
        sharedPreferences.edit().putString("chips", jsonString).apply()
    }

    // SharedPreferences에서 Chip 데이터 로드
    private fun loadChips(chipGroup: ChipGroup) {
        val savedChipsString = sharedPreferences.getString("chips", "") ?: ""
        val savedChips = if (savedChipsString.isNotEmpty()) {
            savedChipsString.split(",").filter { it.isNotEmpty() } // JSON 문자열을 리스트로 변환
        } else {
            emptyList()
        }
        // 저장된 순서대로 Chip 추가 (맨 뒤에 추가)
        savedChips.forEach { text ->
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
            chipGroup.addView(chip) // 저장된 순서대로 맨 뒤에 추가
        }
    }

    // ChipGroup에서 현재 Chip 텍스트 목록 추출
    private fun getChipTexts(chipGroup: ChipGroup): List<String> {
        val chips = mutableListOf<String>()
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as? Chip
            chip?.let {
                chips.add(it.text.toString())
            }
        }
        return chips
    }

    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }

    private fun getColorStateList(color: String) =
        android.content.res.ColorStateList.valueOf(Color.parseColor(color))
}
