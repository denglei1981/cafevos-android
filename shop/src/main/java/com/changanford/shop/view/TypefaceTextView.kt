package com.changanford.shop.view

import android.content.Context
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
    private var startText:String?=""
    private var endText:String?=""
    init {
        initTypefaceTextView(context, attrs)
        initView()
    }
    private fun initTypefaceTextView(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TypefaceTextView)
        //开头字体
        startText=typedArray.getString(R.styleable.TypefaceTextView_start_txt)
        endText=typedArray.getString(R.styleable.TypefaceTextView_end_txt)
        //字体
        val typeface: Typeface= when (val typefaceName = typedArray.getString(R.styleable.TypefaceTextView_typeface)) {
            "ZenDots-Regular" -> Typeface.createFromAsset(context.assets, "$typefaceName.ttf")
            else ->Typeface.DEFAULT
        }
        setTypeface(typeface)
        //行高
        setLineSpacing(0f,1.5f)
        typedArray.recycle()
    }
    private fun initView(){
        if(TextUtils.isEmpty(text)){
            val txt=context.getString(R.string.str_text)
            text=if(startText!=null&&endText!=null)"$startText$txt$endText"
            else if(startText!=null)"$startText$txt"
            else if(endText!=null)"$txt$endText"
            else txt
        }
    }
}