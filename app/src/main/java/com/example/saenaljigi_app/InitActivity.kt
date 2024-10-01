    package com.example.saenaljigi_app

    import android.content.Intent
    import android.os.Bundle
    import android.os.Handler
    import android.os.Looper
    import androidx.activity.enableEdgeToEdge
    import androidx.appcompat.app.AppCompatActivity
    import androidx.appcompat.widget.AppCompatButton
    import androidx.core.view.ViewCompat
    import androidx.core.view.WindowInsetsCompat

    class InitActivity : AppCompatActivity() {

        private lateinit var btnLogin: AppCompatButton

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_init)

            btnLogin = findViewById(R.id.btn_login)

            //시작하기 누르면 로그인 페이지로 화면 전환
            btnLogin.setOnClickListener{
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }

        }
    }
