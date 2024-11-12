package com.example.saenaljigi_app.login

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.WindowCompat
import com.example.saenaljigi_app.MainActivity
import com.example.saenaljigi_app.R
import com.example.saenaljigi_app.RetrofitClient
import com.example.saenaljigi_app.UserDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

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
        setContentView(R.layout.activity_login)

        setStatusBarTransparent()  // 상태 바 투명하게 설정

        tvNo = findViewById(R.id.tv_no)
        clBox = findViewById(R.id.cl_box)
        etId = findViewById(R.id.et_id)
        etPassword = findViewById(R.id.et_password)
        btnLogin = findViewById(R.id.btn_login)
        constraintLayout = findViewById(R.id.root_layout)

        tvNo.setOnClickListener {
            updateClBoxVisible()
        }

        btnLogin.setOnClickListener {
            performLogin()
        }

        etId.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                updateAllBackgrounds()
            }
        }

        etPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                updateAllBackgrounds()
            }
        }
    }

    // 로그인 요청 함수
    private fun performLogin() {
        val id = etId.text.toString()
        val pw = etPassword.text.toString()

        val body = mapOf(
            "id" to id,
            "pw" to pw
        )

        // 세종대학교 로그인 요청
        RetrofitClient.sejongApi.login(body).enqueue(object : Callback<SejongAuthResponse> {
            override fun onResponse(call: Call<SejongAuthResponse>, response: Response<SejongAuthResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val auth = response.body()?.result
                    if (auth?.isAuth == "true") {
                        val userData = auth.body
                        requestJwtToken(userData, id)
                    } else {
                        showLoginError("아이디나 비밀번호가 일치하지 않습니다.")
                    }
                } else {
                    showLoginError("네트워크 문제로 로그인하지 못했습니다.")
                }
            }

            override fun onFailure(call: Call<SejongAuthResponse>, t: Throwable) {
                showLoginError("네트워크 문제로 로그인하지 못했습니다.")
            }
        })
    }

    // JWT 토큰 요청 함수
    private fun requestJwtToken(userData: SejongAuthResponseResultBodyJson, id: String) {
        val userDTO = UserDTO(
            id = id.toLong(),
            studentId = userData.studentId,
            name = userData.name,
            mealCnt = 0,
            reward = 0,
            penalty = 0,
            totalCnt = 0
        )

        // JWT 요청
        RetrofitClient.userApi.requestJwtToken(userDTO).enqueue(object : Callback<JwtResponse> {
            override fun onResponse(call: Call<JwtResponse>, response: Response<JwtResponse>) {
                if (response.isSuccessful) {
                    val token = response.body()?.token
                    if (!token.isNullOrEmpty()) {
                        saveUserData(token)
                        navigateToMainActivity()
                    } else {
                        showLoginError("네트워크 문제로 로그인하지 못했습니다.")
                    }
                } else {
                    showLoginError("네트워크 문제로 로그인하지 못했습니다.")
                }
            }

            override fun onFailure(call: Call<JwtResponse>, t: Throwable) {
                Log.e("JWT_REQUEST", "JWT 발급 오류", t)
                showLoginError("네트워크 문제로 로그인하지 못했습니다.")
            }
        })
    }

    // SharedPreferences에 JWT 토큰 저장
    private fun saveUserData(token: String) {
        val sharedPref = getSharedPreferences("auth", MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("jwt_token", token)
            apply()
        }
    }

    // MainActivity로 이동
    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    // 로그인 실패 시 에러 표시
    private fun showLoginError(message: String) {
        etId.setBackgroundResource(R.drawable.login_red_id)
        etPassword.setBackgroundResource(R.drawable.login_red_id)
        etId.setHintTextColor(Color.parseColor("#FF0707"))
        etPassword.setHintTextColor(Color.parseColor("#FF0707"))
        tvFailLogin.apply {
            text = message
            visibility = View.VISIBLE
        }    }

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

    // 입력하면 배경 변경
    private fun updateAllBackgrounds() {
        etId.setBackgroundResource(R.drawable.login_black_id)
        etPassword.setBackgroundResource(R.drawable.login_black_id)
        btnLogin.setBackgroundResource(R.drawable.login_btn_black)
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
}
