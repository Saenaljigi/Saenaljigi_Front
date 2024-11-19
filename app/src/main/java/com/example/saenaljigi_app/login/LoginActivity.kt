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

        /* JWT 토큰 요청 (주석 처리하여 화면 넘어가게 하기)
        val body = mapOf("id" to id, "pw" to pw)
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
        */


        // 직접 MainActivity로 이동 (API 실패 무시)
        navigateToMainActivity()
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
        }
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
