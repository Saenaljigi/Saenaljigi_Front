package com.example.saenaljigi_app.notice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.saenaljigi_app.R
import com.example.saenaljigi_app.databinding.FragmentNoticeBoardBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NoticeBoardFragment : Fragment() {

    private var _binding: FragmentNoticeBoardBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: NoticeBoardAdapter
    private lateinit var noticeList: ArrayList<NoticeBoardData>

    private val useDummyData = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNoticeBoardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noticeList = if (useDummyData) {
            loadDummyData()
        } else {
            ArrayList()
        }

        adapter = NoticeBoardAdapter(noticeList) { id ->
            loadNoticeDetail(id)
        }

        binding.rvNoticeBoard.layoutManager = LinearLayoutManager(requireContext())
        binding.rvNoticeBoard.adapter = adapter

        if (!useDummyData) {
            loadNoticesFromServer()
        }
    }

    private fun loadDummyData(): ArrayList<NoticeBoardData> {
        return arrayListOf(
            NoticeBoardData(id = 1, title = "공지사항 1", created_at = "2024-09-30"),
            NoticeBoardData(id = 2, title = "공지사항 2", created_at = "2024-09-29"),
            NoticeBoardData(id = 3, title = "공지사항 3", created_at = "2024-09-28")
        )
    }

    private fun loadNoticeDetail(id: Long) {
        // Retrofit API 사용 (주석 해제하여 사용)
        /*
        val service = RetrofitInstance.create(NoticeBoardService::class.java)
        service.getNoticeSeq("token").enqueue(object : Callback<NoticeBoardData> {
            override fun onResponse(call: Call<NoticeBoardData>, response: Response<NoticeBoardData>) {
                response.body()?.let { detailedData ->
                    openDetailFragment(detailedData)
                }
            }

            override fun onFailure(call: Call<NoticeBoardData>, t: Throwable) {
                // 에러 처리
            }
        })
        */

        // 가라 데이터로 상세 화면 로드
        val detailedData = NoticeBoardData(
            id = id,
            title = "[생활지도실] 2024-2학기 1차 생활점검 실시 안내 $id",
            created_at = "2024-09-30",
            content = "새관 행복기숙사 전체 생활점검을 아래와 같이 실시하오니, 기숙사생들께서는 반드시 이 점 숙지하시고 생활점검에 반드시 참여해주시기 바랍니다.\\n\\n- 아 래 -\\n\\n1. 일 시: 2024년 09월 26일(목) 저녁 18시 00분 부터 실시\\n2. 점검 시간:\\n점검시간\\n09월 26일 (목)\\n남학생\\n여학생\\n18:00PM\\n4, 5, 6, 7, 8층\\n3, 9, 10, 11, 12, 13층\\n※ 점검시간에 따라 상기 층별 점검시간이 다소 지체될 수 있음.\\n\\n3. 생활점검 내용\\n(1) 호실 내 외부인 투숙, 흡연, 음주, 주류 반입 및 허가되지 않은 물품(취사도구, 전열기구)의 사용 금지.\\n(2) 호실 내 정리정돈 및 청소상태 점검 (호실 출입구 주변, 화장실 포함)\\n\\n※ 호실 내 사생이 없더라도 사감 및 조교가 입실하여 호실 점검 후, 호실 내 상태에 따라 벌점 부과.".replace("\\n", "\n"),
            writer = "관리자",
            files = listOf(FileData(fileName = "파일$id", fileUrl = "https://example.com/file$id"))
        )

        openDetailFragment(detailedData)
    }

    private fun openDetailFragment(detailedData: NoticeBoardData) {
        val fragment = NoticeDetailFragment().apply {
            arguments = Bundle().apply {
                putString("title", detailedData.title)
                putString("content", detailedData.content)
                putString("writer", detailedData.writer)
                putString("created_at", detailedData.created_at)
            }
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun loadNoticesFromServer() {
        // Retrofit API 사용 (주석 해제하여 사용)
        /*
        val service = RetrofitInstance.create(NoticeBoardService::class.java)
        service.getNotice("token").enqueue(object : Callback<List<NoticeBoardData>> {
            override fun onResponse(call: Call<List<NoticeBoardData>>, response: Response<List<NoticeBoardData>>) {
                response.body()?.let { notices ->
                    noticeList.clear()
                    noticeList.addAll(notices)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<List<NoticeBoardData>>, t: Throwable) {
                // 에러 처리
            }
        })
        */
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
