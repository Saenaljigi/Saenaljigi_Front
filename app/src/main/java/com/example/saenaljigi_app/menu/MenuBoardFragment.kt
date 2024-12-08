package com.example.saenaljigi_app.menu

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.icu.util.Calendar
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.text.style.LineBackgroundSpan
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.saenaljigi_app.R
import com.example.saenaljigi_app.RetrofitClient
import com.example.saenaljigi_app.databinding.FragmentMenuBoardBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter
import com.prolificinteractive.materialcalendarview.format.MonthArrayTitleFormatter
import com.prolificinteractive.materialcalendarview.format.TitleFormatter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MenuBoardFragment : Fragment() {

    private var _binding: FragmentMenuBoardBinding? = null
    private val binding get() = _binding!!
    private lateinit var calendarView: MaterialCalendarView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBoardBinding.inflate(inflater, container, false)

        // 월 / 요일을 한글로 보이게 설정
        binding.calendarView.setTitleFormatter(
            MonthArrayTitleFormatter(resources.getTextArray(R.array.custom_months))
        )
        binding.calendarView.setWeekDayFormatter(
            ArrayWeekDayFormatter(resources.getTextArray(R.array.custom_weekdays))
        )

        // 좌우 화살표 사이 연, 월의 폰트 스타일 설정
        binding.calendarView.setHeaderTextAppearance(R.style.CalendarWidgetHeader)

        // 달력 선택 범위 이벤트 설정
        binding.calendarView.setOnRangeSelectedListener { _, dates ->
            val startDay = dates.first().date.toString()
            val endDay = dates.last().date.toString()
        }

        // 오늘 날짜 가져오기
        val today = CalendarDay.today()

        // 오늘 날짜를 하얀색으로 표시하는 데코레이터 추가
        binding.calendarView.addDecorator(object : DayViewDecorator {
            override fun shouldDecorate(day: CalendarDay): Boolean {
                return day == today // 오늘 날짜만 데코레이터 적용
            }

            override fun decorate(view: DayViewFacade) {
                view.addSpan(object : ForegroundColorSpan(Color.WHITE) {}) // 글씨 색을 하얀색으로 변경
            }
        })

        // Decorators 설정
        val dayDecorator = DayDecorator(requireContext())
        val todayDecorator = TodayDecorator(requireContext())
        val sundayDecorator = SundayDecorator(requireContext())
        var selectedMonthDecorator = SelectedMonthDecorator(requireContext(), CalendarDay.today().month)

        binding.calendarView.addDecorators(dayDecorator, todayDecorator, sundayDecorator, selectedMonthDecorator)

        binding.calendarView.setTitleFormatter(object : TitleFormatter {
            override fun format(day: CalendarDay?): CharSequence {
                return "${day?.year}년 ${day?.month}월"
            }
        })

        // 현재 달로 초기값 지정
        var selectedMonth: Int = CalendarDay.today().month

        // 이 달의 하이라이트된 날 표시하기
        //fetchHighlightedDate(selectedMonth)

        // 월 변경 이벤트 리스너
        binding.calendarView.setOnMonthChangedListener { _, date ->
            selectedMonth = date.month
            Log.d("Calendar", "Month: $selectedMonth")
            binding.calendarView.removeDecorators()
            binding.calendarView.invalidateDecorators()
            selectedMonthDecorator = SelectedMonthDecorator(requireContext(), date.month)
            binding.calendarView.addDecorators(dayDecorator, todayDecorator, sundayDecorator, selectedMonthDecorator)
            // 달이 변경될 때마다 그 달의 하이라이트된 날 받아오기
            //fetchHighlightedDate(selectedMonth)
        }

        // 날짜 선택 이벤트 리스너
        binding.calendarView.setOnDateChangedListener { _, date, selected ->
            if (selected) {
                binding.todayApplyBtn.visibility = View.GONE
                val selectedDate = date.date.toString()
                Log.d("Calendar", "Date:$selectedDate")

                // MenuDetailFragment로 전환
                val fragment = MenuDetailFragment().apply {
                    arguments = Bundle().apply { putString("selectedDate", selectedDate) }
                }

                parentFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }

        fetchHighlightedDate(selectedMonth)

        applyForBreakfast(today.date.toString())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // BackStack이 변경될 때 호출되는 Listener 추가
        parentFragmentManager.addOnBackStackChangedListener(backStackListener)
        updateTodayApplyBtnVisibility()
    }

    override fun onPause() {
        super.onPause()
        // BackStack Listener를 제거하여 메모리 누수 방지
        parentFragmentManager.removeOnBackStackChangedListener(backStackListener)
    }

    override fun onStart() {
        super.onStart()
        updateTodayApplyBtnVisibility()
    }

    override fun onResume() {
        super.onResume()
        updateTodayApplyBtnVisibility()  // 버튼 상태 갱신
    }

    // BackStack 변경 Listener 정의
    private val backStackListener = {
        updateTodayApplyBtnVisibility()
    }

    private fun updateTodayApplyBtnVisibility() {
        binding.todayApplyBtn.visibility = View.VISIBLE
    }

    /* 선택된 날짜의 background를 설정하는 클래스 */
    private inner class DayDecorator(context: Context) : DayViewDecorator {
        private val drawable = ContextCompat.getDrawable(context, R.drawable.calendar_selector)
        override fun shouldDecorate(day: CalendarDay): Boolean = true
        override fun decorate(view: DayViewFacade) {
            view.setSelectionDrawable(drawable!!)
        }
    }

    /* 오늘 날짜의 background를 설정하는 클래스 */
    private class TodayDecorator(context: Context) : DayViewDecorator {
        private val date = CalendarDay.today()
        private val whiteTextSpan = ForegroundColorSpan(Color.WHITE)
        private val customBackgroundSpan = CustomBackgroundSpan(context, Color.parseColor("#45565E"))

        override fun shouldDecorate(day: CalendarDay?): Boolean = day == date

        override fun decorate(view: DayViewFacade?) {
            view?.addSpan(whiteTextSpan) // 오늘 날짜 글씨를 하얀색으로
            view?.addSpan(customBackgroundSpan) // 오늘 날짜 배경 커스텀 설정
        }
    }

    /* 일요일 날짜의 색상을 설정하는 클래스 */
    private class SundayDecorator(private val context: Context) : DayViewDecorator {
        private val calendar: Calendar = Calendar.getInstance()
        private val redTextSpan = ForegroundColorSpan(ContextCompat.getColor(context, R.color.red_sun))
        private val today = CalendarDay.today() // 오늘 날짜 가져오기

        override fun shouldDecorate(day: CalendarDay): Boolean {
            // 오늘 날짜는 데코레이터 적용 제외
            calendar.set(day.year, day.month - 1, day.day)
            return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY && day != today
        }

        override fun decorate(view: DayViewFacade) {
            view.addSpan(redTextSpan) // 일요일 글씨를 빨간색으로 설정
        }
    }


    /* 이번달에 속하지 않지만 캘린더에 보여지는 이전달/다음달의 일부 날짜를 설정하는 클래스 */
    private inner class SelectedMonthDecorator(
        private val context: Context,
        val selectedMonth: Int
    ) : DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean = day.month != selectedMonth
        override fun decorate(view: DayViewFacade) {
            view.addSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.gray)))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    // 오늘 날짜의 배경 설정
    class CustomBackgroundSpan(context: Context, private val color: Int) : LineBackgroundSpan {
        private val radius: Float

        init {
            // 24dp를 픽셀로 변환하여 반지름으로 사용
            radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25f, context.resources.displayMetrics) / 2
        }

        override fun drawBackground(
            canvas: Canvas, paint: Paint,
            left: Int, right: Int,
            top: Int, baseline: Int,
            bottom: Int, text: CharSequence,
            start: Int, end: Int, lineNum: Int
        ) {
            val oldColor = paint.color
            paint.color = color

            // 원의 중심 좌표 계산
            val cx = (left + right) / 2f
            val cy = (top + bottom) / 2f

            // 원 그리기
            canvas.drawCircle(cx, cy, radius, paint)
            paint.color = oldColor
        }
    }

    // 하이라이트된 날짜 받아와 표시하기
    private fun fetchHighlightedDate(selectedMonth: Int) {
        val token = getJwtToken()
        val userId = getUserId()
        Log.d("MenuBoard", "userId: $userId")

        val menuService = RetrofitClient.instance.create(MenuApiService::class.java)
        val call = menuService.getAllDay(token, userId)

        call.enqueue(object : Callback<List<CalendarDto>> {
            override fun onResponse(call: Call<List<CalendarDto>>, response: Response<List<CalendarDto>>) {
                if (response.isSuccessful) {
                    response.body()?.let { days ->
                        // 하이라이트 여부가 true인 날들의 날짜만 저장
                        val highlightedDayList = days.filter { it.isHilight == true }.map { it.day }

                        // String 리스트를 LocalDate로 변환
                        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd") // 날짜 포맷에 맞게 수정
                        val localDateList = highlightedDayList.mapNotNull { dateString ->
                            try {
                                LocalDate.parse(dateString, dateFormatter)
                            } catch (e: Exception) {
                                Log.e("MenuBoard", "Invalid date format: $dateString")
                                null
                            }
                        }

                        Log.d("MenuBoard", "LocalDate list: $localDateList")

                        // HighlightedDayDecorator에 LocalDate 리스트 전달
                        binding.calendarView.addDecorators(HighlightedDayDecorator(localDateList))

                    } ?: Log.e("MenuBoard", "Response body is null")
                } else {
                    Log.e("MenuBoard", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<CalendarDto>>, t: Throwable) {
                Log.e("MenuBoard", "Error: ${t.message}")
            }
        })
    }

    // 하이라이트된 날짜에 동그라미를 표시하는 데코레이터 클래스 정의
    private inner class HighlightedDayDecorator(private val dates: List<LocalDate>) : DayViewDecorator {
        private val paint = Paint().apply {
            color = Color.parseColor("#FF0707")
            style = Paint.Style.FILL
        }

        override fun shouldDecorate(day: CalendarDay): Boolean {
            // CalendarDay를 LocalDate로 변환하여 비교
            val localDate = LocalDate.of(day.year, day.month, day.day)
            return dates.contains(localDate)
        }

        override fun decorate(view: DayViewFacade) {
            view.addSpan(object : LineBackgroundSpan {
                override fun drawBackground(
                    canvas: Canvas, paint: Paint,
                    left: Int, right: Int,
                    top: Int, baseline: Int,
                    bottom: Int, text: CharSequence,
                    start: Int, end: Int, lineNum: Int
                ) {
                    val radius = 10f // 원의 반지름 설정
                    val cx = (left + right) / 2f
                    val cy = (top + bottom) / 2f + 51f
                    canvas.drawCircle(cx, cy, radius, this@HighlightedDayDecorator.paint)
                }
            })
        }
    }

    private fun applyForBreakfast(date: String) {
        // 로컬 저장소에서 데이터 가져오기
        val savedMeals = getSavedMealsFromPreferences(requireContext())
        val isMealSaved = savedMeals.any { it["date"] == date && it["foodType"] == "조식" }

        // 버튼 상태 설정
        binding.todayApplyBtn.apply {
            if (isMealSaved) {
                // 이미 신청된 상태
                setBackgroundResource(R.drawable.apply_btn_background)
                isClickable = false
                text = "오늘의 조식 신청 완료"
            } else {
                // 신청 가능 상태
                setBackgroundResource(R.drawable.menu_btn)
                isClickable = true
                text = "오늘의 조식 신청하기"
                setOnClickListener {
                    setBackgroundResource(R.drawable.apply_btn_background)
                    text = "오늘의 조식 신청 완료"

                    // 클릭 시 로컬 저장 및 상태 업데이트
                    saveMealToPreferences(requireContext(), date, "조식")

                    // SharedPreferences에서 MealTicket 값을 가져오고, 없으면 0으로 초기화
                    val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                    val savedMealsCount = sharedPreferences.getInt("MealTicket", 0)  // 기본값 0

                    // 값을 하나씩 증가시킴
                    val newMealsCount = savedMealsCount + 1

                    // SharedPreferences에 새 값 저장
                    val editor = sharedPreferences.edit()
                    editor.putInt("MealTicket", newMealsCount)
                    editor.apply()  // 비동기적으로 저장
                }
            }
        }

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
        val sharedPref = requireContext().getSharedPreferences("auth", Context.MODE_PRIVATE)
        val token = sharedPref.getString("jwt_token", "") ?: ""
        Log.d("MenuBoardFrag", "Retrieved JWT token: $token")
        return token
    }

    private fun getUserId(): Int {
        val sharedPref = requireContext().getSharedPreferences("auth", Context.MODE_PRIVATE)
        val userId = sharedPref.getInt("userId", 0) // 기본값 0
        Log.d("UserId", "UserId: $userId")  // 로그 출력
        return userId
    }
}
