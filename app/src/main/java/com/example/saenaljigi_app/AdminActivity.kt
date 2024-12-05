package com.example.saenaljigi_app

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil.setContentView
import com.example.saenaljigi_app.databinding.ActivityAdminBinding

class AdminActivity : BaseActivity() {
    private lateinit var binding: ActivityAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setWindowInsets(binding)

        binding.tvApplicationCount.text = "2명"
        binding.tvApplicationDate.text = "2024.12.05 기준"
    }
}