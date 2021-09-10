package com.changanford.shop.control

import android.content.Context
import androidx.core.widget.NestedScrollView
import com.changanford.shop.R
import com.changanford.shop.databinding.ActGoodsDetailsBinding
import kotlin.math.roundToInt

/**
 * @Author : wenke
 * @Time : 2021/9/10 0010
 * @Description : GoodsDetailsControl
 */
class GoodsDetailsControl(context:Context,val binding: ActGoodsDetailsBinding) {
    private val commentH =binding.inTop.tvComment.y -binding.inTop.tvComment.height
    private val detailsH =binding.inTop.recycler.y
    private val tabLayout =binding.tabLayout
    private val tabTitles =arrayOf(context.getString(R.string.str_goods), context.getString(R.string.str_comment),context.getString(R.string.str_details))
    init {
        with(binding) { nScrollView.setOnScrollChangeListener(onScrollChangeListener) }
        for(it in tabTitles){
            tabLayout.addTab(tabLayout.newTab().setText(it))
        }
    }
    private val onScrollChangeListener= NestedScrollView.OnScrollChangeListener { _, _, _, _, oldScrollY ->
        val headerBg=tabLayout.background
        when {
            oldScrollY <= 100 -> {
                headerBg.alpha=0
                tabLayout.alpha=0f
            }
            oldScrollY < commentH -> {
                val alpha = (oldScrollY / commentH * 255).roundToInt()
                headerBg.alpha=alpha
                val tabAlpha=oldScrollY / commentH * 1.0f
                tabLayout.alpha=tabAlpha
            }
            oldScrollY >= commentH ->{
                headerBg.alpha=255
                tabLayout.alpha=1f
            }
        }
    }
}