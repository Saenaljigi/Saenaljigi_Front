package com.example.saenaljigi_app.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.saenaljigi_app.R
import com.example.saenaljigi_app.databinding.FragmentMyPageBinding

class MyPageFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentMyPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyPageBinding.inflate(inflater, container, false)

        // MealTicketFragment로 교체
        binding.menu1Box.setOnClickListener {
            replaceFragment(PointFragment())
        }

        // PointFragment로 교체
        binding.menu2Box.setOnClickListener {
            replaceFragment(MealTicketFragment())
        }

        // FAQFragment로 교체
        binding.menu3Box.setOnClickListener {
            // replaceFragment(FAQFragment())
        }

        // SettingFragment로 교체
        binding.menu4Box.setOnClickListener {
            // replaceFragment(SettingFragment())
        }

        return binding.root
    }

    // 프래그먼트를 교환하는 함수
    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}