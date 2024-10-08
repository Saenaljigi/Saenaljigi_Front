package com.example.saenaljigi_app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.saenaljigi_app.databinding.FragmentMealTicketBinding
import com.example.saenaljigi_app.databinding.FragmentPointBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

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
            layoutManager = LinearLayoutManager(context)
        }

        val recyclerView = binding.rcvPoint
        recyclerView.layoutManager = LinearLayoutManager(context)

        // 구분선 적용
        val dividerItemDecoration = DividerItemDecoration(recyclerView.context, LinearLayoutManager.VERTICAL)
        val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.mypage_divider)
        drawable?.let {
            dividerItemDecoration.setDrawable(it)
        }

        recyclerView.addItemDecoration(dividerItemDecoration)

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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PointFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PointFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}