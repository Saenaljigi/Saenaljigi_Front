package com.example.saenaljigi_app.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.saenaljigi_app.R
import com.example.saenaljigi_app.databinding.FragmentMenuDetailBinding

/**
 * A simple [Fragment] subclass.
 * Use the [MenuDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MenuDetailFragment : Fragment() {
    private var _binding: FragmentMenuDetailBinding? = null
    private val binding get() = _binding!!

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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

        // 메뉴 리스트 설정
        val menuLists = listOf(
            listOf("밥", "밥", "밥"), // 조식 메뉴
            listOf("국", "국", "국"), // 중식 메뉴
            listOf("고기", "김치", "나물") // 석식 메뉴
        )

        // 어댑터 연결
        val adapter = MenuDetailAdapter(menuLists, binding.viewPager, binding.applyBtn)
        viewPager.adapter = adapter

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
}