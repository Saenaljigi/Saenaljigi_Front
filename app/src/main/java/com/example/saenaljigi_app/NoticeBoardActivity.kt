package com.example.saenaljigi_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.saenaljigi_app.databinding.ActivityNoticeBoardBinding
import java.util.ArrayList

class NoticeBoardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNoticeBoardBinding
    private lateinit var adapter: NoticeBoardAdapter
    private lateinit var noticeList: ArrayList<NoticeBoardData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoticeBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        binding.rvNoticeBoard.layoutManager = LinearLayoutManager(this)
        binding.rvNoticeBoard.adapter = adapter
    }
}
