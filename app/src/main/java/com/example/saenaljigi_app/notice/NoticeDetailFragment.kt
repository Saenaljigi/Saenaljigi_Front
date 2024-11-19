package com.example.saenaljigi_app.notice

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.saenaljigi_app.databinding.FragmentNoticeDetailBinding

class NoticeDetailFragment : Fragment() {

    private var _binding: FragmentNoticeDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var fileAdapter: FileAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNoticeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val files = listOf(
            FileData("파일 1", "https://example.com/file1.pdf"),
            FileData("파일 2", "https://example.com/file2.pdf")
        )

        fileAdapter = FileAdapter(files) { fileData ->
            openFileUrl(fileData.fileUrl)
        }

        // 뒤로가기 버튼 동작
        binding.noticeBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.rvAttachedFiles.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = fileAdapter
        }

        binding.tvNoticeTitle.text = arguments?.getString("title")
        binding.tvNoticeContent.text = arguments?.getString("content")
        binding.tvNoticeWriter.text = "작성자: ${arguments?.getString("writer")}"
        binding.tvCreatedAt.text = arguments?.getString("created_at")
    }

    private fun openFileUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
