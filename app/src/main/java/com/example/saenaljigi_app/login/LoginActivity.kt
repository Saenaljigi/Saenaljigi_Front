package com.example.saenaljigi_app.login

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import com.example.saenaljigi_app.notice.NoticeBoardFragment
import com.example.saenaljigi_app.R

class LoginActivity : AppCompatActivity() {

    private lateinit var tvNo:TextView
    private lateinit var clBox:ConstraintLayout
    private lateinit var tvFailLogin:TextView
    private lateinit var etId: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: AppCompatButton
    private lateinit var constraintLayout: ConstraintLayout

    private var isClBoxVisible:Boolean=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        tvNo=findViewById(R.id.tv_no)
        clBox = findViewById(R.id.cl_box)
        tvFailLogin = findViewById(R.id.tv_fail_login)
        etId = findViewById(R.id.et_id)
        etPassword = findViewById(R.id.et_password)
        btnLogin = findViewById(R.id.btn_login)
        constraintLayout = findViewById(R.id.root_layout)

        tvNo.setOnClickListener {
            updateClBoxVisible()
        }

        /*// *****btnLogin 클릭 시 MainActivity로 이동
        btnLogin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }*/

        // *****btnLogin 클릭 시 NoticeBoardFragment로 이동
        btnLogin.setOnClickListener {
            replaceFragment(NoticeBoardFragment())
        }


        // etId 또는 etPassword 입력 시 배경 변경
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

    //tv_no 눌렀을 때 마진 수정 함수
    private fun updateClBoxVisible() {
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)

        if (isClBoxVisible) {
            constraintSet.setMargin(R.id.et_id, ConstraintSet.TOP, convertDpToPx(175))
        } else {
            constraintSet.setMargin(R.id.et_id, ConstraintSet.TOP, convertDpToPx(132))
        }

        // 변경 사항 적용
        constraintSet.applyTo(constraintLayout)

        if (isClBoxVisible) {
            clBox.visibility = View.GONE
        } else {
            clBox.visibility = View.VISIBLE
        }

        // 가시성 변경 반영
        clBox.requestLayout()

        // 상태 토글
        isClBoxVisible = !isClBoxVisible
    }


    //입력하면 배경 변경
    private fun updateAllBackgrounds() {
        etId.setBackgroundResource(R.drawable.login_black_id) // etId 배경 변경
        etPassword.setBackgroundResource(R.drawable.login_black_id) // etPassword 배경 변경
        btnLogin.setBackgroundResource(R.drawable.login_btn_black) // btnLogin 배경 변경
    }

    // dp를 px로 변환하는 함수
    private fun convertDpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    // Fragment 교체 함수
    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment) // fragment_container는 Fragment를 추가할 컨테이너 ID입니다.
        transaction.addToBackStack(null) // 뒤로 가기 스택에 추가
        transaction.commit()
    }
}
