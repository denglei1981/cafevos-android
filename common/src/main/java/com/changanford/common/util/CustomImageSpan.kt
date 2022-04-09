package com.changanford.common.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ImageSpan
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.changanford.common.R
import com.changanford.common.bean.OrderItemBean
import com.changanford.common.bean.PayShowBean

class CustomImageSpan(drawable: Drawable?) : ImageSpan(drawable!!) {
    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        val fm = paint.fontMetricsInt
        val drawable = drawable

        val transY = (y + fm.descent + y + fm.ascent) / 2 - drawable.bounds.bottom / 2 + top+4
        canvas.save()
        canvas.translate(x, transY.toFloat())
        drawable.draw(canvas)
        canvas.restore()
    }
}

class CustomImageSpanV2(drawable: Drawable?) : ImageSpan(drawable!!) {
    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        val fm = paint.fontMetricsInt
        val drawable = drawable

        val transY = (y + fm.descent + y + fm.ascent) / 2 - drawable.bounds.bottom / 2 + top+2
        canvas.save()
        canvas.translate(x, transY.toFloat())
        drawable.draw(canvas)
        canvas.restore()
    }
}

fun showTotalTag(context:Context,text: AppCompatTextView?, item: PayShowBean,isStart:Boolean=true) {
    if (TextUtils.isEmpty(item.payFb)) {
        showZero(text, item)
        return
    }
    item.payFb?.let { // 福币为0
        if (it.toInt() <= 0) {
            showZero(text, item)
            return
        }
    }
    val fbNumber = item.payFb
    var starStr=""
    if(isStart){
         starStr = "合计: "
    }
    val str = if (TextUtils.isEmpty(item.payRmb)) {
        "$starStr[icon] ${item.payFb}"
    } else {
        "$starStr[icon] ${item.payFb}+￥${item.payRmb}"
    }
    //先设置原始文本
    text?.text = str
    //使用post方法，在TextView完成绘制流程后在消息队列中被调用
    text?.post { //获取第一行的宽度
        val stringBuilder: StringBuilder = StringBuilder(str)
        //SpannableString的构建
        val spannableString = SpannableString("$stringBuilder ")
        val drawable = ContextCompat.getDrawable(context, R.mipmap.question_fb)
        drawable?.apply {
            val imageSpan = CustomImageSpanV2(this)
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val strLength = spannableString.length
            val numberLength = fbNumber?.length
            val startIndex = strLength - numberLength!! - 1
            spannableString.setSpan(
                imageSpan, str.lastIndexOf("["), str.lastIndexOf("]") + 1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            text.text = spannableString
        }
    }
}

fun showZero(text: AppCompatTextView?, item: PayShowBean) {
    val tagName = item.payRmb
    //先设置原始文本
    text?.text = "合计".plus("  ￥${tagName}")
}