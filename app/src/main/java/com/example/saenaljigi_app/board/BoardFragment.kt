package com.example.saenaljigi_app.board

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.saenaljigi_app.R
import com.example.saenaljigi_app.databinding.FragmentBoardBinding

class BoardFragment : Fragment() {

    private var _binding: FragmentBoardBinding? = null
    private val binding get() = _binding!!

    private val boardList = listOf(
        BoardClass(1, "배달 같이 시키실 분", "버거킹 먹고 싶은데 최소주문 금액이 안되는데 같이 시킬 사람 있나요? 버거킹 아니어도 돼요", "익명1", 3, 5, emptyList(), "10월 28일 14:20"),
        BoardClass(2, "게시글 제목2", "내용2", "익명2", 5, 2, emptyList(), "10월 28일 15:00"),
        BoardClass(3, "게시글 제목3", "내용3", "익명3", 1, 8, emptyList(), "10월 28일 16:10")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBoardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = BoardAdapter(boardList) { board ->
            val fragment = BoardDetailFragment.newInstance(board.postId)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        binding.rvBoard.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBoard.adapter = adapter

        binding.ivSearchIcon.setOnClickListener {
            val intent = Intent(requireContext(), SearchPostActivity::class.java)
            startActivity(intent) // SearchPostActivity로 이동
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
