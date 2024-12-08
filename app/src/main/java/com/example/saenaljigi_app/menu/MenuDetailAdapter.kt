package com.example.saenaljigi_app.menu

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.saenaljigi_app.R
import com.example.saenaljigi_app.RetrofitClient
import com.example.saenaljigi_app.menu.MenuDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuDetailAdapter(
    private val breakfast: List<String>, // 조식
    private val lunchMenuDto: MenuDto, // 중식 메뉴
    private val dinnerMenuDto: MenuDto, // 석식 메뉴
    private val calendarDto: CalendarDto,
    private val viewPager: ViewPager2,
    private val context: Context,
    private val onPageChangeCallback: (position: Int) -> Unit
) : RecyclerView.Adapter<MenuDetailAdapter.MenuDetailViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuDetailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_menu_detail, parent, false)
        return MenuDetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuDetailViewHolder, position: Int) {
        val menuDto = when (position) {
            0 -> {
                holder.menuType.text = "조식"
                null
            }
            1 -> {
                holder.menuType.text = lunchMenuDto.foodTime
                lunchMenuDto
            }
            else -> {
                holder.menuType.text = dinnerMenuDto.foodTime
                dinnerMenuDto
            }
        }

        if (menuDto == null) {
            val foodItems = breakfast
            val textViews = listOf(holder.menu1, holder.menu2, holder.menu3, holder.menu4, holder.menu5, holder.menu6)

            textViews.forEachIndexed { index, textView ->
                if (index < foodItems.size) {
                    textView.text = foodItems[index]
                    textView.isClickable = false
                    textView.setBackgroundColor(Color.TRANSPARENT)
                } else {
                    textView.text = ""
                    textView.isClickable = false
                    textView.setBackgroundColor(Color.TRANSPARENT)
                }
            }
        } else {
            val foodItems = menuDto.foods
            val textViews = listOf(holder.menu1, holder.menu2, holder.menu3, holder.menu4, holder.menu5, holder.menu6)

            textViews.forEachIndexed { index, textView ->
                if (index < foodItems.size) {
                    val food = foodItems[index]
                    textView.text = food.foodName
                    textView.isClickable = true
                    textView.setBackgroundColor(
                        if (food.selected) Color.parseColor("#FFF86C") else Color.TRANSPARENT
                    )

                    textView.setOnClickListener {
                        val isHighlighted = textView.tag == true
                        textViews.forEach { it.setBackgroundColor(Color.TRANSPARENT); it.tag = false }
                        if (!isHighlighted) {
                            textView.setBackgroundColor(Color.parseColor("#FFF86C"))
                            textView.tag = true
                            updateFoodSelection(food.menuId, true, food.foodName)
                        } else {
                            textView.tag = false
                            updateFoodSelection(food.menuId, false, food.foodName)
                        }
                    }
                } else {
                    textView.text = ""
                    textView.isClickable = false
                    textView.setBackgroundColor(Color.TRANSPARENT)
                }
            }
        }

        holder.prevBtn.visibility = if (position == 0) View.GONE else View.VISIBLE
        holder.nextBtn.visibility = if (position == itemCount - 1) View.GONE else View.VISIBLE

        holder.prevBtn.setOnClickListener {
            if (position > 0) {
                viewPager.currentItem = position - 1
                onPageChangeCallback(position - 1)
            }
        }
        holder.nextBtn.setOnClickListener {
            if (position < itemCount - 1) {
                viewPager.currentItem = position + 1
                onPageChangeCallback(position + 1)
            }
        }
    }

    override fun getItemCount(): Int = 3

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

    private fun updateFoodSelection(menuId: Long, isSelected: Boolean, foodname: String) {
        val token = getJwtToken()

        val menuService = RetrofitClient.instance.create(MenuApiService::class.java)
        val call = menuService.updateHighlightedMenu(token, menuId, isSelected, foodname)
        Log.d("MenuDetail_A", "$menuId, $isSelected, $foodname")

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("MenuDetail_A", "Success")
                } else {
                    Log.e("MenuDetail_A", "Response Code: ${response.code()}, Message: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("MenuDetail_A", "Error updating food selection: ${t.message}")
            }
        })
    }

    // 조식 신청
    private fun applyForBreakfast(token: String, calendar: CalendarDto) {
        val calendarId = calendar.id

        // Retrofit API 서비스 인스턴스 생성
        val menuService = RetrofitClient.instance.create(MenuApiService::class.java)
        val call = menuService.applyForBreakfast(
            token = token,
            calendarId = calendarId,
            isBreakfast = true
        )

        // 요청 비동기 처리
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // 성공적인 응답 처리
                    Toast.makeText(context, "조식 신청이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                    Log.d("applyForBreakfast", "조식 신청 성공")
                } else {
                    // 실패 응답 처리
                    val errorMessage = "조식 신청에 실패했습니다. (${response.code()})"
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    Log.e("applyForBreakfast", "Response Code: ${response.code()}, Message: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // 네트워크 오류 등 요청 실패 처리
                val errorMessage = "조식 신청 요청 중 오류가 발생했습니다: ${t.message}"
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                Log.e("applyForBreakfast", "Error: ${t.message}")
            }
        })
    }

    // SharedPreferences에 데이터를 저장하는 함수
    fun saveMealToPreferences(context: Context, date: String, foodType: String) {
        // SharedPreferences 인스턴스 가져오기
        val sharedPreferences = context.getSharedPreferences("MenuPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // 현재 저장된 데이터를 불러오기
        val currentData = getSavedMealsFromPreferences(context)

        // 새로운 메뉴 데이터를 추가
        val newMeal = mapOf("date" to date, "foodType" to foodType)
        val updatedList = currentData.toMutableList().apply { add(newMeal) }

        // 리스트를 JSON 문자열로 변환하여 저장
        val json = Gson().toJson(updatedList)
        editor.putString("mealDataList", json)
        editor.apply() // 저장
    }

    // SharedPreferences에서 기존 데이터를 불러오는 함수
    fun getSavedMealsFromPreferences(context: Context): List<Map<String, String>> {
        val sharedPreferences = context.getSharedPreferences("MenuPrefs", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("mealDataList", "") ?: ""

        // Gson을 이용해 JSON을 리스트로 변환
        val listType = object : TypeToken<List<Map<String, String>>>() {}.type
        return Gson().fromJson(json, listType) ?: emptyList()
    }

    private fun getJwtToken(): String {
        // JWT 토큰 가져오기
        val sharedPref = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
        return sharedPref.getString("jwt_token", "") ?: ""
    }
}