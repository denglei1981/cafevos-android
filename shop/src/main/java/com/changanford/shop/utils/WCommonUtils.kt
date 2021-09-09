package com.changanford.shop.utils

import android.content.Context
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayout

/**
 * @Author : wenke
 * @Time : 2021/9/8 0008
 * @Description : WCommonUtils
 */
object WCommonUtils {
    /**
     * 设置tabLayout选择样式
     * [size]被选择的字体大小
     * [typeface]字体样式 Typeface.DEFAULT_BOLD
     * [colorID]被选择的字体的颜色值
     * */
    fun setTabSelectStyle(context: Context, tabLayout: TabLayout, size: Float, typeface: Typeface, colorID: Int) {
        val tab= tabLayout.getTabAt(0)
        val textView = TextView(context)
        val selectedSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX,size,context.resources.displayMetrics)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, selectedSize)
        textView.setTextColor(ContextCompat.getColor(context, colorID))
        textView.typeface =typeface
        textView.text = tab!!.text
        textView.gravity= Gravity.CENTER
        tab.customView = textView
        tabLayout.addOnTabSelectedListener(object : TabLayout.BaseOnTabSelectedListener<TabLayout.Tab> {
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.customView = null
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                textView.text = tab!!.text
                tab.customView = textView
            }
        })
    }
}