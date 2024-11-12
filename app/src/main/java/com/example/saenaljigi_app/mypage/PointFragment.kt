package com.example.saenaljigi_app.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.saenaljigi_app.R
import com.example.saenaljigi_app.databinding.FragmentPointBinding

/**
 * A simple [Fragment] subclass.
 * Use the [PointFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PointFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentPointBinding? = null
    private val binding get() = _binding!!

    private lateinit var myPageHistoryAdapter: MyPageHistoryAdapter
    private lateinit var myPageHistoryData: ArrayList<MyPageHistoryData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPointBinding.inflate(inflater, container, false)
        val view = binding.root

        // 데이터 생성
        myPageHistoryData = arrayListOf(
            MyPageHistoryData("2024년 9월 20일", "상점", "+1점", "총 -3점"),
            MyPageHistoryData("2024년 9월 19일", "상점", "+1점", "총 -4점"),
            MyPageHistoryData("2024년 9월 19일", "벌점", "-2점", "총 -5점")
        )

        // 어댑터 초기화
        myPageHistoryAdapter = MyPageHistoryAdapter(myPageHistoryData)

        // RecyclerView에 어댑터와 레이아웃 매니저 연결
        binding.rcvPoint.apply {
            adapter = myPageHistoryAdapter
            layoutManager = LinearLayoutManager(context) // 레이아웃 매니저 설정
        }

        // 구분선 적용
        val dividerItemDecoration =
            DividerItemDecoration(binding.rcvPoint.context, LinearLayoutManager.VERTICAL)
        val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.mypage_divider)
        drawable?.let {
            dividerItemDecoration.setDrawable(it)
        }
        binding.rcvPoint.addItemDecoration(dividerItemDecoration)

        // back_btn 클릭 시 백스택으로 이동
        binding.backBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}