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
import android.webkit.WebChromeClient
import android.webkit.WebView
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

    private lateinit var fileAdapter: FileAdapter

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
            // NoticeBoardFragment로 이동 (뒤로 가는 대신 새로운 프래그먼트로 교체)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, NoticeBoardFragment()) // fragment_container는 프래그먼트가 표시될 컨테이너 ID
                .commit() // 트랜잭션 적용
        }

        val noticeId = arguments?.getLong("noticeId") ?: return  // noticeId를 받아옴
        Log.d("NoticeDetailFragment", "Received postId: $noticeId")

        // 서버에서 공지사항 세부 정보 가져오기
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
                        updateUI(notice)
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

    private fun updateUI(notice: NoticeBoardData) {
        Log.d("NoticeDetailFragment", "Updating UI with notice details")

        // 서버에서 받아온 데이터로 UI 업데이트
        binding.tvNoticeTitle.text = notice.title
        binding.tvNoticeWriter.text = "작성자: ${notice.author}"
        binding.tvCreatedAt.text = notice.date

        // 로그 출력: 제목, 작성자, 생성일
        Log.d("NoticeDetailFragment", "Notice Title: ${notice.title}")
        Log.d("NoticeDetailFragment", "Notice Author: ${notice.author}")
        Log.d("NoticeDetailFragment", "Notice Created At: ${notice.date}")

        // 파일 목록 처리
        notice.files?.let { files ->
            if (files.isNotEmpty()) {
                binding.rvAttachedFiles.visibility = View.VISIBLE
                fileAdapter = FileAdapter(files) { fileData ->
                    openFileUrl(fileData.fileUrl) // 파일 클릭 시 해당 URL을 여는 함수
                }
                binding.rvAttachedFiles.layoutManager = LinearLayoutManager(requireContext())
                binding.rvAttachedFiles.adapter = fileAdapter

                // 파일 이름을 로그로 출력
                files.forEachIndexed { index, file ->
                    Log.d("NoticeDetailFragment", "File${index + 1}: ${file.fileName}") // 파일 이름 출력
                }
            } else {
                binding.rvAttachedFiles.visibility = View.GONE
            }
        }

        // 공지사항 내용 처리
        val noticeContent = notice.content
        if (!noticeContent.isNullOrEmpty()) {
            // 내용이 있을 때 WebView로 렌더링
            binding.webView.visibility = View.VISIBLE
            binding.webView.settings.javaScriptEnabled = true
            binding.webView.webChromeClient = WebChromeClient()

            // \n을 <br>로 변환하고 불필요한 공백을 제거
            val formattedContent = noticeContent
                .replace("\n", "<br>")  // \n을 <br>로 변환
                .replace("&nbsp;", " ")  // &nbsp;을 공백으로 변환 (필요시 추가)
                .trim()  // 불필요한 공백을 제거

            // HTML 포맷으로 변환
            val htmlContent = """
            <html>
                <head>
                    <style>
                        body {
                            font-family: suite_semibold, sans-serif;
                            font-size: 13sp;
                            line-height: 1.5;
                        }
                    </style>
                </head>
                <body>
                    $formattedContent
                </body>
            </html>
        """

            // 불필요한 <p><br></p> 태그 제거 후 로드
            if (noticeContent.trim().isNotEmpty()) {
                Log.d("NoticeDetailFragment", "Notice content is not empty, loading WebView")
                binding.webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
            } else {
                Log.d("NoticeDetailFragment", "Notice content is empty, hiding WebView")
                binding.webView.visibility = View.GONE
            }
        } else {
            // 내용이 없으면 WebView 숨기기
            Log.d("NoticeDetailFragment", "Notice content is empty, hiding WebView")
            binding.webView.visibility = View.GONE
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
