package com.example.saenaljigi_app.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.saenaljigi_app.RetrofitClient
import com.example.saenaljigi_app.databinding.FragmentHomeBinding
import com.example.saenaljigi_app.menu.CalendarDto
import com.example.saenaljigi_app.menu.MenuApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // 오늘 날짜 가져오기 (로컬 시간대 설정)
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA)
        dateFormat.timeZone = TimeZone.getDefault()
        val todayDate: String = dateFormat.format(calendar.time)

        // TextView에 날짜 설정
        binding.homeDate.text = todayDate

        // 오늘의 메뉴 불러오기
        val today: LocalDate = LocalDate.now()
        fetchTodayMenu(today)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // 오늘 메뉴 불러오기
    private fun fetchTodayMenu(todayDate: LocalDate) {
        val menuService = RetrofitClient.instance.create(MenuApiService::class.java)
        val formattedDate = todayDate.toString()
        Log.d("MenuDetail_R", "$formattedDate")

        menuService.getMenu(formattedDate).enqueue(object : Callback<CalendarDto> {
            override fun onResponse(call: Call<CalendarDto>, response: Response<CalendarDto>) {
                if (response.isSuccessful) {
                    response.body()?.let { menus ->
                        Log.d("MenuDetail_R", "$menus")

                        // 중식과 석식의 foodName만 분리하여 리스트 생성
                        val lunchMenuNames = mutableListOf<String>()
                        val dinnerMenuNames = mutableListOf<String>()

                        menus.menus.forEach { menu ->
                            when (menu.foodTime) {
                                "중식" -> lunchMenuNames.addAll(menu.foods.map { it.foodName })
                                "석식" -> dinnerMenuNames.addAll(menu.foods.map { it.foodName })
                            }
                        }

                        // "\n"으로 구분된 문자열 생성
                        val lunchMenu = lunchMenuNames.joinToString("\n")
                        val dinnerMenu = dinnerMenuNames.joinToString("\n")

                        val breakfast =
                            "식당 스낵바에\n샐러드&샌드위치\n준비되어있습니다.\n이틀 전에\n예약하신 후\n이용 가능합니다."

                        Log.d("HomeFrag", "Lunch: $lunchMenu, Dinner: $dinnerMenu")

                        // 텍스트뷰에 표시 (예: 점심 메뉴와 저녁 메뉴)
                        binding.breakfastMenu.text = breakfast
                        binding.lunchMenu.text = lunchMenu
                        binding.dinnerMenu.text = dinnerMenu

                    } ?: Log.e("HomeFrag", "Response body is null")
                } else {
                    Log.e("HomeFrag", "Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<CalendarDto>, t: Throwable) {
                Log.e("HomeFrag", "Error: ${t.message}")
            }
        })
    }

}