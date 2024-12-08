package com.example.saenaljigi_app.notice

import NoticeBoardData
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.saenaljigi_app.R
import com.example.saenaljigi_app.RetrofitClient
import com.example.saenaljigi_app.databinding.FragmentNoticeDetailBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NoticeDetailFragment : Fragment() {

    private var _binding: FragmentNoticeDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNoticeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.noticeBack.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, NoticeBoardFragment())
                .addToBackStack(null)
                .commit()
        }

        val noticeId = arguments?.getLong("noticeId") ?: return  // postId를 받아옴
        Log.d("NoticeDetailFragment", "Received noticeId: $noticeId")

        loadNoticeDetail(noticeId)
    }

    private fun loadNoticeDetail(postId: Long) {
        val token = getJwtToken()
        Log.d("NoticeDetailFragment", "Initiating fetch for notice details with postId: $postId and token: $token")

        val service = RetrofitClient.instance.create(NoticeBoardService::class.java)
        service.getNoticeSeq(token, postId.toString()).enqueue(object : Callback<NoticeBoardData> {
            override fun onResponse(call: Call<NoticeBoardData>, response: Response<NoticeBoardData>) {
                Log.d("NoticeDetailFragment", "Received response for notice details")

                if (response.isSuccessful) {
                    response.body()?.let { notice ->
                        Log.d("NoticeDetailFragment", "Response Body: $notice")

                        // 서버에서 받아온 값으로 파일 목록을 동적으로 업데이트
                        notice.populateFiles()
                        // UI 업데이트
                        updateUI(notice, postId)
                    } ?: run {
                        Log.e("NoticeDetailFragment", "Notice detail response body is null")
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "No error body available"
                    Log.e(
                        "NoticeDetailFragment",
                        "Failed to fetch notice details, Code: ${response.code()}, Error Body: $errorBody"
                    )
                }
            }

            override fun onFailure(call: Call<NoticeBoardData>, t: Throwable) {
                Log.e("NoticeDetailFragment", "Error occurred while fetching notice details", t)
            }
        })
    }

    private fun updateUI(notice: NoticeBoardData, postId: Long) {
        Log.d("NoticeDetailFragment", "Updating UI with notice details")

        // 제목, 작성자, 생성일 처리
        binding.tvNoticeTitle.text = notice.title
        binding.tvNoticeWriter.text = "작성자: ${notice.author}"
        binding.tvCreatedAt.text = notice.date
        binding.webView.visibility=View.GONE

        // 이미지와 내용의 가시성 설정
        when (postId) {
            36259L -> {
                binding.ivImg1.visibility = View.VISIBLE
                binding.ivImg2.visibility = View.VISIBLE
                binding.ivImg3.visibility = View.GONE
                binding.tvNoticeContent.visibility = View.GONE
            }
            36180L -> {
                binding.ivImg1.visibility = View.GONE
                binding.ivImg2.visibility = View.GONE
                binding.ivImg3.visibility = View.VISIBLE
                binding.tvNoticeContent.visibility = View.GONE
            }
            10000L -> {
                binding.ivImg1.visibility = View.GONE
                binding.ivImg2.visibility = View.GONE
                binding.ivImg3.visibility = View.GONE
                binding.tvNoticeContent.visibility = View.VISIBLE
                binding.tvNoticeContent.text = notice.content // 내용 표시
            }
            else -> {
                // 기본 상태에서는 아무것도 보이지 않음
                binding.ivImg1.visibility = View.GONE
                binding.ivImg2.visibility = View.GONE
                binding.ivImg3.visibility = View.GONE
                binding.tvNoticeContent.visibility = View.GONE
            }
        }

        // 파일 목록 처리 (기존 코드)
        notice.files?.let { files ->
            if (files.isNotEmpty()) {
                binding.rvAttachedFiles.visibility = View.VISIBLE
                val fileAdapter = FileAdapter(files) { fileData ->
                    openFileUrl(fileData.fileUrl) // 파일 클릭 시 해당 URL을 여는 함수
                }
                binding.rvAttachedFiles.layoutManager = LinearLayoutManager(requireContext())
                binding.rvAttachedFiles.adapter = fileAdapter
            } else {
                binding.rvAttachedFiles.visibility = View.GONE
            }
        }
    }

    private fun openFileUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    private fun getJwtToken(): String {
        val sharedPref = requireContext().getSharedPreferences("auth", Context.MODE_PRIVATE)
        val token = sharedPref.getString("jwt_token", "") ?: ""
        Log.d("NoticeDetailFragment", "Retrieved JWT token: $token")
        return token
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
