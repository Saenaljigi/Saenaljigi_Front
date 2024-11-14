package com.example.saenaljigi_app.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.util.Log
import android.view.Menu
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
            val translationYFactor = resources.getDimensionPixelOffset(R.dimen.view_page_translation_y)
            val translationY = translationYFactor * Math.abs(position)
            page.translationY = if (position != 0f) translationY.toFloat() else 0f

            // 중앙으로 올수록 투명도가 증가하며, 양옆으로 갈수록 투명도가 감소
            val minAlpha = 0.2f // 양옆 페이지의 최소 투명도
            val alpha = Math.max(minAlpha, 1 - Math.abs(position)) // 중앙으로 갈수록 밝아짐
            page.alpha = alpha
        }

        // foodName 리스트로 생성하여 lunch_menu와 dinner_menu에 저장
        val breakfast_menu: List<String> = listOf(
            "식당 스낵바에",
            "샐러드&샌드위치",
            "준비되어 있습니다",
            "이틀 전에 예약하신 후",
            "이용 가능합니다."
        )
        val lunch_menu = listOf(
            "밥",
            "국",
            "고기",
            "김치"
        )

        val dinner_menu = listOf(
            "밥",
            "국",
            "빵"
        )

        // 어댑터 연결
        val adapter = MenuDetailAdapter(listOf(breakfast_menu, lunch_menu, dinner_menu), binding.viewPager, binding.applyBtn)
        binding.viewPager.adapter = adapter

        // ViewPager에 페이지 변화 리스너 추가
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
    }

    // 메뉴 불러오기
    private fun fetchMenu(selectedDate: LocalDate) {
        val menuService = RetrofitClient.instance.create(MenuApiService::class.java)
        val formattedDate = selectedDate.toString()
        val call = menuService.getMenu(formattedDate)

        call.enqueue(object : Callback<CalendarDto> {
            override fun onResponse(call: Call<CalendarDto>, response: Response<CalendarDto?>) {
                if (response.isSuccessful) {
                    response.body()?.let { menus ->
                        Log.d("MenuDetail_R", "$menus")

                        // 각 메뉴의 foodName만 추출하여 리스트 생성
                        val menuNames = menus.menus.take(2).map { menu ->
                            menu.foods.map { it.foodName }
                        }

                        // 어댑터 연결
                        val adapter = MenuDetailAdapter(menuNames, binding.viewPager, binding.applyBtn)
                        binding.viewPager.adapter = adapter
                    } ?: Log.e("MenuDetail_R", "Response body is null")
                } else {
                    Log.e("MenuDetail_R", "Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<CalendarDto>, t: Throwable) {
                Log.e("MenuDetail_R", "Error: ${t.message}")
            }
        })
    }

    // 하이라이트된 메뉴 서버로 보내기
    private fun updatedHighlightedMenu(userDTO: UserDTO, selectedDate: LocalDate, menu: Menu) {

    }
}
