package com.example.saenaljigi_app.board

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
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

        val etKeyword = binding.etSearchPost
        val chipGroup = binding.searchChipGroup

        // 저장된 Chip 데이터 복원
        loadChips(chipGroup)

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
            chipGroup.removeAllViews()
            saveChips(emptyList()) // Chip 데이터 초기화
        }

        binding.searchBackBtn.setOnClickListener {
            // 뒤로가기 버튼 동작
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

    // Chip 생성 및 추가 함수
    private fun addChipToGroup(chipGroup: ChipGroup, text: String) {
        // Chip 개수 확인 후 10개 초과 시 첫 번째 Chip 제거
        if (chipGroup.childCount >= 10) {
            chipGroup.removeViewAt(0)
        }

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

    // Chip 생성 및 추가 함수
    private fun addChipToGroup(chipGroup: ChipGroup, text: String, saveToPrefs: Boolean = true) {
        // Chip 개수 확인 후 10개 초과 시 가장 오래된 Chip(맨 뒤) 제거
        if (chipGroup.childCount >= 10) {
            chipGroup.removeViewAt(chipGroup.childCount - 1) // 맨 뒤 Chip 제거
        }

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
        chipGroup.addView(chip, 0) // Chip을 항상 맨 앞에 추가

        // 저장 옵션이 활성화된 경우에만 SharedPreferences에 저장
        if (saveToPrefs) {
            saveChips(getChipTexts(chipGroup))
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
