package com.example.saenaljigi_app.board

import android.os.Bundle
import android.util.Log // 로그 사용을 위해 추가
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.saenaljigi_app.RetrofitClient
import com.example.saenaljigi_app.databinding.ActivityCreatePostBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreatePostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreatePostBinding
    private val postService: PostService by lazy {
        RetrofitClient.instance.create(PostService::class.java) // RetrofitClient 사용
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Activity 시작 로그
        Log.d("CreatePostActivity", "Activity started")

        binding.postBtn.setOnClickListener {
            val title = binding.tvPostTitle.text.toString().trim()
            val content = binding.tvPostContent.text.toString().trim()
            val token = getJwtToken()
            val userId = getUserIdFromSharedPrefs()

            // 입력 데이터 로그
            Log.d("CreatePostActivity", "Title: $title")
            Log.d("CreatePostActivity", "Content: $content")
            Log.d("CreatePostActivity", "Token: $token")
            Log.d("CreatePostActivity", "UserId: $userId")

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "제목과 내용을 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
                Log.w("CreatePostActivity", "Title or Content is empty")
            } else if (token.isEmpty() || userId == -1) {
                Toast.makeText(this, "유효한 사용자 정보가 없습니다.", Toast.LENGTH_SHORT).show()
                Log.e("CreatePostActivity", "Invalid token or userId")
            } else {
                Log.d("CreatePostActivity", "Sending post request")
                sendPost(token, userId, title, content)
            }
        }
    }

    private fun getUserIdFromSharedPrefs(): Int {
        val sharedPref = getSharedPreferences("auth", MODE_PRIVATE)
        val userId = sharedPref.getInt("userId", -1)
        Log.d("CreatePostActivity", "Retrieved userId: $userId")
        return userId
    }

    private fun getJwtToken(): String {
        val sharedPref = getSharedPreferences("auth", MODE_PRIVATE)
        val token = sharedPref.getString("jwt_token", "") ?: ""
        Log.d("CreatePostActivity", "Retrieved token: $token")
        return token
    }

    private fun sendPost(token: String, userId: Int, title: String, content: String) {
        postService.createPost(token, userId, title, content)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        // 서버 응답 성공 (본문이 없어도 처리)
                        Log.d("CreatePostActivity", "Post successful. HTTP Code: ${response.code()}")
                        Toast.makeText(this@CreatePostActivity, "게시물이 등록되었습니다!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        // 서버 응답 실패 (에러 메시지 로그 출력)
                        val errorBody = response.errorBody()?.string() ?: "No error body"
                        Log.e("CreatePostActivity", "Post failed. HTTP Code: ${response.code()}, Error: $errorBody")
                        Toast.makeText(this@CreatePostActivity, "게시물 등록 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    // 네트워크 문제 등 요청 실패
                    Log.e("CreatePostActivity", "Post request failed", t)
                    Toast.makeText(this@CreatePostActivity, "서버와의 통신 실패: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            })
    }

}
