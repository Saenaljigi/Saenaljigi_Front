package com.example.saenaljigi_app.menu

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.saenaljigi_app.R

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
        // 메뉴 타입 설정
        val menuTypeText = when (position) {
            0 -> "조식"
            1 -> "중식"
            else -> "석식"
        }
        holder.menuType.text = menuTypeText

        // 메뉴 리스트에서 최대 6개 메뉴 가져오기
        val menuItems = menuLists[position]
        val textViews = listOf(
            holder.menu1, holder.menu2, holder.menu3,
            holder.menu4, holder.menu5, holder.menu6
        )

        // 각 TextView에 메뉴 아이템을 할당하고, 메뉴가 없는 경우 비워두고 클릭 불가 처리
        for (i in textViews.indices) {
            textViews[i].text = if (i < menuItems.size) menuItems[i] else ""
            textViews[i].isClickable = textViews[i].text.isNotEmpty() // 빈 텍스트뷰는 클릭 불가

            // 클릭 리스너 설정 (클릭 시 노란색으로 하이라이트)
            textViews[i].setOnClickListener {
                if (textViews[i].text.isNotEmpty()) {
                    textViews[i].setBackgroundColor(Color.parseColor("#FFF86C"))

                }
            }
        }

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
        val menu1: TextView = itemView.findViewById(R.id.tv_menu1)
        val menu2: TextView = itemView.findViewById(R.id.tv_menu2)
        val menu3: TextView = itemView.findViewById(R.id.tv_menu3)
        val menu4: TextView = itemView.findViewById(R.id.tv_menu4)
        val menu5: TextView = itemView.findViewById(R.id.tv_menu5)
        val menu6: TextView = itemView.findViewById(R.id.tv_menu6)
        val prevBtn: ImageView = itemView.findViewById(R.id.prev_btn)
        val nextBtn: ImageView = itemView.findViewById(R.id.next_btn)
    }
}