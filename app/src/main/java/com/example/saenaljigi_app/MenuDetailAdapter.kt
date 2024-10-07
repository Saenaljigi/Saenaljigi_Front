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
    private val menuList: List<String>,
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
        holder.menuList.text = menuList[position]

        // 조식
        if (position == 0) {
            holder.menuType.text = "조식"
            holder.prevBtn.visibility = View.GONE // prev_btn 숨김
            holder.nextBtn.visibility = View.VISIBLE // next_btn 표시

            applyBtn.visibility = View.VISIBLE // 조식에서는 신청하기 버튼 표시
        }
        // 중식
        else if (position == 1) {
            holder.menuType.text = "중식"
            holder.prevBtn.visibility = View.VISIBLE // prev_btn 표시
            holder.nextBtn.visibility = View.VISIBLE // next_btn 표시

            applyBtn.visibility = View.GONE // 중식과 석식은 신청하기 버튼 X
        }
        // 석식
        else if (position == 2) {
            holder.menuType.text = "석식"
            holder.prevBtn.visibility = View.VISIBLE // prev_btn 표시
            holder.nextBtn.visibility = View.GONE // next_btn 숨김

            applyBtn.visibility = View.GONE
        }

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

    override fun getItemCount(): Int = menuList.size

    inner class MenuDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val menuType: TextView = itemView.findViewById(R.id.tv_menu_type)
        val menuList: TextView = itemView.findViewById(R.id.tv_menu_list)
        val prevBtn: ImageView = itemView.findViewById(R.id.prev_btn)
        val nextBtn: ImageView = itemView.findViewById(R.id.next_btn)
    }
}

