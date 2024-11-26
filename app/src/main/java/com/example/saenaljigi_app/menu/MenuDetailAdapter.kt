package com.example.saenaljigi_app.menu

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.saenaljigi_app.R
import com.example.saenaljigi_app.RetrofitClient
import com.example.saenaljigi_app.menu.MenuApiService
import com.example.saenaljigi_app.menu.MenuDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuDetailAdapter(
    private val lunchMenuDto: MenuDto, // 중식 메뉴
    private val dinnerMenuDto: MenuDto, // 석식 메뉴
    private val viewPager: ViewPager2,
    private val token: String
) : RecyclerView.Adapter<MenuDetailAdapter.MenuDetailViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuDetailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_menu_detail, parent, false)
        return MenuDetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuDetailViewHolder, position: Int) {
        // position에 따라 중식/석식 데이터를 선택
        val menuDto = if (position == 0) lunchMenuDto else dinnerMenuDto

        // 메뉴 타입 설정
        holder.menuType.text = menuDto.foodTime

        // 메뉴 리스트에서 최대 6개 메뉴 가져오기
        val foodItems = menuDto.foods
        val textViews = listOf(
            holder.menu1, holder.menu2, holder.menu3,
            holder.menu4, holder.menu5, holder.menu6
        )

        // 각 TextView에 메뉴 아이템을 설정하고 클릭 리스너 추가
        textViews.forEachIndexed { index, textView ->
            if (index < foodItems.size) {
                val food = foodItems[index] // FoodDto 객체 가져오기
                textView.text = food.foodName
                textView.isClickable = true

                // isSelected가 true일 경우 배경색 변경
                textView.setBackgroundColor(
                    if (food.isSelected) Color.parseColor("#FFF86C") else Color.TRANSPARENT
                )

                // 클릭 리스너: 하이라이트 처리 및 서버 업데이트 호출
                textView.setOnClickListener {
                    val isHighlighted = textView.tag == true

                    // 모든 텍스트뷰의 배경색 초기화
                    textViews.forEach { it.setBackgroundColor(Color.TRANSPARENT); it.tag = false }

                    if (!isHighlighted) {
                        textView.setBackgroundColor(Color.parseColor("#FFF86C"))
                        textView.tag = true
                        // 하이라이트 상태 true로 업데이트
                        updateFoodSelection(food.menuId, true, food.foodName, token)
                    } else {
                        textView.tag = false
                        // 하이라이트 상태 false로 업데이트
                        updateFoodSelection(food.menuId, false, food.foodName, token)
                    }
                }
            } else {
                // 메뉴가 없으면 비우고 클릭 불가 처리
                textView.text = ""
                textView.isClickable = false
                textView.setBackgroundColor(Color.TRANSPARENT)
            }
        }

        // 이전/다음 버튼 가시성 설정
        holder.prevBtn.visibility = if (position == 0) View.GONE else View.VISIBLE
        holder.nextBtn.visibility = if (position == itemCount - 1) View.GONE else View.VISIBLE

        // 버튼 클릭 리스너
        holder.prevBtn.setOnClickListener {
            if (position > 0) viewPager.currentItem = position - 1
        }
        holder.nextBtn.setOnClickListener {
            if (position < itemCount - 1) viewPager.currentItem = position + 1
        }
    }

    override fun getItemCount(): Int = 2 // "중식"과 "석식" 두 개의 페이지만 존재

    private fun updateFoodSelection(menuId: Long, isSelected: Boolean, foodId: String, token: String) {

        val menuService = RetrofitClient.instance.create(MenuApiService::class.java)
        val call = menuService.updateHighlightedMenu("Bearer $token", menuId, isSelected, foodId)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    println("Food selection updated successfully!")
                } else {
                    println("Failed to update food selection. Error code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                println("Error updating food selection: ${t.message}")
            }
        })
    }

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

