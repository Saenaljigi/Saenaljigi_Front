package com.example.saenaljigi_app.board

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.saenaljigi_app.R
import com.example.saenaljigi_app.RetrofitClient
import com.example.saenaljigi_app.databinding.FragmentBoardDetailBinding
import com.example.saenaljigi_app.databinding.ItemAnswerBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class BoardDetailFragment : Fragment() {

    private var _binding: FragmentBoardDetailBinding? = null
    private val binding get() = _binding!!
    private val postService: PostService by lazy {
        RetrofitClient.instance.create(PostService::class.java)
    }

    private var isAnswerClicked = false
    private lateinit var answerAdapter: AnswerAdapter


    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private val refreshInterval: Long = 60000
    private var isLiked = false  // 좋아요 상태를 추적

    companion object {
        private const val ARG_POST_ID = "postId"

        fun newInstance(postId: Long): BoardDetailFragment {
            return BoardDetailFragment().apply {
                arguments = Bundle().apply { putLong(ARG_POST_ID, postId) }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBoardDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val postId = arguments?.getLong(ARG_POST_ID) ?: return

        // 뒤로가기 버튼 동작
        binding.backBtn.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        // UI 초기화
        fetchPost(postId)



        // 댓글 작성 버튼 클릭 처리
        binding.ivSendButton.setOnClickListener {
            val content = binding.etAnswer.text.toString()
            val isAnonymous = true

            if (content.isNotEmpty()) {
                if (isAnswerClicked) {
                    // 선택된 댓글에 대해 대댓글을 보이도록 설정
                    val selectedPosition = answerAdapter.getSelectedCommentPosition()
                    if (selectedPosition >= 0) {
                        // 대댓글 UI 보이기
                        val selectedItemBinding = ItemAnswerBinding.inflate(LayoutInflater.from(context))
                        selectedItemBinding.sampleComment.visibility = View.VISIBLE

                        // 대댓글 내용 설정
                        selectedItemBinding.sampleComment.findViewById<TextView>(R.id.tv_answer_content).text = content

                    }
                } else {
                    // 일반 댓글 작성
                    createComment(content, isAnonymous, postId)
                }
            } else {
                Toast.makeText(requireContext(), "댓글 내용을 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }




        // 좋아요 버튼 클릭 처리
        binding.ivLikeIcon.setOnClickListener {
            val token = getJwtToken()
            val postId = arguments?.getLong(ARG_POST_ID)

            if (postId != null) {
                if (isLiked) {
                    // 좋아요 취소 (likeCnt - 1)
                    updateLikeCount(postId, -1)
                    isLiked = false
                } else {
                    // 좋아요 (likeCnt + 1)
                    updateLikeCount(postId, 1)
                    isLiked = true
                }
            }
        }

        // 새로 고침 설정
        setupAutoRefresh()
    }



    private fun createComment(content: String, isAnonymous: Boolean, postId: Long) {
        val token = getJwtToken()
        val userId = getUserIdFromSharedPrefs()

        postService.createComment(
            "$token",
            userId,
            postId,
            content,
            isAnonymous
        ).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "댓글이 성공적으로 작성되었습니다.", Toast.LENGTH_SHORT).show()
                    // 댓글 작성 후, 댓글 수 +1 서버로 전송
                    updateCommentCount(postId)
                    binding.etAnswer.setText("")  // 텍스트 필드 비우기
                } else {
                    Log.e("BoardDetailFragment", "댓글 작성 실패: ${response.message()}")
                    Toast.makeText(requireContext(), "댓글 작성에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("BoardDetailFragment", "Network error: ${t.message}", t)
                Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateLikeCount(postId: Long, likeChange: Int) {
        val token = getJwtToken()

        // 기존 좋아요 개수를 가져올 때 "개"를 제외한 숫자만 추출
        val currentLikeCount = binding.tvLikeIcon.text.toString().replace("[^0-9]".toRegex(), "").toIntOrNull() ?: 0
        val updatedLikeCount = currentLikeCount + likeChange

        Log.d("BoardDetailFragment", "Updating like count for postId: $postId, current count: $currentLikeCount, updated count: $updatedLikeCount")

        postService.updateLikeCnt("$token", postId, updatedLikeCount.toLong())
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    Log.d("BoardDetailFragment", "Response received for updating like count: ${response.code()}, ${response.message()}")

                    if (response.isSuccessful) {
                        Log.d("BoardDetailFragment", "Like count updated successfully")
                        binding.tvLikeIcon.text = "${updatedLikeCount}개"
                    } else {
                        Log.e("BoardDetailFragment", "좋아요 업데이트 실패: ${response.message()}")
                        Toast.makeText(requireContext(), "좋아요 업데이트 실패", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e("BoardDetailFragment", "Network error while updating like count: ${t.message}", t)
                    Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun updateCommentCount(postId: Long) {
        val token = getJwtToken()

        // 기존 댓글 개수를 가져올 때 "개"를 제외한 숫자만 추출
        val currentCommentCount = binding.tvCommentIcon.text.toString().replace("[^0-9]".toRegex(), "").toIntOrNull() ?: 0
        val updatedCommentCount = currentCommentCount + 1

        Log.d("BoardDetailFragment", "Updating comment count for postId: $postId, current count: $currentCommentCount, updated count: $updatedCommentCount")

        postService.updateCommentCnt("$token", postId, updatedCommentCount.toLong())
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    Log.d("BoardDetailFragment", "Response received for updating comment count: ${response.code()}, ${response.message()}")

                    if (response.isSuccessful) {
                        Log.d("BoardDetailFragment", "Comment count updated successfully")
                        binding.tvCommentIcon.text = "${updatedCommentCount}개"
                    } else {
                        Log.e("BoardDetailFragment", "댓글 개수 업데이트 실패: ${response.message()}")
                        Toast.makeText(requireContext(), "댓글 개수 업데이트 실패", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e("BoardDetailFragment", "Network error while updating comment count: ${t.message}", t)
                    Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            })
    }


    private fun setupAutoRefresh() {
        handler = Handler(Looper.getMainLooper())
        runnable = object : Runnable {
            override fun run() {
                val postId = arguments?.getLong(ARG_POST_ID)
                if (postId != null) {
                    // 새로 고침: 좋아요, 댓글 상태 업데이트
                    fetchPost(postId)
                }
                handler.postDelayed(this, refreshInterval)
            }
        }
        handler.post(runnable)
    }

    private fun fetchPost(postId: Long) {
        val token = getJwtToken()

        // 요청 전 로그 추가: 요청하는 postId와 token 확인
        Log.d("BoardDetailFragment", "Fetching post with postId: $postId and token: $token")

        postService.getPost("$token", postId).enqueue(object : Callback<PostClass> {
            override fun onResponse(call: Call<PostClass>, response: Response<PostClass>) {
                // 응답 코드와 메시지 확인
                Log.d("BoardDetailFragment", "Response code: ${response.code()}")

                if (response.isSuccessful) {
                    val post = response.body()

                    if (post != null) {
                        // 성공적인 응답을 받았을 때의 로그
                        Log.d("BoardDetailFragment", "Post fetched successfully: $post")

                        // UI 업데이트
                        binding.tvTitle.text = post.title
                        binding.tvContent.text = post.content
                        binding.tvLikeIcon.text = "${post.likeCnt}개"
                        binding.tvCommentIcon.text = "${post.commentCnt}개"
                        binding.tvCreatedAt.text = formatDate(post.createdAt)

                        binding.rvCommentContainer.layoutManager = LinearLayoutManager(requireContext())

                        // 댓글 목록 업데이트
                        val answerAdapter = AnswerAdapter(post.comments ?: emptyList()) { position, isSelected ->
                            // 클릭된 댓글의 대댓글 표시/숨기기 처리
                            if (isSelected) {
                                answerAdapter.showReplyForSelectedItem(position)
                            } else {
                                answerAdapter.showReplyForSelectedItem(position)
                            }
                        }
                        binding.rvCommentContainer.adapter = answerAdapter

                        // 댓글 데이터 확인
                        Log.d("BoardDetailFragment", "Post comments: ${post.comments}")
                    } else {
                        Log.e("BoardDetailFragment", "Response body is null")
                    }
                } else {
                    // 실패한 경우, 응답 메시지와 코드
                    Log.e("BoardDetailFragment", "Failed to load post: ${response.message()} with code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<PostClass>, t: Throwable) {
                // 네트워크 오류나 다른 실패 발생시 로그
                Log.e("BoardDetailFragment", "Failed to load post due to network error: ${t.message}", t)
            }
        })
    }

    private fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSS", Locale.getDefault())
            val date = inputFormat.parse(dateString)

            val outputFormat = SimpleDateFormat("MM월 dd일 HH:mm", Locale.getDefault())
            outputFormat.format(date)
        } catch (e: Exception) {
            Log.e("BoardDetailFragment", "Error formatting date: ${e.message}")
            dateString
        }
    }

    private fun getJwtToken(): String {
        val sharedPref = requireContext().getSharedPreferences("auth", Context.MODE_PRIVATE)
        return sharedPref.getString("jwt_token", "") ?: ""
    }

    private fun getUserIdFromSharedPrefs(): Int {
        val sharedPref = requireContext().getSharedPreferences("auth", Context.MODE_PRIVATE)
        val userId = sharedPref.getInt("userId", -1)
        Log.d("BoardDetailFragment", "Retrieved userId: $userId")
        return userId
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        handler.removeCallbacks(runnable)  // Handler 제거
    }
}
