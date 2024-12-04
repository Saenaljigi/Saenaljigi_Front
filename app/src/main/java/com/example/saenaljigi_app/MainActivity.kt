package com.example.saenaljigi_app

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import com.example.saenaljigi_app.board.BoardFragment
import com.example.saenaljigi_app.board.PostResultsFragment // 추가
import com.example.saenaljigi_app.databinding.ActivityMainBinding
import com.example.saenaljigi_app.home.HomeFragment
import com.example.saenaljigi_app.menu.MenuBoardFragment
import com.example.saenaljigi_app.mypage.MyPageFragment
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

        setStatusBarTransparent()

        // 검색어를 Intent로부터 받아서 처리
        val searchQuery = intent.getStringExtra("searchQuery")

        // 검색어가 존재하면 PostResultsFragment를 띄움
        if (!searchQuery.isNullOrEmpty()) {
            val fragment = PostResultsFragment.newInstance(searchQuery)
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    fun setStatusBarTransparent() {
        window.apply {
            setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }
        if (Build.VERSION.SDK_INT >= 30) {    // API 30에 적용
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
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

                R.id.fragment_board -> {
                    BoardFragment().changeFragment()
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
