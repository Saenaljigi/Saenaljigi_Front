    package com.example.saenaljigi_app.login

    import android.content.Intent
    import android.os.Bundle
    import androidx.activity.enableEdgeToEdge
    import androidx.appcompat.app.AppCompatActivity
    import androidx.appcompat.widget.AppCompatButton
    import com.example.saenaljigi_app.BaseActivity
    import com.example.saenaljigi_app.R
    import com.example.saenaljigi_app.databinding.ActivityInitBinding

    class InitActivity : BaseActivity() {

        private lateinit var binding: ActivityInitBinding
        private lateinit var btnLogin: AppCompatButton

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            enableEdgeToEdge()
            binding = ActivityInitBinding.inflate(layoutInflater)
            setContentView(binding.root)

            setWindowInsets(binding)

            btnLogin = findViewById(R.id.btn_login)

            //시작하기 누르면 로그인 페이지로 화면 전환
            btnLogin.setOnClickListener{
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }

        }
    }
