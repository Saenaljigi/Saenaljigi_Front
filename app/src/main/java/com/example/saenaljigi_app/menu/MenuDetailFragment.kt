package com.example.saenaljigi_app.menu

import MenuDetailAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.saenaljigi_app.R
import com.example.saenaljigi_app.RetrofitClient
import com.example.saenaljigi_app.UserDTO
import com.example.saenaljigi_app.databinding.FragmentMenuDetailBinding
import java.time.LocalDate
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuDetailFragment : Fragment() {
    private var _binding: FragmentMenuDetailBinding? = null
    private val binding get() = _binding!!

    // selectedDate를 저장할 변수
    private var selectedDateString: String? = null

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

        // selectedDate 값 받아 LocalDate 타입으로 변경
        selectedDateString = arguments?.getString("selectedDate")
        Log.d("MenuDetailFragment", "Received Date: $selectedDateString")

        val selectedDate: LocalDate = selectedDateString.let {
            LocalDate.parse(it)
        }

        // 선택된 날짜의 메뉴 불러오기
        fetchMenu(selectedDate)

        // 배경 클릭 시 프래그먼트 종료
        binding.root.setOnClickListener {
            parentFragmentManager.popBackStack()  // BackStack에서 현재 Fragment를 제거
        }

        // ViewPager 설정
        val viewPager = binding.viewPager
        viewPager.clipToPadding = false
        viewPager.clipChildren = false
        viewPager.offscreenPageLimit = 1

        // 페이지 간 마진 및 스케일 조정
        viewPager.setPageTransformer { page, position ->
            // 페이지 간의 간격
            val pageMarginPx = resources.getDimensionPixelOffset(R.dimen.view_page_margin)
            // 양옆 페이지가 보이는 크기
            val pageOffsetPx = resources.getDimensionPixelOffset(R.dimen.view_page_margin_next)

            // 페이지 간 마진 및 오프셋 설정
            val offset = position * -(2 * pageOffsetPx + pageMarginPx)
            page.translationX = offset

            // 20dp를 픽셀로 변환하여 y축 이동
            val translationYFactor =
                resources.getDimensionPixelOffset(R.dimen.view_page_translation_y)
            val translationY = translationYFactor * Math.abs(position)
            page.translationY = if (position != 0f) translationY.toFloat() else 0f

            // 중앙으로 올수록 투명도가 증가하며, 양옆으로 갈수록 투명도가 감소
            val minAlpha = 0.2f // 양옆 페이지의 최소 투명도
            val alpha = Math.max(minAlpha, 1 - Math.abs(position)) // 중앙으로 갈수록 밝아짐
            page.alpha = alpha
        }

        // onPageChange를 ViewPager2에 연결
        viewPager.registerOnPageChangeCallback(onPageChange)
    }

    // 페이지 변화 콜백
    private val onPageChange = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            if (position == 0) {
                // 조식 페이지에서만 신청하기 버튼을 보이게 함
                binding.applyBtn.visibility = View.VISIBLE
            } else {
                // 그 외 페이지에서는 신청하기 버튼 숨김
                binding.applyBtn.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        apiCall?.cancel()
    }

    // 메뉴 불러오기
    private fun fetchMenu(selectedDate: LocalDate) {
        val token = ""

        val menuService = RetrofitClient.instance.create(MenuApiService::class.java)
        val formattedDate = selectedDate.toString() // String 형태로 포맷
        Log.d("MenuDetail_R", "$formattedDate")
        apiCall = menuService.getMenu(token, formattedDate)

        apiCall?.enqueue(object : Callback<CalendarDto> {
            override fun onResponse(call: Call<CalendarDto>, response: Response<CalendarDto>) {
                if (isAdded) {
                    if (response.isSuccessful) {
                        response.body()?.let { calendarDto ->
                            Log.d("MenuDetail_R", "$calendarDto")

                            // 메뉴 리스트를 분할해 저장
                            val menuList = calendarDto.menus
                            val lunchMenu = menuList[1]
                            val dinnerMenu = menuList[0]

                            Log.d("MenuDetail_R", "Menu List: $menuList")

                            // 어댑터 연결
                            val adapter = MenuDetailAdapter(lunchMenu, dinnerMenu, binding.viewPager)
                            binding.viewPager.adapter = adapter

                        } ?: run {
                            val errorMessage = "메뉴 데이터를 불러오는 데 실패했습니다. 다시 시도해주세요."
                            Log.e("MenuDetail_R", "Response body is null")
                            showErrorAndExit(errorMessage)
                        }

                    } else {
                        val errorMessage = "서버에서 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
                        Log.e("MenuDetail_R", "Error: ${response.errorBody()?.string()}")
                        showErrorAndExit(errorMessage)
                    }
                }
            }

            override fun onFailure(call: Call<CalendarDto>, t: Throwable) {
                if (isAdded) {
                    val errorMessage = "네트워크 연결에 문제가 발생했습니다. 인터넷 상태를 확인해주세요."
                    Log.e("MenuDetail_R", "Error: ${t.message}")
                    showErrorAndExit(errorMessage)
                }
            }
        })
    }


    private fun showErrorAndExit(message: String) {
        if (!isAdded) return // 프래그먼트가 Activity에 연결되지 않은 경우 아무 작업도 수행하지 않음
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        parentFragmentManager.popBackStack() // 프래그먼트 종료
    }
}
