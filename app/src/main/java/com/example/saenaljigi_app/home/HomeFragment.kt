package com.example.saenaljigi_app.home

import NoticeBoardData
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.saenaljigi_app.R
import com.example.saenaljigi_app.RetrofitClient
import com.example.saenaljigi_app.databinding.FragmentHomeBinding
import com.example.saenaljigi_app.menu.CalendarDto
import com.example.saenaljigi_app.menu.MenuApiService
import com.example.saenaljigi_app.notice.NoticeBoardFragment
import com.example.saenaljigi_app.notice.NoticeBoardService
import kotlinx.coroutines.launch
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
    private val binding get() = _binding ?: throw IllegalStateException("Binding should not be null")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val calendar = Calendar.getInstance(TimeZone.getDefault())
        val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA)
        dateFormat.timeZone = TimeZone.getDefault()
        val todayDate: String = dateFormat.format(calendar.time)

        binding.homeDate.text = todayDate

        val today: LocalDate = LocalDate.now()
        fetchTodayMenu(today)

        val mealTicketCount = getMealTicketCount()
        binding.mealCount.text = "${mealTicketCount}회"

        val totalPoint = fetchPoint()
        binding.totalCount.text = if (totalPoint > 0) "+$totalPoint" else totalPoint.toString()

        binding?.let {
            it.homeDate.text = todayDate
            it.mealCount.text = "${mealTicketCount}회"
            it.totalCount.text = if (totalPoint > 0) "+$totalPoint" else totalPoint.toString()
            if (totalPoint < 0)
                it.totalCountCmt.text = "벌점이 더 높아요 \uD83D\uDE13"
            else if (totalPoint == 0)
                it.totalCountCmt.text = "상벌점이 같아요 \uD83D\uDE10"
        }

        fetchNotice()

        binding.detailBtn.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, NoticeBoardFragment())
            transaction.commit()

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

    private fun fetchTodayMenu(todayDate: LocalDate) {
        val token = getJwtToken()
        val userId = getUserId()

        val menuService = RetrofitClient.instance.create(MenuApiService::class.java)
        val formattedDate = todayDate.toString()
        Log.d("HomeFrag_M", "$formattedDate")

        viewLifecycleOwner.lifecycleScope.launch {
            menuService.getMenu(token, formattedDate, userId).enqueue(object : Callback<CalendarDto> {
                override fun onResponse(call: Call<CalendarDto>, response: Response<CalendarDto>) {
                    if (response.isSuccessful) {
                        response.body()?.let { menus ->
                            val lunchMenuNames = mutableListOf<String>()
                            val dinnerMenuNames = mutableListOf<String>()

                            menus.menus.forEach { menu ->
                                when (menu.foodTime) {
                                    "중식" -> lunchMenuNames.addAll(menu.foods.map { it.foodName })
                                    "석식" -> dinnerMenuNames.addAll(menu.foods.map { it.foodName })
                                }
                            }

                            val lunchMenu1 = lunchMenuNames.joinToString("\n")
                            val dinnerMenu1 = dinnerMenuNames.joinToString("\n")
                            val breakfast = "식당 스낵바에\n샐러드&샌드위치\n준비되어있습니다.\n이틀 전에\n예약하신 후\n이용 가능합니다."

                            Log.d("HomeFrag_M", "Lunch: $lunchMenu1, Dinner: $dinnerMenu1")

                            binding?.apply {
                                breakfastMenu.text = breakfast
                                lunchMenu.text = lunchMenu1
                                dinnerMenu.text = dinnerMenu1
                            }
                        }
                    } else {
                        Log.e("HomeFrag_M", "Error body: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<CalendarDto>, t: Throwable) {
                    Log.e("HomeFrag_M", "Error fetching menu data", t)
                }
            })
        }
    }

    private fun getMealTicketCount(): Int {
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("MealTicket", 0)
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
                if (response.isSuccessful) {
                    response.body()?.let { notices ->
                        if (notices.isNotEmpty()) {
                            val notice1 = notices[0]
                            val notice2 = notices.getOrNull(1)
                            Log.d("HomeFragment", "notice: $notice1, $notice2")

                            binding?.apply {
                                noticeContent1.text = formatTitle(notice1.title)
                                noticePostedTime1.text = formatDate(notice1.createdAt)

                                notice2?.let {
                                    noticeContent2.text = formatTitle(it.title)
                                    noticePostedTime2.text = formatDate(it.createdAt)
                                }
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<NoticeBoardData>>, t: Throwable) {
                Log.e("HomeFragment", "Error occurred while fetching notice list", t)
            }
        })
    }

    private fun formatTitle(title: String): String {
        return if (title.length > 24) title.substring(0, 25) + " ..." else title
    }

    private fun formatDate(dateString: String?): String {
        if (dateString.isNullOrEmpty()) return ""
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MM월 dd일", Locale.getDefault())  // 변경된 부분
        return try {
            val parsedDate = inputFormat.parse(dateString)
            outputFormat.format(parsedDate)
        } catch (e: Exception) {
            Log.e("HomeFragment", "Error parsing date: $dateString", e)
            dateString
        }
    }

    private fun getJwtToken(): String {
        val sharedPref = requireContext().getSharedPreferences("auth", Context.MODE_PRIVATE)
        return sharedPref.getString("jwt_token", "") ?: ""
    }

    private fun getUserId(): Int {
        val sharedPref = requireContext().getSharedPreferences("auth", Context.MODE_PRIVATE)
        return sharedPref.getInt("userId", 0)
    }
}
