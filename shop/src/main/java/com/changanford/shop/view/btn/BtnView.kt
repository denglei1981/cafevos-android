package com.changanford.shop.view.btn

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import com.changanford.shop.R

/**
 * @Author : wenke
 * @Time : 2021/9/23 0023
 * @Description : KillBtnView
 */
class BtnView(context:Context, attrs: AttributeSet? = null):AppCompatButton(context,attrs) {
    init {
        initAttributes(context, attrs)
    }
    @SuppressLint("Recycle", "CustomViewStyleable")
    private fun initAttributes(context: Context, attrs: AttributeSet?) {
        //获取自定义属性
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomBtn)
//        typeface=Typeface.createFromAsset(context.assets, "MHeiPRC-Medium.OTF")
        setBtnEnabled(isEnabled)
    }
    fun setBtnEnabled(isBtnEnabled:Boolean){
        isEnabled=isBtnEnabled
        if(isBtnEnabled)setBackgroundResource(R.drawable.btn_selector)
        else setBackgroundResource(R.drawable.btn_disable_selector)
    }

}