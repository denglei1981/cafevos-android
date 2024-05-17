package com.changanford.shop.view

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.changanford.shop.R
import com.changanford.shop.utils.WCommonUtil


/**
 * @Author : wenke
 * @Time : 2021/9/15
 * @Description : TypefaceTextView
 */
class TypefaceTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    AppCompatTextView(context, attrs, defStyleAttr) {
    private var startText: String? = ""
    private var endText: String? = ""

    init {
        initTypefaceTextView(context, attrs)
        initView()
    }

    private fun initTypefaceTextView(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TypefaceTextView)
        //开头字体
        startText = typedArray.getString(R.styleable.TypefaceTextView_start_txt)
        endText = typedArray.getString(R.styleable.TypefaceTextView_end_txt)
        val intTxt = typedArray.getInt(R.styleable.TypefaceTextView_set_int_txt, -999999)
        if (intTxt != -999999) setText("$intTxt")
        val textFlag = typedArray.getInt(R.styleable.TypefaceTextView_text_flags, 0)
        if (0 != textFlag) paint.flags = textFlag
//        val inputType= inputType
//        val typefaceValue=if(InputType.TYPE_CLASS_TEXT==inputType||InputType.TYPE_NULL==inputType) TypefaceUtils.getTypefaceTxt(context)
//        else TypefaceUtils.getTypefaceNumber(context)
//        typeface = typefaceValue
        //行高
//        if(lineSpacingExtra==0.0f)setLineSpacing(0f,1.2f)
        typedArray.recycle()
    }

    private fun initView() {
        if (TextUtils.isEmpty(text)) setText(context.getString(R.string.str_text))
    }

    fun setHtmlTxt(str: Any?, color: String) {
        WCommonUtil.htmlToString(
            this,
            "${startText ?: ""}<font color=\"$color\">$str</font>${endText ?: ""}"
        )
    }

    fun setText(str: Int?) {
        setText(str ?: "0")
    }

    fun setText(str: Any?) {
        if (str != null) setText("$str")
    }

    fun setIntTxt(str: Int) {
        setText("$str")
    }

    @SuppressLint("SetTextI18n")
    fun setText(str: String?) {
        if (str == null) return
        text = "${startText ?: ""}$str${endText ?: ""}"
    }

    fun setEndTxt(endStr: String?) {
        this.endText = endStr
    }
}