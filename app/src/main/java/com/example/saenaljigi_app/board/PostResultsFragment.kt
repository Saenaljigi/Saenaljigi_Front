package com.example.saenaljigi_app.board

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.saenaljigi_app.R
import com.example.saenaljigi_app.RetrofitClient
import com.example.saenaljigi_app.databinding.FragmentPostResultsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostResultsFragment : Fragment() {

    private var searchKeyword: String? = null
    private lateinit var binding: FragmentPostResultsBinding
    private val postService: PostService by lazy {
        RetrofitClient.instance.create(PostService::class.java)
    }

    companion object {
        const val ARG_PARAM1 = "search_keyword"

        @JvmStatic
        fun newInstance(searchKeyword: String) =
            PostResultsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, searchKeyword)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            searchKeyword = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostResultsBinding.inflate(inflater, container, false)

        // EditText에 검색어 표시
        searchKeyword?.let {
            binding.etSearchPost.setText(it)
        }

        // btn_cancel 클릭 시 검색 화면으로 돌아가기
        binding.btnCancel.setOnClickListener {
            val intent = Intent(requireContext(), SearchPostActivity::class.java)
            startActivity(intent)
        }

        // 검색어에 맞는 게시물 필터링
        searchKeyword?.let {
            fetchFilteredPosts(it)
        }

        // et_search_post 클릭 시 SearchPostActivity로 이동
        binding.etSearchPost.setOnClickListener {
            val intent = Intent(requireContext(), SearchPostActivity::class.java)
            startActivity(intent)
        }


        return binding.root
    }

    private fun fetchFilteredPosts(searchKeyword: String) {
        val token = getJwtToken()  // JWT 토큰을 받아오는 함수
        Log.d("PostResultsFragment", "Fetching posts with keyword: $searchKeyword")

        postService.getAllPosts("$token").enqueue(object : Callback<List<PostClass>> {
            override fun onResponse(call: Call<List<PostClass>>, response: Response<List<PostClass>>) {
                if (response.isSuccessful) {
                    val posts = response.body()
                    Log.d("PostResultsFragment", "Successfully fetched posts: ${posts?.size}")

                    val filteredPosts = posts?.filter { post ->
                        post.title.contains(searchKeyword, ignoreCase = true) ||
                                post.content.contains(searchKeyword, ignoreCase = true)
                    } ?: emptyList()

                    Log.d("PostResultsFragment", "Filtered posts: ${filteredPosts.size}")

                    // 필터링된 게시물만 RecyclerView에 표시
                    if (filteredPosts.isEmpty()) {
                        // 검색 결과가 없으면 iv_no_search를 보이고, rv_board는 숨김
                        binding.ivNoSearch.visibility = View.VISIBLE
                        binding.rvBoard.visibility = View.GONE
                    } else {
                        // 검색 결과가 있으면 iv_no_search는 숨기고, rv_board는 보임
                        binding.ivNoSearch.visibility = View.GONE
                        binding.rvBoard.visibility = View.VISIBLE

                        val adapter = PostResultsAdapter(filteredPosts, searchKeyword) { post ->
                            val fragment = BoardDetailFragment.newInstance(post.id)
                            parentFragmentManager.beginTransaction()
                                .replace(R.id.fragment_container, fragment)
                                .addToBackStack(null)
                                .commit()
                        }

                        // RecyclerView 설정
                        binding.rvBoard.layoutManager = LinearLayoutManager(requireContext())
                        binding.rvBoard.adapter = adapter
                    }
                } else {
                    showError("Failed to fetch posts: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<PostClass>>, t: Throwable) {
                showError("Network error: ${t.message}")
            }
        })
    }


    private fun getJwtToken(): String {
        val sharedPref = requireContext().getSharedPreferences("auth", Context.MODE_PRIVATE)
        return sharedPref.getString("jwt_token", "") ?: ""
    }

    private fun showError(message: String) {
        Log.e("PostResultsFragment", message)
        // 에러 메시지를 사용자에게 표시하는 코드 추가 (예: Toast 또는 Snackbar)
    }
}
