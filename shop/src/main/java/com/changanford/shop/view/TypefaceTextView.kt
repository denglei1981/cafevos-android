package com.changanford.shop.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Typeface
import android.text.TextUtils
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
        typedArray.recycle()
    }
    private fun initView(){

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //默认文字
        if(TextUtils.isEmpty(text))text = context.getString(R.string.str_text)
    }
}