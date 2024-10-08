package com.example.saenaljigi_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

class MenuDetailAdapter(
    private val menuLists: List<List<String>>, // 각 페이지별 메뉴 리스트
    private val viewPager: ViewPager2, // ViewPager2를 어댑터에 전달
    private val applyBtn: Button
) : RecyclerView.Adapter<MenuDetailAdapter.MenuDetailViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MenuDetailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_menu_detail, parent, false)
        return MenuDetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuDetailViewHolder, position: Int) {
        // 메뉴 리스트를 한 줄씩 표시하도록 포멧
        val menuText = menuLists[position].joinToString(separator = "\n")
        holder.menuList.text = menuText

        // 메뉴 타입 설정
        val menuTypeText = when (position) {
            0 -> "조식"
            1 -> "중식"
            else -> "석식"
        }
        holder.menuType.text = menuTypeText

        // 버튼 가시성 설정 (조식 페이지: 뒤로 가기 버튼X, 석식 페이지: 앞으로 가기 버튼X)
        holder.prevBtn.visibility = if (position == 0) View.GONE else View.VISIBLE
        holder.nextBtn.visibility = if (position == itemCount - 1) View.GONE else View.VISIBLE

        // next_btn 클릭 시 다음 페이지로 이동
        holder.nextBtn.setOnClickListener {
            if (position + 1 < itemCount) {
                viewPager.currentItem = position + 1
            }
        }

        // prev_btn 클릭 시 이전 페이지로 이동
        holder.prevBtn.setOnClickListener {
            if (position - 1 >= 0) {
                viewPager.currentItem = position - 1
            }
        }
    }

    override fun getItemCount(): Int = menuLists.size

    inner class MenuDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val menuType: TextView = itemView.findViewById(R.id.tv_menu_type)
        val menuList: TextView = itemView.findViewById(R.id.tv_menu_list)
        val prevBtn: ImageView = itemView.findViewById(R.id.prev_btn)
        val nextBtn: ImageView = itemView.findViewById(R.id.next_btn)
    }
}
