package com.example.saenaljigi_app.board

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.saenaljigi_app.databinding.ItemBoardBinding

class PostResultsAdapter(
    private val posts: List<PostClass>,
    private val searchKeyword: String,
    private val onItemClick: (PostClass) -> Unit
) : RecyclerView.Adapter<PostResultsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostResultsViewHolder {
        val binding = ItemBoardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostResultsViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: PostResultsViewHolder, position: Int) {
        holder.bind(posts[position], searchKeyword)
    }

    override fun getItemCount(): Int = posts.size
}
