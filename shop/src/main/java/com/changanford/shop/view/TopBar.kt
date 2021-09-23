package com.changanford.shop.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.changanford.shop.R

/**
 * @Author : wenke
 * @Time : 2021/9/9
 * @Description : TitleBar
 */
class TopBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    ConstraintLayout(context, attrs), View.OnClickListener {


    init {
        LayoutInflater.from(context).inflate(R.layout.view_topbar, this)
        initAttributes(context, attrs)
        initView()
    }
    @SuppressLint("CustomViewStyleable", "Recycle")
    private fun initAttributes(context: Context, attrs: AttributeSet?) {
        //获取自定义属性
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TopBar)
        val bgColor=typedArray.getResourceId(R.styleable.TopBar_bg_color,R.color.white)
        setBackgroundResource(bgColor)
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