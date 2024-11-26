package com.example.saenaljigi_app.notice

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.saenaljigi_app.R
import com.example.saenaljigi_app.RetrofitClient
import com.example.saenaljigi_app.databinding.FragmentNoticeBoardBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NoticeBoardFragment : Fragment() {

    private var _binding: FragmentNoticeBoardBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: NoticeBoardAdapter
    private lateinit var noticeList: ArrayList<NoticeBoardData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoticeBoardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noticeList = ArrayList()

        adapter = NoticeBoardAdapter(noticeList) { id ->
            loadNoticeDetail(id)
        }

        binding.rvNoticeBoard.layoutManager = LinearLayoutManager(requireContext())
        binding.rvNoticeBoard.adapter = adapter

        // 서버에서 공지사항 목록 가져오기
        loadNoticesFromServer()
    }

    private fun loadNoticeDetail(noticeId: Long) {
        val token = getJwtToken()
        val service = RetrofitClient.instance.create(NoticeBoardService::class.java)
        val noticeSeq = noticeId.toString()

        // 상세 공지사항 로드 시작 로그
        Log.d("NoticeBoardFragment", "Initiating fetch for notice detail, ID: $noticeSeq")

        service.getNoticeSeq(token, noticeSeq).enqueue(object : Callback<NoticeBoardData> {
            override fun onResponse(call: Call<NoticeBoardData>, response: Response<NoticeBoardData>) {
                Log.d("NoticeBoardFragment", "Received response for notice detail, ID: $noticeSeq")

                if (response.isSuccessful) {
                    response.body()?.let { detailedData ->
                        Log.d("NoticeBoardFragment", "Successfully fetched notice detail: $detailedData")
                        openDetailFragment(detailedData)
                    } ?: run {
                        Log.e("NoticeBoardFragment", "Notice detail response body is null, ID: $noticeSeq")
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "No error body available"
                    Log.e(
                        "NoticeBoardFragment",
                        "Failed to fetch notice detail, ID: $noticeSeq, Code: ${response.code()}, Error Body: $errorBody"
                    )
                }
            }

            override fun onFailure(call: Call<NoticeBoardData>, t: Throwable) {
                Log.e("NoticeBoardFragment", "Error occurred while fetching notice detail, ID: $noticeSeq", t)
            }
        })
    }

    private fun openDetailFragment(detailedData: NoticeBoardData) {
        Log.d("NoticeBoardFragment", "Navigating to NoticeDetailFragment with data: $detailedData")

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

        Log.d("NoticeBoardFragment", "NoticeDetailFragment transaction committed")
    }

    private fun getJwtToken(): String {
        val sharedPref = requireContext().getSharedPreferences("auth", Context.MODE_PRIVATE)
        val token = sharedPref.getString("jwt_token", "") ?: ""
        Log.d("NoticeBoardFragment", "Retrieved JWT token: $token")
        return token
    }

    private fun loadNoticesFromServer() {
        val token = getJwtToken()
        Log.d("NoticeBoardFragment", "Initiating fetch for notice list with token: $token")

        val service = RetrofitClient.instance.create(NoticeBoardService::class.java)
        service.getNotice(token).enqueue(object : Callback<List<NoticeBoardData>> {
            override fun onResponse(call: Call<List<NoticeBoardData>>, response: Response<List<NoticeBoardData>>) {
                Log.d("NoticeBoardFragment", "Received response for notice list")

                if (response.isSuccessful) {
                    response.body()?.let { notices ->
                        Log.d("NoticeBoardFragment", "Successfully fetched ${notices.size} notices")
                        noticeList.clear()
                        noticeList.addAll(notices)
                        adapter.notifyDataSetChanged()
                    } ?: run {
                        Log.e("NoticeBoardFragment", "Notice list response body is null")
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "No error body available"
                    Log.e(
                        "NoticeBoardFragment",
                        "Failed to fetch notice list, Code: ${response.code()}, Error Body: $errorBody"
                    )
                }
            }

            override fun onFailure(call: Call<List<NoticeBoardData>>, t: Throwable) {
                Log.e("NoticeBoardFragment", "Error occurred while fetching notice list", t)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
