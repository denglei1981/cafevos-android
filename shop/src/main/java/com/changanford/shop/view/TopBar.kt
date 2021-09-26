package com.changanford.shop.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.changanford.shop.R
import com.luck.picture.lib.tools.ScreenUtils

/**
 * @Author : wenke
 * @Time : 2021/9/23
 * @Description : TopBar
 */
class TopBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    ConstraintLayout(context, attrs), View.OnClickListener {
    private var activity:Activity?=null
    private var backListener:OnBackClickListener?=null
    private var rightListener:OnRightClickListener?=null
    private lateinit var layoutHeader:ConstraintLayout
    private lateinit var imgBack:ImageView
    private lateinit var tvTitle:TypefaceTextView
    private lateinit var imgRight:ImageView
    private lateinit var rightTV:TypefaceTextView
    init {
        LayoutInflater.from(context).inflate(R.layout.view_topbar, this)
        initAttributes(context, attrs)
        initView()
    }
    @SuppressLint("CustomViewStyleable", "Recycle")
    private fun initAttributes(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TopBar)
        val bgColor=typedArray.getResourceId(R.styleable.TopBar_bg_color,R.color.transparent)
        val backIcon=typedArray.getResourceId(R.styleable.TopBar_back_icon,R.mipmap.shop_back_black)
        val rightIcon=typedArray.getResourceId(R.styleable.TopBar_right_icon,0)
        val titleText=typedArray.getString(R.styleable.TopBar_title_text)
        val titleColor=typedArray.getResourceId(R.styleable.TopBar_title_color,R.color.color_33)
        val rightTxt=typedArray.getString(R.styleable.TopBar_right_text)

        layoutHeader=findViewById(R.id.layout_topbar)
        imgBack=findViewById(R.id.img_back)
        tvTitle=findViewById(R.id.tv_title)
        imgRight=findViewById(R.id.img_right)
        rightTV=findViewById(R.id.tv_right)

        layoutHeader.setPadding(0,ScreenUtils.getStatusBarHeight(context)+10,0,ScreenUtils.dip2px(context,10f))
        layoutHeader.setBackgroundResource(bgColor)
        imgBack.setImageResource(backIcon)
        imgRight.setImageResource(rightIcon)
        tvTitle.text=titleText
        tvTitle.setTextColor(ContextCompat.getColor(context,titleColor))
        rightTV.text=rightTxt
    }

    private fun initView() {
        imgBack.setOnClickListener(this)
        imgRight.setOnClickListener(this)
        rightTV.setOnClickListener (this)
    }
    override fun onClick(v: View) {
        when(v.id){
            R.id.img_back->if(null==backListener)activity?.finish() else backListener?.onBackClick()
            R.id.img_right->rightListener?.onRightClick()
            R.id.tv_right->rightListener?.onRightClick()
        }
    }
    fun setActivity(activity:Activity){
        this.activity=activity
    }
    fun setOnBackClickListener(listener:OnBackClickListener?){
        this.backListener=listener
    }
    fun setOnRightClickListener(listener:OnRightClickListener?){
        this.rightListener=listener
    }
    interface OnBackClickListener {
        fun onBackClick()
    }

    interface OnRightClickListener {
        fun onRightClick()
    }
}