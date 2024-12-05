package com.example.saenaljigi_app.notice

import FileData
import androidx.recyclerview.widget.RecyclerView
import com.example.saenaljigi_app.databinding.ItemFileBinding

class FileViewHolder(
    private val binding: ItemFileBinding,
    private val onFileClick: (FileData) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(file: FileData) {
        binding.tvAttachedFile.text = file.fileName
        binding.root.setOnClickListener {
            onFileClick(file) // 파일 클릭 시 다운로드 또는 열기
        }
    }
}
