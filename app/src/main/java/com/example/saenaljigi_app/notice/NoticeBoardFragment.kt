package com.example.saenaljigi_app.notice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.saenaljigi_app.databinding.FragmentNoticeBoardBinding

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

        // 제목과 날짜만 로드하여 간단한 목록을 생성
        noticeList = ArrayList()
        noticeList.add(NoticeBoardData(id = 1, title = "공지사항 1", created_at = "2024-09-30"))
        noticeList.add(NoticeBoardData(id = 2, title = "공지사항 2", created_at = "2024-09-29"))
        noticeList.add(NoticeBoardData(id = 3, title = "공지사항 3", created_at = "2024-09-28"))

        // 어댑터 설정
        adapter = NoticeBoardAdapter(noticeList) { id ->
            // 아이템 클릭 시 상세 데이터 로드
            loadNoticeDetail(id)
        }

        // 리사이클러뷰 설정
        binding.rvNoticeBoard.layoutManager = LinearLayoutManager(requireContext())
        binding.rvNoticeBoard.adapter = adapter
    }

    private fun loadNoticeDetail(id: Long) {
        // 여기서 서버나 데이터베이스에서 상세 데이터를 가져옵니다.
        // 예시: 서버 응답을 가정한 더미 데이터 추가
        val detailedData = NoticeBoardData(
            id = id,
            title = "공지사항 $id",
            created_at = "2024-09-30",
            content = "상세 내용입니다.",
            writer = "관리자",
            file = listOf(fileData(fileUrl = "https://example.com/file$id"))
        )

        // 가져온 상세 데이터를 활용하여 상세 페이지로 이동하거나 업데이트
        // 예를 들어, 상세 페이지로 이동하는 경우 Intent로 데이터를 전달할 수 있습니다.
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
