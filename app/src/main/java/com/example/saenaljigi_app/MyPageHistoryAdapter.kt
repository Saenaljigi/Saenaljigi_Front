package com.example.saenaljigi_app

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.saenaljigi_app.databinding.ItemMypageHistoryBinding

class MyPageHistoryAdapter(val myPageHistoryData: ArrayList<MyPageHistoryData>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemMypageHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyPageHistoryViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return myPageHistoryData.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = myPageHistoryData[position]
        if (holder is MyPageHistoryViewHolder) {
            holder.bind(item)

            // 포지션에 따라 모서리 둥글림 설정
            val background = GradientDrawable()
            background.setColor(ContextCompat.getColor(holder.itemView.context, R.color.white)) // 배경색 설정

            // dp 값을 px 값으로 변환
            val cornerRadius = 20 * holder.itemView.resources.displayMetrics.density

            when {
                itemCount == 1 -> {
                    // 아이템이 1개일 때: 모든 모서리를 둥글게
                    background.cornerRadii = floatArrayOf(cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius)
                }
                position == 0 -> {
                    // 가장 첫 번째 아이템: 위쪽 모서리만 둥글게
                    background.cornerRadii = floatArrayOf(cornerRadius, cornerRadius, cornerRadius, cornerRadius, 0f, 0f, 0f, 0f)
                }
                position == itemCount - 1 -> {
                    // 가장 마지막 아이템: 아래쪽 모서리만 둥글게
                    background.cornerRadii = floatArrayOf(0f, 0f, 0f, 0f, cornerRadius, cornerRadius, cornerRadius, cornerRadius)
                }
                else -> {
                    // 중간 아이템: 둥글림 없음
                    background.cornerRadii = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
                }
            }

            // `mypage_history_box`에 배경 적용
            holder.mypageHistoryBox.background = background
        }
    }

    inner class MyPageHistoryViewHolder(private val binding: ItemMypageHistoryBinding): RecyclerView.ViewHolder(binding.root) {
        val date = binding.tvDate
        val title = binding.tvTitle
        val point = binding.tvPoint
        val total = binding.tvTotal

        val mypageHistoryBox = binding.mypageHistoryBox

        // MyPageHistoryData의 데이터를 각 뷰에 바인딩
        fun bind(item: MyPageHistoryData) {
            date.text = item.date
            title.text = item.title
            point.text = item.point.toString()
            total.text = item.total.toString()
        }
    }
}
