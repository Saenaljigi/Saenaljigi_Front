package com.example.saenaljigi_app.menu

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.saenaljigi_app.R
import com.example.saenaljigi_app.RetrofitClient
import com.example.saenaljigi_app.databinding.FragmentMenuDetailBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate

class MenuDetailFragment : Fragment() {
    private var _binding: FragmentMenuDetailBinding? = null
    private val binding get() = _binding!!

    private var selectedDateString: String? = null
    private var calendarId: Long? = null
    private var apiCall: Call<CalendarDto>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 배경 클릭 시 프래그먼트 종료
        binding.root.setOnClickListener {
            // 현재 프래그먼트를 백스택에서 제거
            parentFragmentManager.popBackStack()

            // 새로 MenuDetailFragment를 시작
            val newFragment = MenuBoardFragment()

            // 새 프래그먼트를 추가하고 트랜잭션을 커밋
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, newFragment) // 또는 add() 사용 가능
                .commit()
        }

        // 전달받은 날짜 데이터를 가져옴
        selectedDateString = arguments?.getString("selectedDate")
        Log.d("MenuDetailFragment", "Received Date: $selectedDateString")

        val selectedDate: LocalDate = LocalDate.parse(selectedDateString)
        fetchMenu(selectedDate)

        setupViewPager()
    }

    private fun setupViewPager() {
        val viewPager = binding.viewPager
        viewPager.clipToPadding = false
        viewPager.clipChildren = false
        viewPager.offscreenPageLimit = 1

        // ViewPager2의 페이지 전환 애니메이션 설정
        viewPager.setPageTransformer { page, position ->
            val offset = position * -(2 * resources.getDimensionPixelOffset(R.dimen.view_page_margin) +
                    resources.getDimensionPixelOffset(R.dimen.view_page_margin_next))
            page.translationX = offset

            val translationYFactor = resources.getDimensionPixelOffset(R.dimen.view_page_translation_y)
            val translationY = translationYFactor * Math.abs(position)
            page.translationY = if (position != 0f) translationY.toFloat() else 0f

            val alpha = Math.max(0.2f, 1 - Math.abs(position))
            page.alpha = alpha
        }

        // 페이지 변경 이벤트 처리
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Log.d("MenuDetail", "currentPosition: $position")
                // 버튼 상태 업데이트
                updateApplyButton(position)
            }
        })
    }

    private fun updateApplyButton(position: Int) {
        val date = selectedDateString ?: return
        val foodType = when (position) {
            0 -> "조식"
            1 -> "중식"
            2 -> "석식"
            else -> return
        }

        // 로컬 저장소에서 데이터 가져오기
        val savedMeals = getSavedMealsFromPreferences(requireContext())
        val isMealSaved = savedMeals.any { it["date"] == date && it["foodType"] == foodType }

        val viewPager = binding.viewPager

        // 버튼 상태 설정
        binding.applyBtn.apply {
            if (isMealSaved) {
                // 이미 신청된 상태
                setBackgroundResource(R.drawable.apply_btn_background)
                isClickable = false
                text = if (position == 0) "신청 완료" else "기록 완료"
            } else {
                // 신청 가능 상태
                setBackgroundResource(R.drawable.menu_btn)
                isClickable = true
                text = if (position == 0) "신청하기" else "식사 기록하기"
                setOnClickListener {
                    // 클릭 시 로컬 저장 및 상태 업데이트
                    saveMealToPreferences(requireContext(), date, foodType)

                    // SharedPreferences에서 MealTicket 값을 가져오고, 없으면 0으로 초기화
                    val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                    val savedMealsCount = sharedPreferences.getInt("MealTicket", 0)  // 기본값 0

                    // 값을 하나씩 증가시킴
                    val newMealsCount = savedMealsCount + 1

                    // SharedPreferences에 새 값 저장
                    val editor = sharedPreferences.edit()
                    editor.putInt("MealTicket", newMealsCount)
                    editor.apply()  // 비동기적으로 저장


                    editor.apply()

                    updateApplyButton(position) // 위치 기반으로 상태 갱신
                    if (position == 0) {
                        applyForBreakfast()
                    }
                }
            }
        }

    }

    private fun fetchMenu(selectedDate: LocalDate) {
        val token = getJwtToken()
        val userId = getUserId()

        // 메뉴 데이터를 가져오기 위한 API 호출
        val menuService = RetrofitClient.instance.create(MenuApiService::class.java)
        apiCall = menuService.getMenu(token, selectedDate.toString(), userId)

        apiCall?.enqueue(object : Callback<CalendarDto> {
            override fun onResponse(call: Call<CalendarDto>, response: Response<CalendarDto>) {
                if (!isAdded) return
                if (response.isSuccessful) {
                    response.body()?.let { calendarDto ->
                        val breakfast = listOf(
                            "식당 스낵바에", "샐러드&샌드위치", "준비되어있습니다.",
                            "이틀 전에 예약하신 후", "이용 가능합니다."
                        )
                        val lunchMenu = calendarDto.menus.getOrNull(1)
                        val dinnerMenu = calendarDto.menus.getOrNull(0)

                        if (lunchMenu != null && dinnerMenu != null) {
                            val adapter = MenuDetailAdapter(
                                breakfast,
                                lunchMenu,
                                dinnerMenu,
                                calendarDto,
                                binding.viewPager,
                                requireContext()
                            ) { position ->
                            }

                            binding.viewPager.adapter = adapter
                            calendarId = calendarDto.id
                        }
                    } ?: showErrorAndExit("메뉴 데이터를 불러오는 데 실패했습니다.")
                } else {
                    showErrorAndExit("서버에서 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
                }
            }

            override fun onFailure(call: Call<CalendarDto>, t: Throwable) {
                if (isAdded) {
                    showErrorAndExit("네트워크 연결에 문제가 발생했습니다.")
                }
            }
        })
    }

    private fun applyForBreakfast() {
        val token = getJwtToken()
        val calendarId = calendarId ?: return

        // 조식 신청 API 호출
        val menuService = RetrofitClient.instance.create(MenuApiService::class.java)
        menuService.applyForBreakfast(token, calendarId, true)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "조식 신청이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "조식 신청에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(requireContext(), "조식 신청 요청 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun saveMealToPreferences(context: Context, date: String, foodType: String) {
        // 로컬 저장소에 데이터를 저장
        val sharedPreferences = context.getSharedPreferences("MenuPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val currentData = getSavedMealsFromPreferences(context)
        val newMeal = mapOf("date" to date, "foodType" to foodType)
        val updatedList = currentData.toMutableList().apply { add(newMeal) }
        val json = Gson().toJson(updatedList)
        editor.putString("mealDataList", json)
        editor.apply()
    }

    private fun getSavedMealsFromPreferences(context: Context): List<Map<String, String>> {
        // 로컬 저장소에서 데이터 읽기
        val sharedPreferences = context.getSharedPreferences("MenuPrefs", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("mealDataList", "") ?: ""
        val listType = object : TypeToken<List<Map<String, String>>>() {}.type
        return Gson().fromJson(json, listType) ?: emptyList()
    }

    private fun getJwtToken(): String {
        // JWT 토큰 가져오기
        val sharedPref = requireContext().getSharedPreferences("auth", Context.MODE_PRIVATE)
        return sharedPref.getString("jwt_token", "") ?: ""
    }

    private fun getUserId(): Int {
        // 사용자 ID 가져오기
        val sharedPref = requireContext().getSharedPreferences("auth", Context.MODE_PRIVATE)
        return sharedPref.getInt("userId", 0)
    }

    private fun showErrorAndExit(message: String) {
        // 에러 메시지 출력 후 뒤로 이동
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        parentFragmentManager.popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        apiCall?.cancel()
    }
}
