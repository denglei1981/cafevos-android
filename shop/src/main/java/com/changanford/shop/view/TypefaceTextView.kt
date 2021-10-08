package com.changanford.shop.view

import android.content.Context
import android.text.InputType
import android.text.TextUtils
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.changanford.shop.R
import com.changanford.shop.utils.TypefaceUtils


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
        val inputType= inputType
        val typefaceValue=if(InputType.TYPE_CLASS_TEXT==inputType||InputType.TYPE_NULL==inputType) TypefaceUtils.getTypefaceTxt(context)
        else TypefaceUtils.getTypefaceNumber(context)
        typeface = typefaceValue
        //行高
        setLineSpacing(0f,1.5f)
        typedArray.recycle()
    }
    private fun initView(){
        if(TextUtils.isEmpty(text))setText(context.getString(R.string.str_text))
    }
    fun setText(str:Int?){
        setText("$str")
    }
    fun setText(str:Any?){
        setText("$str")
    }
    fun setText(str:String?){
        if(str==null)return
        text=if(startText!=null&&endText!=null) "$startText$str$endText"
        else if(startText!=null) "$startText$str"
        else if (endText != null) "$str$endText"
        else str
    }
}