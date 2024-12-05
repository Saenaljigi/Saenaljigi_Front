package com.example.saenaljigi_app.notice

import FileData
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.saenaljigi_app.databinding.ItemFileBinding

class FileAdapter(
    private val fileList: List<FileData>,
    private val onFileClick: (FileData) -> Unit
) : RecyclerView.Adapter<FileViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val binding = ItemFileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FileViewHolder(binding, onFileClick)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        holder.bind(fileList[position])
    }

    override fun getItemCount(): Int = fileList.size
}
