package com.changanford.shop.view

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.changanford.shop.R


/**
 * @Author : wenke
 * @Time : 2021/9/15
 * @Description : TypefaceTextView
 */
class TypefaceTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    AppCompatTextView(context, attrs, defStyleAttr) {
    init {
        initTypefaceTextView(context, attrs)
        initView()
    }
    private fun initTypefaceTextView(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TypefaceTextView)
        //字体
        val typefaceName = typedArray.getString(R.styleable.TypefaceTextView_typeface) ?: return
        val typeface: Typeface= when (typefaceName) {
            "ZenDots-Regular" -> Typeface.createFromAsset(context.assets, "$typefaceName.ttf")
            else ->Typeface.DEFAULT
        }
        setTypeface(typeface)
        //行高
        setLineSpacing(0f,1.5f)
        //默认文字
        setText("00000111111111")
        typedArray.recycle()
    }
    private fun initView(){

    }

}