package com.example.saenaljigi_app.mypage

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class CustomDividerItemDecoration(private val divider: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        // 마지막 아이템이 아니라면 구분선 추가
        if (parent.getChildAdapterPosition(view) != parent.adapter?.itemCount?.minus(1)) {
            outRect.bottom = parent.context.resources.getDrawable(divider, null).intrinsicHeight
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val drawable = ContextCompat.getDrawable(parent.context, divider) ?: return
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight

        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(child)

            // 마지막 아이템이 아니라면 구분선 그리기
            if (position != parent.adapter?.itemCount?.minus(1)) {
                val params = child.layoutParams as RecyclerView.LayoutParams
                val top = child.bottom + params.bottomMargin
                val bottom = top + drawable.intrinsicHeight
                drawable.setBounds(left, top, right, bottom)
                drawable.draw(c)
            }
        }
    }
}

