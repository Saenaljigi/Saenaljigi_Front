package com.example.saenaljigi_app.notice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.saenaljigi_app.databinding.FragmentNoticeBoardBinding
import java.util.ArrayList

class NoticeBoardFragment : Fragment() {

    private var _binding: FragmentNoticeBoardBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: NoticeBoardAdapter
    private lateinit var noticeList: ArrayList<NoticeBoardData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNoticeBoardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 가라데이터 추가
        noticeList = ArrayList()
        noticeList.add(NoticeBoardData("공지사항 1", "2024-09-30"))
        noticeList.add(NoticeBoardData("공지사항 2", "2024-09-29"))
        noticeList.add(NoticeBoardData("공지사항 3", "2024-09-28"))

        // 어댑터 설정
        adapter = NoticeBoardAdapter(noticeList) { id ->
            // 아이템 클릭 이벤트 처리
        }

        // 리사이클러뷰 설정
        binding.rvNoticeBoard.layoutManager = LinearLayoutManager(requireContext())
        binding.rvNoticeBoard.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
