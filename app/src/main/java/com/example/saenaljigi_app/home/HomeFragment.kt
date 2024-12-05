package com.example.saenaljigi_app.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import com.example.saenaljigi_app.R
import com.example.saenaljigi_app.RetrofitClient
import com.example.saenaljigi_app.databinding.FragmentHomeBinding
import com.example.saenaljigi_app.menu.CalendarDto
import com.example.saenaljigi_app.menu.MenuApiService
import com.example.saenaljigi_app.notice.NoticeBoardData
import com.example.saenaljigi_app.notice.NoticeBoardFragment
import com.example.saenaljigi_app.notice.NoticeBoardService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

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

        // 식권 사용횟수 불러오기
        val mealTicketCount = getMealTicketCount()
        binding.mealCount.text = "${mealTicketCount}회"

        // 총 상벌점 불러오기
        val total_point = fetchPoint()
        // 텍스트뷰 변경
        binding.totalCount.text = if (total_point > 0) "+$total_point" else total_point.toString()

        if (total_point < 0)
            binding.totalCountCmt.text = "벌점이 더 높아요 \uD83D\uDE13"
        else if (total_point == 0)
            binding.totalCountCmt.text = "상벌점이 같아요 \uD83D\uDE10"

        // 공지사항 2개 불러오기
        fetchNotice()

        // 자세히 보기 클릭 시 공지사항 프래그먼트로 이동
        binding.detailBtn.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, NoticeBoardFragment())
            transaction.commit()

            // 바텀 네비게이션 상태를 NoticeBoardFragment로 동기화
            val bottomNavigationView =
                requireActivity().findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(
                    R.id.bottom_navigation_view
                )
            bottomNavigationView.selectedItemId = R.id.fragment_notice
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // 오늘 메뉴 불러오기
    private fun fetchTodayMenu(todayDate: LocalDate) {
        val token = getJwtToken()
        val userId = getUserId()

        val menuService = RetrofitClient.instance.create(MenuApiService::class.java)
        val formattedDate = todayDate.toString()
        Log.d("HomeFrag_M", "$formattedDate")

        menuService.getMenu(token, formattedDate, userId).enqueue(object : Callback<CalendarDto> {
            override fun onResponse(call: Call<CalendarDto>, response: Response<CalendarDto>) {
                if (response.isSuccessful) {
                    response.body()?.let { menus ->
                        Log.d("HomeFrag_M", "$menus")

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

                        val breakfast = "식당 스낵바에\n샐러드&샌드위치\n준비되어있습니다.\n이틀 전에\n예약하신 후\n이용 가능합니다."

                        Log.d("HomeFrag_M", "Lunch: $lunchMenu, Dinner: $dinnerMenu")

                        // 텍스트뷰에 표시
                        binding.breakfastMenu.text = breakfast
                        binding.lunchMenu.text = lunchMenu
                        binding.dinnerMenu.text = dinnerMenu
                    } ?: Log.e("HomeFrag_M", "Menu data is null")
                } else {
                    Log.e("HomeFrag_M", "Error body: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<CalendarDto>, t: Throwable) {
                Log.e("HomeFrag_M", "Error fetching menu data", t)
            }
        })
    }

    private fun getMealTicketCount(): Int {
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("MealTicket", 0)  // 기본값은 0으로 설정
    }

    private fun fetchPoint(): Int {
        return 2
    }

    private fun fetchNotice() {
        val token = getJwtToken()
        Log.d("HomeFragment", "token: $token")

        val service = RetrofitClient.instance.create(NoticeBoardService::class.java)
        service.getNotice(token).enqueue(object : Callback<List<NoticeBoardData>> {
            override fun onResponse(
                call: Call<List<NoticeBoardData>>,
                response: Response<List<NoticeBoardData>>
            ) {
                Log.d("HomeFragment", "Received response for notice list")

                if (response.isSuccessful) {
                    response.body()?.let { notices ->
                        if (notices.isNotEmpty()) {
                            val notice1 = notices[0]
                            val notice2 = notices[1] // 두 번째 공지사항이 없을 수 있으므로 getOrNull() 사용
                            Log.d("HomeFragment", "notice: $notice1, $notice2")

                            // 제목이 24자 이상이면 생략 처리
                            binding.noticeContent1.text = notice1.title.let {
                                if (it.length > 24) it.substring(0, 25) + " ..." else it
                            }

                            // created_at 포맷 처리
                            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val outputFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())

                            val formattedDate1 = formatDate(notice1.created_at, inputFormat, outputFormat)
                            binding.noticePostedTime1.text = formattedDate1

                            notice2?.let {
                                binding.noticeContent2.text = it.title.let {
                                    if (it.length > 24) it.substring(0, 25) + " ..." else it
                                }

                                val formattedDate2 = formatDate(it.created_at, inputFormat, outputFormat)
                                binding.noticePostedTime2.text = formattedDate2
                            }

                        } else {
                            Log.e("HomeFragment", "Notice list is empty")
                        }
                    } ?: run {
                        Log.e("HomeFragment", "Notice list response body is null")
                    }
                } else {
                    Log.e(
                        "HomeFragment",
                        "Failed to fetch notice list, Code: ${response.code()}, Error Body: ${response.message()}"
                    )
                }
            }

            override fun onFailure(call: Call<List<NoticeBoardData>>, t: Throwable) {
                Log.e("HomeFragment", "Error occurred while fetching notice list", t)
            }
        })
    }

    // 날짜 포맷 처리 함수 추가
    private fun formatDate(dateString: String?, inputFormat: SimpleDateFormat, outputFormat: SimpleDateFormat): String {
        return if (!dateString.isNullOrEmpty()) {
            try {
                val parsedDate = inputFormat.parse(dateString)
                outputFormat.format(parsedDate)
            } catch (e: Exception) {
                Log.e("HomeFragment", "Error parsing date: $dateString", e)
                dateString // 날짜 형식 오류가 발생하면 원래의 문자열을 반환
            }
        } else {
            "" // created_at이 null이거나 빈 문자열인 경우 처리
        }
    }

    private fun getJwtToken(): String {
        val sharedPref = requireContext().getSharedPreferences("auth", Context.MODE_PRIVATE)
        return sharedPref.getString("jwt_token", "") ?: ""
    }

    private fun getUserId(): Int {
        val sharedPref = requireContext().getSharedPreferences("auth", Context.MODE_PRIVATE)
        val userId = sharedPref.getInt("userId", 0) // 기본값 0
        Log.d("UserId", "UserId: $userId")  // 로그 출력
        return userId
    }
}
