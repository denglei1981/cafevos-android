package com.changanford.circle.utils

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.changanford.circle.R

object MUtils {

    fun postDetailsFrom(textView: TextView,content:String) {

        val content1 = "来自"

        val spannable = SpannableString(content1 + content)

        spannable.setSpan(
            object : ClickableSpan() {
                override fun onClick(widget: View) {
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = ContextCompat.getColor(textView.context, R.color.circle_1B3B89)
                    ds.isUnderlineText = false //去除超链接的下划线
                }
            }, content1.length,
            spannable.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textView.text = spannable
        textView.movementMethod = LinkMovementMethod.getInstance()
    }

    fun setDrawableStar(textView: TextView,@DrawableRes resource:Int){
        val drawable: Drawable = ContextCompat.getDrawable(textView.context, resource)!!
        drawable.setBounds(
            0, 0, drawable.minimumWidth,
            drawable.minimumHeight
        )
        textView.setCompoundDrawables(drawable, null, null, null)
    }
}