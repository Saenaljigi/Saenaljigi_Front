package com.example.saenaljigi_app.mypage

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.saenaljigi_app.R
import com.example.saenaljigi_app.databinding.FragmentMealTicketBinding

/**
 * A simple [Fragment] subclass.
 * Use the [MealTicketFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MealTicketFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentMealTicketBinding? = null
    private val binding get() = _binding!!

    private lateinit var myPageHistoryAdapter: MyPageHistoryAdapter
    private lateinit var myPageHistoryData: ArrayList<MyPageHistoryData>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMealTicketBinding.inflate(inflater, container, false)
        val view = binding.root

        // 데이터 생성
        myPageHistoryData = arrayListOf(
            MyPageHistoryData("2024년 9월 20일", "석식", "+1장", "잔여 식권  109장"),
            MyPageHistoryData("2024년 9월 19일", "중식", "+1장", "잔여 식권  110장"),
            MyPageHistoryData("2024년 9월 19일", "석식", "+1장", "잔여 식권  111장")
        )

        // 어댑터 초기화
        myPageHistoryAdapter = MyPageHistoryAdapter(myPageHistoryData)

        // RecyclerView에 어댑터와 레이아웃 매니저 연결
        binding.rcvMealTicket.apply {
            adapter = myPageHistoryAdapter
            layoutManager = LinearLayoutManager(context)
        }

        val recyclerView = binding.rcvMealTicket
        recyclerView.layoutManager = LinearLayoutManager(context)

        // 커스텀 구분선 적용
        val customItemDecoration = CustomDividerItemDecoration(R.drawable.mypage_divider)
        binding.rcvMealTicket.addItemDecoration(customItemDecoration)

        // back_btn 클릭 시 백스택으로 이동
        binding.backBtn.setOnClickListener {
            // 백스택에서 이전 프래그먼트로 이동
            parentFragmentManager.popBackStack()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}