package com.changanford.common.widget

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView


/**
 *Author lcw
 *Time on 2022/8/17
 *Purpose
 */
class RequiredTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    override fun setText(text: CharSequence?, type: BufferType?) {
        val span: Spannable = SpannableString("* $text")
        span.setSpan(ForegroundColorSpan(Color.RED), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        super.setText(span, type)

    }

}