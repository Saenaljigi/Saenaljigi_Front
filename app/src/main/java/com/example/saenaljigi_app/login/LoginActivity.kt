package com.example.saenaljigi_app.login

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.WindowCompat
import androidx.core.widget.addTextChangedListener
import com.example.saenaljigi_app.AdminActivity
import com.example.saenaljigi_app.BaseActivity
import com.example.saenaljigi_app.MainActivity
import com.example.saenaljigi_app.R
import com.example.saenaljigi_app.RetrofitClient
import com.example.saenaljigi_app.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var tvNo: TextView
    private lateinit var clBox: ConstraintLayout
    private lateinit var etId: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: AppCompatButton
    private lateinit var constraintLayout: ConstraintLayout
    private lateinit var tvFailLogin: TextView

    private var isClBoxVisible: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setWindowInsets(binding)

        tvNo = findViewById(R.id.tv_no)
        clBox = findViewById(R.id.cl_box)
        etId = findViewById(R.id.et_id)
        etPassword = findViewById(R.id.et_password)
        btnLogin = findViewById(R.id.btn_login)
        tvFailLogin = findViewById(R.id.tv_fail_login) // tv_fail_login 초기화
        constraintLayout = findViewById(R.id.root_layout)

        tvNo.setOnClickListener {
            updateClBoxVisible()
        }

        btnLogin.setOnClickListener {
            performLogin()
        }

        // 아이디나 비밀번호 입력 필드가 수정될 때 버튼 색깔을 원래대로 변경
        etId.addTextChangedListener {
            resetButtonAndTextFields()
        }
        etPassword.addTextChangedListener {
            resetButtonAndTextFields()
        }
    }

    // 로그인 요청 함수
    private fun performLogin() {
        val id = etId.text.toString()
        val pw = etPassword.text.toString()

        // 관리자 페이지로 이동
        if (id.equals("000000") && pw.equals("111111")) {
            goToAdminPage()
            return
        }

        val body = UserRequest(username = id, password = pw)

        /* JWT 토큰 요청 */
        RetrofitClient.userApi.requestJwtToken(body).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.code() == 200) { // HTTP 상태 코드 200만 확인
                    response.body()?.let { userResponse ->
                        // JWT 토큰 저장 로직 추가
                        val jwtToken = response.headers()["Authorization"] ?: ""
                        saveUserData(jwtToken, userResponse.username,userResponse.userId)

                        // 로그인 성공 시 MainActivity로 이동
                        navigateToMainActivity()
                    } ?: run {
                        // Response body가 null인 경우
                        showLoginError("응답 데이터가 올바르지 않습니다.")
                    }
                } else {
                    // 상태 코드가 200이 아닌 경우
                    showLoginError("아이디나 비밀번호가 일치하지 않습니다.")
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                // 네트워크 문제 처리
                showLoginError("네트워크 문제로 로그인하지 못했습니다.")
            }
        })
    }

    // MainActivity로 이동
    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    // jwt_token 저장
    private fun saveUserData(token: String, username: String, userId:Int) {
        val sharedPref = getSharedPreferences("auth", MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("jwt_token", token)
            putString("username", username)
            putInt("userId", userId)
            apply()
        }
    }

    // 로그인 실패 시 에러 표시 및 버튼 색 변경
    private fun showLoginError(message: String) {
        etId.setBackgroundResource(R.drawable.login_red_id)
        etPassword.setBackgroundResource(R.drawable.login_red_id)
        etId.setHintTextColor(Color.parseColor("#FF0707"))
        etPassword.setHintTextColor(Color.parseColor("#FF0707"))
        tvFailLogin.apply {
            text = message // 에러 메시지 설정
            visibility = View.VISIBLE // 메시지 보이기
        }
        btnLogin.setBackgroundResource(R.drawable.login_btn)
    }

    // 아이디나 비밀번호 입력 필드가 수정될 때 호출: 색깔을 원래대로 복원
    private fun resetButtonAndTextFields() {
        etId.setBackgroundResource(R.drawable.login_black_id)
        etPassword.setBackgroundResource(R.drawable.login_black_id)
        etId.setHintTextColor(Color.parseColor("#808080"))
        etPassword.setHintTextColor(Color.parseColor("#808080"))
        btnLogin.setBackgroundResource(R.drawable.login_btn_black) // 버튼 색 복원
    }

    // tv_no 눌렀을 때 마진 수정 함수
    private fun updateClBoxVisible() {
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)

        if (isClBoxVisible) {
            constraintSet.setMargin(R.id.et_id, ConstraintSet.TOP, convertDpToPx(175))
        } else {
            constraintSet.setMargin(R.id.et_id, ConstraintSet.TOP, convertDpToPx(132))
        }

        constraintSet.applyTo(constraintLayout)

        clBox.visibility = if (isClBoxVisible) View.GONE else View.VISIBLE
        clBox.requestLayout()
        isClBoxVisible = !isClBoxVisible
    }

    // dp를 px로 변환하는 함수
    private fun convertDpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    private fun setStatusBarTransparent() {
        window.apply {
            setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }
        if(Build.VERSION.SDK_INT >= 30) {   // API 30 에 적용
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
    }

    private fun goToAdminPage() {
        val intent = Intent(this, AdminActivity::class.java)
        startActivity(intent)
    }
}
