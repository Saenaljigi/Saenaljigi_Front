package com.example.saenaljigi_app

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.saenaljigi_app.databinding.ActivityMainBinding
import com.example.saenaljigi_app.notice.NoticeBoardFragment


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showInit()
        initBottomNav()
    }

    private fun initBottomNav() {
        binding.bottomNavigationView.itemIconTintList = null

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.fragment_home -> {
                    HomeFragment().changeFragment()
                    true
                }

                R.id.fragment_menu -> {
                    MenuBoardFragment().changeFragment()
                    true
                }

                R.id.fragment_notice -> {
                    NoticeBoardFragment().changeFragment()
                    true
                }

                R.id.fragment_mypage -> {
                    MyPageFragment().changeFragment()
                    true
                }

                else -> false
            }
        }

        binding.bottomNavigationView.setOnItemReselectedListener {
            // 바텀네비 재클릭시 화면 재생성 방지
        }
    }

    private fun Fragment.changeFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, this)
            .commit()
    }

    private fun showInit() {
        val transaction = supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, HomeFragment())
        transaction.commit()
    }
}
