package com.changanford.shop.view.recyclerview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.changanford.shop.R

/**
 * @Author : wenke
 * @Time : 2021/9/9
 * @Description : TitleBar
 */
class TitleBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayout(context, attrs, defStyleAttr), View.OnClickListener {


    init {
        initAttributes(context, attrs)
        LayoutInflater.from(context).inflate(R.layout.in_header_layout, this)
        initView()
    }
    private fun initAttributes(context: Context, attrs: AttributeSet?) {
        //获取自定义属性
        val ta = context.obtainStyledAttributes(attrs, R.styleable.TitleBar)
    }

    private fun initView() {

    }


    override fun onClick(v: View) {

    }

    interface OnBackClickListener {
        fun onBackClick()
    }

    interface OnRightClickListener {
        fun onRightClick()
    }
}