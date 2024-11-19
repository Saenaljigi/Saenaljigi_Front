package com.example.saenaljigi_app.board

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.saenaljigi_app.databinding.FragmentBoardDetailBinding

class BoardDetailFragment : Fragment() {

    private var _binding: FragmentBoardDetailBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_POST_ID = "postId"

        fun newInstance(postId: Long): BoardDetailFragment {
            return BoardDetailFragment().apply {
                arguments = Bundle().apply { putLong(ARG_POST_ID, postId) }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBoardDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val postId = arguments?.getLong(ARG_POST_ID) ?: return

        // 뒤로가기 버튼 동작
        binding.backBtn.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        val boardData = BoardClass(
            postId = 1,
            title = "배달 같이 시키실 분",
            content = "버거킹 먹고 싶은데 최소주문 금액이 안되는데 같이 시킬 사람 있나요? 버거킹 아니어도 돼요",
            anonymousName = "종이1",
            likeCount = 1,
            replyCount = 5,
            replies = listOf(
                CommentDto(
                    commentId = 1,
                    postId = 1,
                    content = "기숙사에서 배달 시켜도 되나요?",
                    anonymousName = "익명",
                    replyCount = 2,
                    replies = listOf(
                        CommentDto(
                            commentId = 2,
                            postId = 1,
                            content = "네",
                            anonymousName = "종이2",
                            replyCount = 0,
                            replies = emptyList(),
                            createdAt = "14:13"
                        ),
                        CommentDto(
                            commentId = 3,
                            postId = 1,
                            content = "원래 안되는데 그냥 봐주세요",
                            anonymousName = "종이3",
                            replyCount = 0,
                            replies = emptyList(),
                            createdAt = "14:14"
                        )
                    ),
                    createdAt = "14:13"
                ),
                CommentDto(
                    commentId = 4,
                    postId = 1,
                    content = "맥도날드는 어때요?",
                    anonymousName = "종이3",
                    replyCount = 1,
                    replies = listOf(
                        CommentDto(
                            commentId = 5,
                            postId = 1,
                            content = "좋아요!",
                            anonymousName = "작성자",
                            replyCount = 0,
                            replies = emptyList(),
                            createdAt = "14:17"
                        )
                    ),
                    createdAt = "14:15"
                )
            ),
            createdAt = "10월 28일 14:12"
        )


        binding.tvTitle.text = boardData.title
        binding.tvContent.text = boardData.content
        binding.tvLikeIcon.text = "${boardData.likeCount}개"
        binding.tvCommentIcon.text = "${boardData.replyCount}개"
        binding.tvCreatedAt.text = boardData.createdAt

        val answerAdapter = AnswerAdapter(boardData.replies)
        binding.rvAnswerContainer.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAnswerContainer.adapter = answerAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
