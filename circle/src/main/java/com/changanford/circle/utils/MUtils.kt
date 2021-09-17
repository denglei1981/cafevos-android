package com.changanford.circle.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewTreeObserver
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

    /**
     * 设置textView结尾...后面显示的文字和颜色
     * @param context 上下文
     * @param textView textview
     * @param minLines 最少的行数
     * @param originText 原文本
     * @param endText 结尾文字
     * @param endColorID 结尾文字颜色id
     * @param isExpand 当前是否是展开状态
     */
    fun toggleEllipsize(
        context: Context,
        textView: TextView,
        minLines: Int,
        originText: String,
        endText: String,
        endColorID: Int,
        isExpand: Boolean
    ) {
        if (TextUtils.isEmpty(originText)) {
            return
        }
        textView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (isExpand) {
                    textView.text = originText
                } else {
                    val paddingLeft = textView.paddingLeft
                    val paddingRight = textView.paddingRight
                    val paint = textView.paint
                    val moreText = textView.textSize * endText.length
                    val availableTextWidth = (textView.width - paddingLeft - paddingRight) *
                            minLines - moreText
                    val ellipsizeStr: CharSequence = TextUtils.ellipsize(
                        originText, paint,
                        availableTextWidth, TextUtils.TruncateAt.END
                    )
                    if (ellipsizeStr.length < originText.length) {
                        val temp: CharSequence = ellipsizeStr.toString() + endText
                        val ssb = SpannableStringBuilder(temp)
                        ssb.setSpan(
                            ForegroundColorSpan(context.resources.getColor(endColorID)),
                            temp.length - endText.length,
                            temp.length,
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                        )
                        textView.text = ssb
                    } else {
                        textView.text = originText
                    }
                }
                textView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }
}