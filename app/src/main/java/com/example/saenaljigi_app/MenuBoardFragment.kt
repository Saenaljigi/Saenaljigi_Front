package com.example.saenaljigi_app

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.icu.util.Calendar
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.saenaljigi_app.databinding.FragmentMenuBoardBinding
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter
import com.prolificinteractive.materialcalendarview.format.MonthArrayTitleFormatter
import com.prolificinteractive.materialcalendarview.format.TitleFormatter

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

        // 월 변경 이벤트 리스너
        binding.calendarView.setOnMonthChangedListener { _, date ->
            binding.calendarView.removeDecorators()
            binding.calendarView.invalidateDecorators()
            selectedMonthDecorator = SelectedMonthDecorator(requireContext(), date.month)
            binding.calendarView.addDecorators(dayDecorator, todayDecorator, sundayDecorator, selectedMonthDecorator)
        }

        // 날짜 선택 이벤트 리스너
        binding.calendarView.setOnDateChangedListener { _, date, selected ->
            if (selected) {
                binding.todayApplyBtn.visibility = View.GONE
                val selectedDate = date.date.toString()

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

    // BackStack 변경 Listener 정의
    private val backStackListener = {
        updateTodayApplyBtnVisibility()
    }

    private fun updateTodayApplyBtnVisibility() {
        _binding?.let {
            it.todayApplyBtn.visibility =
                if (parentFragmentManager.backStackEntryCount == 0) View.VISIBLE else View.GONE
        }
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
        private val drawable = ContextCompat.getDrawable(context, R.drawable.calendar_circle_gray)
        private var date = CalendarDay.today()
        override fun shouldDecorate(day: CalendarDay?): Boolean = day == date
        override fun decorate(view: DayViewFacade?) {
            view?.setBackgroundDrawable(drawable!!)
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

    /* 일요일 날짜의 색상을 설정하는 클래스 */
    private class SundayDecorator(private val context: Context) : DayViewDecorator {
        private val calendar: Calendar = Calendar.getInstance()

        override fun shouldDecorate(day: CalendarDay): Boolean {
            calendar.set(day.year, day.month - 1, day.day)
            return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
        }

        override fun decorate(view: DayViewFacade) {
            view.addSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.red_sun)))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
