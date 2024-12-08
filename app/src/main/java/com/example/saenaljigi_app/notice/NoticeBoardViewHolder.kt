package com.example.saenaljigi_app.notice

import NoticeBoardData
import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.saenaljigi_app.R
import com.example.saenaljigi_app.board.BoardDetailFragment
import com.example.saenaljigi_app.databinding.ItemNoticeBoardBinding

class NoticeBoardViewHolder(
    private val binding: ItemNoticeBoardBinding,
    private val onItemClickListener: (Long) -> Unit,
    private val fragmentManager: FragmentManager
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(notice: NoticeBoardData, isLastItem: Boolean) {
        binding.tvTile.text = notice.title
        binding.tvCreatedAt.text = notice.date

        binding.root.setOnClickListener {
            val fragment = BoardDetailFragment.newInstance(notice.noticeId)

            fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
            onItemClickListener(notice.noticeId)
        }

        binding.imgLine.visibility = if (isLastItem) View.GONE else View.VISIBLE

        Log.d("NoticeBoardViewHolder", "Binding: Title=${notice.title}, CreatedAt=${notice.date}")
    }
}
