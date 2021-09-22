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
        val textFlag=typedArray.getInt(R.styleable.TypefaceTextView_text_flags,0)
        if(0!=textFlag)paint.flags= textFlag
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
        if(TextUtils.isEmpty(text))setText(context.getString(R.string.str_text))
    }
    fun setText(str:String?){
        if(str==null)return
        text=if(startText!=null&&endText!=null) "$startText$str$endText"
        else if(startText!=null) "$startText$str"
        else if (endText != null) "$str$endText"
        else str
    }
}