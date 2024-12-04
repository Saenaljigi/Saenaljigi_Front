package com.example.saenaljigi_app.board

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.saenaljigi_app.R
import com.example.saenaljigi_app.RetrofitClient
import com.example.saenaljigi_app.databinding.FragmentBoardBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.os.Handler
import android.os.Looper
import android.util.Log

class BoardFragment : Fragment() {

    private var _binding: FragmentBoardBinding? = null
    private val binding get() = _binding!!
    private val postService: PostService by lazy {
        RetrofitClient.instance.create(PostService::class.java)
    }

    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private val refreshInterval: Long = 60000//12000

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBoardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvBoard.layoutManager = LinearLayoutManager(requireContext())

        // 초기 데이터 로드
        fetchPosts()

        // 주기적으로 데이터 새로고침x
        setupAutoRefresh()

        // 게시글 검색 버튼 클릭 시
        binding.ivSearchIcon.setOnClickListener {
            val intent = Intent(requireContext(), SearchPostActivity::class.java)
            startActivity(intent) // SearchPostActivity로 이동
        }

        // 게시글 작성 버튼 클릭 시
        binding.btnPost.setOnClickListener {
            val intent = Intent(requireContext(), CreatePostActivity::class.java)
            startActivityForResult(intent, CREATE_POST_REQUEST_CODE)
        }
    }

    private fun setupAutoRefresh() {
        handler = Handler(Looper.getMainLooper())
        runnable = object : Runnable {
            override fun run() {
                fetchPosts() // 데이터 새로고침
                handler.postDelayed(this, refreshInterval)
            }
        }
        handler.post(runnable)
    }

    private fun fetchPosts() {
        val token = getJwtToken()

        // Log the token being used for the request (be careful with exposing sensitive data)
        Log.d("BoardFragment", "Fetching posts with token: $token")

        postService.getAllPosts("$token").enqueue(object : Callback<List<PostClass>> {
            override fun onResponse(call: Call<List<PostClass>>, response: Response<List<PostClass>>) {
                if (response.isSuccessful) {
                    val posts = response.body()
                    if (posts != null) {
                        Log.d("BoardFragment", "Successfully fetched ${posts.size} posts.")
                        val adapter = BoardAdapter(posts) { board ->
                            val fragment = BoardDetailFragment.newInstance(board.id)
                            parentFragmentManager.beginTransaction()
                                .replace(R.id.fragment_container, fragment)
                                .addToBackStack(null)
                                .commit()
                        }
                        binding.rvBoard.adapter = adapter
                    }
                } else {
                    Log.e("BoardFragment", "Failed to fetch posts. Response code: ${response.code()}, Message: ${response.message()}")

                    if (response.code() == 403) {
                        Log.e("BoardFragment", "Authorization failed. Token may be invalid or expired.")
                        showError("Authorization failed. Please check your login status.")
                    } else {
                        showError("Failed to load posts: ${response.message()}")
                    }
                }
            }

            override fun onFailure(call: Call<List<PostClass>>, t: Throwable) {
                Log.e("BoardFragment", "Network error: ${t.message}", t)
                showError("Network error: ${t.message}")
            }
        })
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun getJwtToken(): String {
        val sharedPref = requireContext().getSharedPreferences("auth", Context.MODE_PRIVATE)
        return sharedPref.getString("jwt_token", "") ?: ""
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        stopAutoRefresh()
    }

    private fun stopAutoRefresh() {
        if (::handler.isInitialized && ::runnable.isInitialized) {
            handler.removeCallbacks(runnable)
        }
    }

    companion object {
        const val CREATE_POST_REQUEST_CODE = 100
    }
}
