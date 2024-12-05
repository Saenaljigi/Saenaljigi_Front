package com.example.saenaljigi_app.mypage

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.saenaljigi_app.R
import com.example.saenaljigi_app.databinding.FragmentMealTicketBinding
import com.example.saenaljigi_app.mypage.CustomDividerItemDecoration
import com.example.saenaljigi_app.mypage.MyPageHistoryAdapter
import com.example.saenaljigi_app.mypage.MyPageHistoryData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Locale

class MealTicketFragment : Fragment() {
    private var _binding: FragmentMealTicketBinding? = null
    private val binding get() = _binding!!

    private lateinit var myPageHistoryAdapter: MyPageHistoryAdapter
    private lateinit var myPageHistoryData: ArrayList<MyPageHistoryData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMealTicketBinding.inflate(inflater, container, false)
        val view = binding.root

        // SharedPreferences에서 mealDataList 가져오기
        val savedMeals = getSavedMealsFromPreferences(requireContext())
        Log.d("PointFrag", "saved Data: $savedMeals")

        // savedMeals에서 데이터를 MyPageHistoryData로 변환하여 리스트에 추가
        myPageHistoryData = ArrayList(savedMeals.mapIndexed { index, meal ->
            val date = meal["date"] ?: ""
            val foodType = meal["foodType"] ?: ""

            // 날짜 포맷 변환
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
            val outputFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA)
            val formattedDate = try {
                val dateObj = inputFormat.parse(date)
                outputFormat.format(dateObj)
            } catch (e: Exception) {
                date  // 변환 실패시 원본 그대로 사용
            }
            Log.d("PointFrag", "Formatted Date: $formattedDate, Food Type: $foodType")

            // savedMeals의 개수 확인
            val savedMealsCount = savedMeals.size
            Log.d("PointFrag", "Saved Meals Count: $savedMealsCount")

            // 남은 식권 계산
            val remainingTicket = 150 - (savedMeals.size - index)

            MyPageHistoryData(formattedDate, foodType, "+1장", "잔여 식권 ${remainingTicket}장")
        })


        // 어댑터 초기화
        myPageHistoryAdapter = MyPageHistoryAdapter(myPageHistoryData)

        // RecyclerView에 어댑터와 레이아웃 매니저 연결
        binding.rcvMealTicket.apply {
            adapter = myPageHistoryAdapter
            layoutManager = LinearLayoutManager(context)
        }

        // 커스텀 구분선 적용
        val customItemDecoration = CustomDividerItemDecoration(R.drawable.mypage_divider)
        binding.rcvMealTicket.addItemDecoration(customItemDecoration)

        // back_btn 클릭 시 백스택으로 이동
        binding.backBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // 데이터 초기화
        binding.downBtn.setOnClickListener {
            // MenuPrefs에서 mealDataList 삭제
            var sharedPreferences = requireContext().getSharedPreferences("MenuPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.remove("mealDataList")
            editor.apply()

            // MyPrefs에서 MealTicket 삭제
            sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val editor2 = sharedPreferences.edit()
            editor2.remove("MealTicket")
            editor2.apply()

            // RecyclerView 데이터 초기화
            myPageHistoryData.clear()
            myPageHistoryAdapter.notifyDataSetChanged()

            // 로그 확인
            Log.d("MealTicketFragment", "SharedPreferences 데이터가 삭제되었습니다. MenuPrefs와 MyPrefs에서 삭제됨")
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // SharedPreferences에서 기존 데이터를 불러오는 함수
    fun getSavedMealsFromPreferences(context: Context): List<Map<String, String>> {
        val sharedPreferences = context.getSharedPreferences("MenuPrefs", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("mealDataList", "") ?: ""

        // Gson을 이용해 JSON을 리스트로 변환
        val listType = object : TypeToken<List<Map<String, String>>>() {}.type
        val savedMeals = Gson().fromJson<List<Map<String, String>>>(json, listType) ?: emptyList()

        // 리스트를 역순으로 변환 (저장된 역순으로 불러와져야 하기 때문)
        return savedMeals.reversed()
    }
}
